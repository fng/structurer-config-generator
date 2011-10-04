package com.github.fng.structurer.ui

import chart.PayoffChartCreator
import instrument._
import org.jfree.chart.ChartPanel
import swing._
import event.ButtonClicked
import javax.swing.JPanel
import com.github.fng.structurer.instrument.{BondInstrument, OptionInstrument, PackageInstrument}

object Structurer extends SimpleSwingApplication with PayoffSamples {

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


    }

    val drawButton = new Button("Draw")

    val packagePanel = new PackagePanel

    val chartPanel = new Panel {
      override lazy val peer = payoffChartFormInstrumentPanel(packagePanel.instrumentPanel)
    }

    payoffSamples.foreach(listenTo(_))

    packagePanel.instrumentPanel.contents.collect({
      case p: Publisher => p
    }).foreach(listenTo(_))

    listenTo(drawButton, addOptionMenu, addBondMenu)


    reactions += {
      case ButtonClicked(`drawButton`) =>
        reDrawChart()
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
      case ButtonClicked(sample) if sample.isInstanceOf[SampleMenuItem] =>
        dialogSave {
          refreshPackagePanelWithNew(sample.asInstanceOf[SampleMenuItem].packageInstrument)
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
        override lazy val peer: JPanel = payoffChartFormInstrumentPanel(packagePanel.instrumentPanel)
      })
    }


    def refreshPackagePanelWithNew(packageInstrument: PackageInstrument) {
      packagePanel.update(packageInstrument)
      splitPane.revalidate()
      reDrawChart()
    }


    val splitPane = new SplitPane(Orientation.Horizontal,
      new BorderPanel {
        add(packagePanel, BorderPanel.Position.North)
        add(drawButton, BorderPanel.Position.South)
      }
      , chartPanel) {
      //dividerLocation = 230
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