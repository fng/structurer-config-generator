package com.github.fng.structurer.ui

import chart.PayoffChartCreator
import instrument._
import org.jfree.chart.ChartPanel
import swing._
import event.ButtonClicked
import javax.swing.JPanel
import com.efgfp.commons.spring.resource.ResourceLoader
import com.github.fng.structurer.instrument._
import org.springframework.core.io.support.{ResourcePatternResolver, ResourcePatternUtils}
import org.springframework.core.io.{Resource, DefaultResourceLoader, ClassPathResource}
import com.github.fng.structurer.config.{FieldConfig, ProductConfig}

object Structurer extends SimpleSwingApplication with PayoffSamples with LoadableConfigurations {

  val payoffChartCreator = new PayoffChartCreator


  def top = new MainFrame {

    title = "Structurer"

    val framewidth = 600
    val frameheight = 600
    val screenSize = java.awt.Toolkit.getDefaultToolkit.getScreenSize
    location = new java.awt.Point((screenSize.width - framewidth) / 2, (screenSize.height - frameheight) / 2)
    minimumSize = new java.awt.Dimension(framewidth, frameheight)


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


    val chartPanel = new BorderPanel {

      updateChart()

      def updateChart() {
        add(new Panel {
          override lazy val peer = payoffChartFormInstrumentPanel(packagePanel.instrumentPanel)
        }, BorderPanel.Position.Center)
      }

    }

    def updateChartPanel() {
      chartPanel.updateChart()
    }


    payoffSamples.foreach(listenTo(_))

    loadableConfigurations.foreach(listenTo(_))

    packagePanel.instrumentPanel.contents.collect({
      case p: Publisher => p
    }).foreach(listenTo(_))

    listenTo(drawButton, addOptionMenu, addBondMenu)


    reactions += {
      case ButtonClicked(`drawButton`) =>
        chartPanel.updateChart()
      case InstrumentPanel.PanelEvent.RemovePanelEvent(panel) =>
        packagePanel.instrumentPanel.contents -= panel
        packagePanel.instrumentPanel.revalidate()
        splitPane.revalidate()
      case ButtonClicked(`addOptionMenu`) =>
        val newOptionPanel = new OptionPanel
        packagePanel.instrumentPanel.contents += newOptionPanel
        packagePanel.instrumentPanel.revalidate()
        splitPane.revalidate()
        listenTo(newOptionPanel)
      case ButtonClicked(`addBondMenu`) =>
        val newBondPanel = new BondPanel
        packagePanel.instrumentPanel.contents += newBondPanel
        packagePanel.instrumentPanel.revalidate()
        splitPane.revalidate()
        listenTo(newBondPanel)
      case ButtonClicked(config) if config.isInstanceOf[LoadableConfigMenuItem] =>
        dialogSave {
          loadFromConfig(config.asInstanceOf[LoadableConfigMenuItem].resource)
        }
      case ButtonClicked(sample) if sample.isInstanceOf[SampleMenuItem] =>
        dialogSave {
          refreshPackagePanelWithNew(sample.asInstanceOf[SampleMenuItem].packageInstrument)
        }

    }

    def dialogSave(ifOkFunction: => Unit) {
      val result = Dialog.showOptions(message = "Show sample / load config and loose all data?", optionType = Dialog.Options.YesNo, initial = 0,
        entries = Seq("do it", "no Way"))
      if (result == Dialog.Result.Ok) {
        ifOkFunction
      }
    }


    def refreshPackagePanelWithNew(packageInstrument: PackageInstrument) {
      packagePanel.update(packageInstrument)
      splitPane.revalidate()
      chartPanel.updateChart()
    }


    val fieldPanel = new FieldPanel

    val splitPane = new SplitPane(Orientation.Horizontal,
      new BorderPanel {
        add(packagePanel, BorderPanel.Position.North)
        add(drawButton, BorderPanel.Position.South)
      }
      , new SplitPane(Orientation.Vertical,
        new BorderPanel {
          add(fieldPanel, BorderPanel.Position.North)
        },
        chartPanel) {
        dividerSize = 8
        oneTouchExpandable = true
      }) {
      //dividerLocation = 230
      dividerSize = 8
      oneTouchExpandable = true
    }
    contents = splitPane

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
          val strike = option.strike.evaluate().doubleValue()
          val quantity = option.quantity.evaluate().doubleValue()
          val notional = option.notional.evaluate().doubleValue()
          val barrierType = Option(option.barrier).map(_.barrierType) match {
            case Some("DownIn") => OptionBarrierType.KnockInBarrier
            case None => OptionBarrierType.NoBarrier
          }
          OptionInstrument(optionType, strike, quantity, notional, barrierType)
        }
      }

      val bonds = productConfig.bonds.map {
        bond => {
          val notional = bond.notional.toDouble
          val quantity = bond.quantity.toDouble
          BondInstrument(notional, quantity)
        }
      }

      val packageInstrument = PackageInstrument(productTypeId, payoffType, denomination, quotationType, bonds ::: options)
      refreshPackagePanelWithNew(packageInstrument)
      fieldPanel.refreshFieldPanel(productConfig.fields)
    }


  }

  def payoffChartFormInstrumentPanel(instrumentPanel: BoxPanel): ChartPanel = {
    val options = instrumentPanel.contents.collect({
      case o: OptionPanel => o
    }).map(_.optionInstrument).toList

    val bonds = instrumentPanel.contents.collect({
      case o: BondPanel => o
    }).map(_.bondInstrument).toList

    val payoffChart = payoffChartCreator.createPayoffChart(options, bonds)
    payoffChart
  }


}