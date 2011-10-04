package com.github.fng.structurer

import payoff._
import org.jfree.chart.{ChartFactory, ChartPanel}
import org.jfree.data.xy.{XYSeriesCollection, XYSeries}
import org.jfree.chart.plot.{XYPlot, PlotOrientation}
import javax.swing.JPanel
import collection.mutable.ListBuffer
import view._
import swing._
import event.ButtonClicked

object SwingTest extends SimpleSwingApplication {

  def top = new MainFrame {

    title = "Test"

    val framewidth = 600
    val frameheight = 600
    val screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize()
    location = new java.awt.Point((screenSize.width - framewidth) / 2, (screenSize.height - frameheight) / 2)
    minimumSize = new java.awt.Dimension(framewidth, frameheight)


    val addOptionMenu = new MenuItem("Option")
    val addBondMenu = new MenuItem("Bond")

    val samples = List(new SampleMenuItem("Reverse Convertible",
      BondInstrument(1000, 1),
      OptionInstrument(OptionType.Put, 1.0, -1000)),
      new SampleMenuItem("Outperformance Certificate",
        OptionInstrument(OptionType.Call, 0, 100),
        OptionInstrument(OptionType.Call, 1.0, 50)),
      new SampleMenuItem("Capped Outperformance Certificate",
        OptionInstrument(OptionType.Call, 0, 100),
        OptionInstrument(OptionType.Call, 1.0, 50),
        OptionInstrument(OptionType.Call, 1.5, -150))
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

    def reDrawChart {
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

      reDrawChart
    }


    val splitPane = new SplitPane(Orientation.Horizontal,
      new BorderPanel {
        add(instrumentPanel, BorderPanel.Position.North)
        add(drawButton, BorderPanel.Position.South)
      }
      , chartPanel) {
      dividerLocation = 160
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

    val payoffChart = createPayoffChart(options, bonds)
    payoffChart
  }


  def createPayoffChart(options: List[OptionInstrument], bonds: List[BondInstrument]): ChartPanel = {

    val dataSet = new XYSeriesCollection
    val series = seriesForOptionsAndBonds(options, bonds)
    dataSet.addSeries(series)


    val chart = ChartFactory.createXYLineChart("XY Chart", "Underlying", "Product", dataSet, PlotOrientation.VERTICAL,
      true, true, false)

    val plot = chart.getPlot.asInstanceOf[XYPlot]
    //    plot.getRangeAxis().asInstanceOf[NumberAxis].centerRange(1000)
    //    plot.getDomainAxis.asInstanceOf[NumberAxis].centerRange(1)
    //        plot.getRangeAxis().asInstanceOf[NumberAxis].setAutoRangeIncludesZero(true)
    //        plot.getRangeAxis().asInstanceOf[NumberAxis].setUpperBound(2000)
    //        plot.getRangeAxis().asInstanceOf[NumberAxis].setLowerBound(-2000)
    //        plot.getDomainAxis.asInstanceOf[NumberAxis].setAutoRangeIncludesZero(true)
    //        plot.getDomainAxis.asInstanceOf[NumberAxis].setUpperBound(2)
    //        plot.getDomainAxis.asInstanceOf[NumberAxis].setLowerBound(-2)

    new ChartPanel(chart)
  }


  def seriesForOptionsAndBonds(options: List[OptionInstrument], bonds: List[BondInstrument]): XYSeries = {

    var segmentCounter = 0
    val segments = new PayoffBuilder().build(options, bonds)

    val series = new XYSeries("series")

    segments.foreach {
      segment =>
        segmentCounter = segmentCounter + 1
        addSeriesFromPayoffSegment(series, segment)
    }
    series
  }


  def addSeriesFromPayoffSegment(series: XYSeries, segment: PayoffSegment): XYSeries = {

    segment.upperStrike match {
      case Some(x2) =>
        val x1 = segment.lowerStrike
        val y1 = segment.payoffAtLowerBound
        val y2 = (x2 - x1) * segment.slope + y1

        series.add(x1, y1)
        series.add(x2, y2)
        series
      case None =>
        val x1 = segment.lowerStrike
        val y1 = segment.payoffAtLowerBound
        val x2 = 2
        val xDistance = x2 - x1
        val y2 = (xDistance * segment.slope) + y1
        series.add(x1, y1)
        series.add(x2, y2)
        series
    }


  }


}