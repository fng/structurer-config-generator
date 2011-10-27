package com.github.fng.structurer.ui

import chart.PayoffChartCreator
import instrument._
import org.jfree.chart.ChartPanel
import swing._
import event.ButtonClicked
import com.efgfp.commons.spring.resource.ResourceLoader
import com.github.fng.structurer.instrument._
import org.springframework.core.io.support.{ResourcePatternResolver, ResourcePatternUtils}
import org.springframework.core.io.{Resource, DefaultResourceLoader, ClassPathResource}
import com.github.fng.structurer.config.{FieldConfig, ProductConfig}
import javax.swing.table.AbstractTableModel
import javax.swing.{BorderFactory, JPanel}


object Structurer extends SimpleSwingApplication with PayoffSamples with LoadableConfigurations {

  val payoffChartCreator = new PayoffChartCreator


  def top = new MainFrame {

    title = "Structurer"

//    val framewidth = 600
//    val frameheight = 600
//    val screenSize = java.awt.Toolkit.getDefaultToolkit.getScreenSize
//    location = new java.awt.Point((screenSize.width - framewidth) / 2, (screenSize.height - frameheight) / 2)
//    minimumSize = new java.awt.Dimension(framewidth, frameheight)


    val addOptionMenu = new MenuItem("Option")
    val addBondMenu = new MenuItem("Bond")


    menuBar = new MenuBar {
      contents += new Menu("File") {
        contents += new MenuItem(Action("Quit") {
          System.exit(0)
        })
      }

      contents += new Menu("Add") {
        contents += addOptionMenu
        contents += addBondMenu
      }

      contents += new Menu("Samples") {
        contents ++= payoffSamples
      }

      contents += new Menu("Config") {
        contents ++= loadableConfigurations
      }


    }

    val drawButton = new Button("Draw")

    val packagePanel = new PackagePanel

    val fieldPanel = new FieldPanel

    val options = List(ExpressionOption(OptionType.Call, 1.0, -10, 100, OptionBarrierType.KnockInBarrier),
      ExpressionOption(OptionType.Call, 1.0, -10, 100, OptionBarrierType.KnockInBarrier))

    val bonds = List(ExpressionBond(1000, 1))


    val optionTable = new OptionTable(options.map(MutableOption(_)))
    val bondTable = new BondTable(bonds.map(MutableBond(_)))

    val fieldTable = new FieldTable(List(
      MutableField("CAP", FieldType.NumberField, ConstraintType.GreaterThan, "100", "", ""),
      MutableField("DUMMY", FieldType.NumberRangeField, ConstraintType.GreaterThan, "", "50;80", ""),
      MutableField("COUPON FREQUENCY", FieldType.ChooseField, ConstraintType.OneOf, "", "", "annually,semi-annually,quarterly,monthly")
    ))

    val chartPanel = new BorderPanel {

      updateChart()

      def updateChart() {
        add(new Panel {
          override lazy val peer = createPayoffChart(optionTable.getOptions, bondTable.getBonds, fieldPanel)
        }, BorderPanel.Position.Center)
      }

    }

    def updateChartPanel() {
      chartPanel.updateChart()
    }


    payoffSamples.foreach(listenTo(_))

    loadableConfigurations.foreach(listenTo(_))

    listenTo(drawButton, addOptionMenu, addBondMenu)


    reactions += {
      case ButtonClicked(`drawButton`) =>
        chartPanel.updateChart()
        mainPanel.revalidate()
      //      case InstrumentPanel.PanelEvent.RemovePanelEvent(panel) =>
      //        packagePanel.instrumentPanel.contents -= panel
      //        packagePanel.instrumentPanel.revalidate()
      //        splitPane.revalidate()
      //        optionTable.removeOne
      //        bondTable.removeOne
      case ButtonClicked(`addOptionMenu`) =>
        optionTable.add(MutableOption(ExpressionOption(OptionType.Call, 0.0, 10, 100, OptionBarrierType.NoBarrier)))
      case ButtonClicked(`addBondMenu`) =>
        bondTable.add(MutableBond(ExpressionBond(1000, 1)))
      case ButtonClicked(config) if config.isInstanceOf[LoadableConfigMenuItem] =>
        dialogSave {
          loadFromConfig(config.asInstanceOf[LoadableConfigMenuItem].resource)
        }
      case ButtonClicked(sample) if sample.isInstanceOf[SampleMenuItem] =>
        dialogSave {
          refreshPackagePanelWithNew(sample.asInstanceOf[SampleMenuItem].packageInstrument, Nil)
        }

    }

    def dialogSave(ifOkFunction: => Unit) {
      val result = Dialog.showOptions(message = "Show sample / load config and loose all data?", optionType = Dialog.Options.YesNo, initial = 0,
        entries = Seq("do it", "no Way"))
      if (result == Dialog.Result.Ok) {
        ifOkFunction
      }
    }


    def refreshPackagePanelWithNew(packageInstrument: PackageInstrument, fields: List[FieldConfig]) {
      val expressionOptions = packageInstrument.components.collect({
        case eo: ExpressionOption => eo
        case oi: OptionInstrument => ExpressionOption(oi)
      })

      optionTable.updateWithNewList(expressionOptions.map(MutableOption(_)))

      val expressionBonds = packageInstrument.components.collect({
        case eb: ExpressionBond => eb
        case bi: BondInstrument => ExpressionBond(bi)
      })

      bondTable.updateWithNewList(expressionBonds.map(MutableBond(_)))

      packagePanel.update(packageInstrument)
      fieldPanel.refreshFieldPanel(fields)
      mainPanel.revalidate()
      chartPanel.updateChart()


    }

    val mainPanel = new MigLayoutPanel(colConstraints = "[grow, fill]", rowConstraints = "[50][100][100][100][][grow, fill]") {
      wrap(packagePanel)
      wrap(new ScrollPane(optionTable) {
        border = BorderFactory.createTitledBorder("Options")
      })
      wrap(new ScrollPane(bondTable) {
        border = BorderFactory.createTitledBorder("Bonds")
      })
      wrap(new ScrollPane(fieldTable) {
        border = BorderFactory.createTitledBorder("Fields")
      })
      wrap(drawButton)
      add(new SplitPane(Orientation.Vertical,
        new BorderPanel {
          add(fieldPanel, BorderPanel.Position.North)
        },
        chartPanel) {
        dividerLocation = 200
        dividerSize = 8
        oneTouchExpandable = true
      })

    }

    contents = mainPanel

    def loadFromConfig(resource: Resource) {

      val productConfig = ProductConfig(ResourceLoader.loadStringResourceUtf8(resource))

      val productTypeId = productConfig.productTypeId
      val payoffType = productConfig.payoffType match {
        case "Bullish" => PayoffType.Bullish
        case "Bearish" => PayoffType.Bearish
      }
      val denomination = 1000
      val quotationType = productConfig.quotationType match {
        case "Notional" => QuotationType.Notional
        case "Unit" => QuotationType.Unit
      }

      val options = productConfig.options.map {
        option => {
          val optionType = option.optionType match {
            case "Call" => OptionType.Call
            case "Put" => OptionType.Put
          }
          val strike = option.strike
          val quantity = option.quantity
          val notional = option.notional
          val barrierType = Option(option.barrier).map(_.barrierType) match {
            case Some("DownIn") => OptionBarrierType.KnockInBarrier
            case None => OptionBarrierType.NoBarrier
          }
          ExpressionOption(optionType, strike, quantity, notional, barrierType)
        }
      }

      val bonds = productConfig.bonds.map {
        bond => {
          val notional = bond.notional
          val quantity = bond.quantity
          ExpressionBond(notional, quantity)
        }
      }

      val packageInstrument = PackageInstrument(productTypeId, payoffType, denomination, quotationType, bonds ::: options)
      refreshPackagePanelWithNew(packageInstrument, productConfig.fields)
    }


  }

  def createPayoffChart(options: List[ExpressionOption], bonds: List[ExpressionBond], fieldPanel: FieldPanel): ChartPanel = {

    val variableValues = fieldPanel.getFieldsWithValues

    val optionInstruments = options.map {
      expressionOption => OptionInstrument(expressionOption.optionType,
        expressionOption.strike.evaluate(variableValues).doubleValue(),
        expressionOption.quantity.evaluate(variableValues).doubleValue(),
        expressionOption.notional.evaluate(variableValues).doubleValue(),
        expressionOption.optionBarrierType
      )
    }

    val bondInstruments = bonds.map {
      expressionBond => BondInstrument(
        expressionBond.notional.evaluate(variableValues).doubleValue(),
        expressionBond.quantity.evaluate(variableValues).doubleValue()
      )
    }


    val payoffChart = payoffChartCreator.createPayoffChart(optionInstruments, bondInstruments)
    payoffChart
  }


}