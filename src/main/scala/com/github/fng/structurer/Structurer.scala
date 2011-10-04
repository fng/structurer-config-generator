package com.github.fng.structurer

import chart.PayoffChartCreator
import payoff._
import org.jfree.chart.ChartPanel
import javax.swing.JPanel
import collection.mutable.ListBuffer
import view._
import swing._
import event.ButtonClicked

object Structurer extends SimpleSwingApplication {

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

    val samples = List(new SampleMenuItem("Reverse Convertible",
      BondInstrument(1000, 1),
      OptionInstrument(OptionType.Put, 1.0, -1000, OptionBarrierType.NoBarrier)),
      new SampleMenuItem("Outperformance Certificate",
        OptionInstrument(OptionType.Call, 0, 100, OptionBarrierType.NoBarrier),
        OptionInstrument(OptionType.Call, 1.0, 50, OptionBarrierType.NoBarrier)),
      new SampleMenuItem("Capped Outperformance Certificate",
        OptionInstrument(OptionType.Call, 0, 100, OptionBarrierType.NoBarrier),
        OptionInstrument(OptionType.Call, 1.0, 50, OptionBarrierType.NoBarrier),
        OptionInstrument(OptionType.Call, 1.5, -150, OptionBarrierType.NoBarrier))
    )

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
        contents ++= samples
      }


    }

    val drawButton = new Button("Draw")

    val instrumentPanel = new BoxPanel(Orientation.Horizontal) {
      contents ++= ListBuffer[InstrumentPanel](new OptionPanel)
    }

    val chartPanel = new Panel {
      override lazy val peer = payoffChartFormInstrumentPanel(instrumentPanel)
    }

    samples.foreach(listenTo(_))

    instrumentPanel.contents.collect({
      case p: Publisher => p
    }).foreach(listenTo(_))

    listenTo(drawButton, addOptionMenu, addBondMenu)


    reactions += {
      case ButtonClicked(`drawButton`) =>
        reDrawChart
      case InstrumentPanel.PanelEvent.RemovePanelEvent(panel) =>
        instrumentPanel.contents -= panel
        instrumentPanel.revalidate()
        splitPane.revalidate()
      case ButtonClicked(`addOptionMenu`) =>
        val newOptionPanel = new OptionPanel
        instrumentPanel.contents += newOptionPanel
        instrumentPanel.revalidate()
        splitPane.revalidate()
        listenTo(newOptionPanel)
      case ButtonClicked(`addBondMenu`) =>
        val newBondPanel = new BondPanel
        instrumentPanel.contents += newBondPanel
        instrumentPanel.revalidate()
        splitPane.revalidate()
        listenTo(newBondPanel)
      case ButtonClicked(sample) if sample.isInstanceOf[SampleMenuItem] =>
        dialogSave {
          refreshInstrumentPanelWithNew(sample.asInstanceOf[SampleMenuItem].asInstrumentPanels: _*)
        }

    }

    def dialogSave(ifOkFunction: => Unit) {
      val result = Dialog.showOptions(message = "Show sample and loose all data?", optionType = Dialog.Options.YesNo, initial = 0,
        entries = Seq("do it", "no Way"))
      if (result == Dialog.Result.Ok) {
        ifOkFunction
      }
    }

    def reDrawChart() {
      splitPane.contents_=(splitPane.leftComponent, new Panel {
        override lazy val peer: JPanel = payoffChartFormInstrumentPanel(instrumentPanel)
      })
    }


    def refreshInstrumentPanelWithNew(instrumentPanels: InstrumentPanel*) {
      instrumentPanel.contents.clear()

      instrumentPanel.contents ++= instrumentPanels

      instrumentPanel.contents.collect({
        case p: Publisher => p
      }).foreach(listenTo(_))


      instrumentPanel.revalidate()
      splitPane.revalidate()

      reDrawChart()
    }


    val splitPane = new SplitPane(Orientation.Horizontal,
      new BorderPanel {
        add(instrumentPanel, BorderPanel.Position.North)
        add(drawButton, BorderPanel.Position.South)
      }
      , chartPanel) {
      dividerLocation = 200
      dividerSize = 8
      oneTouchExpandable = true
    }
    contents = splitPane


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