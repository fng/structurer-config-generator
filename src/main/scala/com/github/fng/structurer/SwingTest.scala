package com.github.fng.structurer

import payoff._
import swing._
import org.jfree.chart.{ChartFactory, ChartPanel}
import org.jfree.data.general.DefaultPieDataset
import org.jfree.util.Rotation
import org.jfree.data.xy.{XYSeriesCollection, XYSeries}
import org.jfree.chart.plot.{XYPlot, PlotOrientation, PiePlot3D}
import org.jfree.chart.axis.NumberAxis
import view.{OptionPanel, DoubleField, StringField}

object SwingTest extends SimpleSwingApplication {
  def top = new MainFrame {

    title = "Test"

    val framewidth = 600
    val frameheight = 600
    val screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize()
    location = new java.awt.Point((screenSize.width - framewidth) / 2, (screenSize.height - frameheight) / 2)
    minimumSize = new java.awt.Dimension(framewidth, frameheight)


    menuBar = new MenuBar {
      contents += new Menu("File") {
        contents += new MenuItem(Action("Quit") {
          System.exit(0)
        })
      }
      contents += new Menu("Test") {
        contents += new MenuItem(Action("Dialog ...") {
          Dialog.showMessage(message = "This is a dialog!")
        })
      }
    }


    val instrumentPanel = new BorderPanel {
      val optionPanel1 = new OptionPanel
      val optionPanel2 = new OptionPanel

      add(new BoxPanel(Orientation.Horizontal) {
        contents += optionPanel1
        contents += optionPanel2
      }, BorderPanel.Position.North)

      add(new Button(Action("Press me") {
        Dialog.showMessage(message = "Option 1: " + optionPanel1.optionInstrument + " Option 2: " + optionPanel2.optionInstrument)
      }), BorderPanel.Position.South)

    }


    val chartPanel = new Panel {
      override lazy val peer = createXYChartPanel()
    }


    contents = new SplitPane(Orientation.Vertical, instrumentPanel, chartPanel) {
      dividerLocation = 250
      dividerSize = 8
      oneTouchExpandable = true
    }
    //    contents = chartPanel


  }

  def createPieChartPanel(): ChartPanel = {
    val dataSet = new DefaultPieDataset()
    dataSet.setValue("Linux", 29)
    dataSet.setValue("Mac", 20)
    dataSet.setValue("Windows", 51)


    val chart = ChartFactory.createPieChart3D("Chart title", dataSet, true, true, false)
    val plot = chart.getPlot.asInstanceOf[PiePlot3D]
    plot.setStartAngle(290)
    plot.setDirection(Rotation.CLOCKWISE)
    plot.setForegroundAlpha(0.5f)

    new ChartPanel(chart)
  }

  def createXYChartPanel(): ChartPanel = {
    //val series = new XYSeries("Random Data")
    //for((x, y) <- List((0, 0), (100, 1000), (200, 2000))){
    //  series.add(x, y)
    //}

    val defaultUnderlyingSeries = {
      val series = new XYSeries("Underlying")
      series.add(0, 0)
      series.add(1, 1000)
      series.add(2, 2000)
      series
    }

    val dataSet = new XYSeriesCollection
    //dataSet.addSeries(defaultUnderlyingSeries)
    //dataSet.addSeries(seriesFromOption(Option(OptionType.Call, 0.0, 10)))
    //    dataSet.addSeries(seriesFromOption(Option(OptionType.Call, 1.2, -10)))
    //    dataSet.addSeries(seriesFromOption(OptionInstrument(OptionType.Put, 1, -10)))

    //    dataSet.addSeries(seriesFromPayoffSegment(PayoffSegment(1000, -1000, 0, 1.0), "short put"))
    //    dataSet.addSeries(seriesFromPayoffSegment(PayoffSegment(-1000, 1000, 0, 1.0), "long put"))
    //    dataSet.addSeries(seriesFromPayoffSegment(PayoffSegment(1000, 0, 1.0, 2.0), "long call"))
    //    dataSet.addSeries(seriesFromPayoffSegment(PayoffSegment(-1000, 0, 1.0, 2.0), "short call"))

    for (series <- seriesForOptionsAndBonds("RC",
      options = List(OptionInstrument(OptionType.Put, 1.0, -1000)),
      bonds = List(BondInstrument(1000, 1)))) {
      dataSet.addSeries(series)
    }

    for (series <- seriesForOptionsAndBonds("OC",
      options = List(
        OptionInstrument(OptionType.Call, 0.0, 1000),
        OptionInstrument(OptionType.Call, 1.0, 1200)
      ),
      bonds = Nil
    )) {
      dataSet.addSeries(series)
    }



    val chart = ChartFactory.createXYLineChart("XY Chart", "Underlying", "Product", dataSet, PlotOrientation.VERTICAL,
      true, true, false)

    val plot = chart.getPlot.asInstanceOf[XYPlot]
    //    plot.getRangeAxis().asInstanceOf[NumberAxis].setAutoRangeIncludesZero(true)
    //    plot.getRangeAxis().asInstanceOf[NumberAxis].setUpperBound(2000)
    //    plot.getRangeAxis().asInstanceOf[NumberAxis].setLowerBound(-2000)
    //    plot.getDomainAxis.asInstanceOf[NumberAxis].setAutoRangeIncludesZero(true)
    //    plot.getDomainAxis.asInstanceOf[NumberAxis].setUpperBound(2)
    //    plot.getDomainAxis.asInstanceOf[NumberAxis].setLowerBound(-2)

    plot.getRangeAxis().asInstanceOf[NumberAxis].centerRange(1000)
    plot.getDomainAxis.asInstanceOf[NumberAxis].centerRange(1)

    new ChartPanel(chart)

  }


  def seriesForOptionsAndBonds(prefix: String, options: List[OptionInstrument], bonds: List[BondInstrument]): List[XYSeries] = {

    var segmentCounter = 0
    val segments = new PayoffBuilder().build(options, bonds)

    val series = segments.map {
      segment =>
        segmentCounter = segmentCounter + 1
        seriesFromPayoffSegment(segment, prefix + "-seg-" + segmentCounter)
    }
    series
  }


  def seriesFromPayoffSegment(segment: PayoffSegment, name: String): XYSeries = {

    segment.upperStrike match {
      case Some(x2) =>
        val x1 = segment.lowerStrike
        val y1 = segment.payoffAtLowerBound
        val y2 = (x2 - x1) * segment.slope + y1

        val series = new XYSeries(name)
        series.add(x1, y1)
        series.add(x2, y2)
        series
      case None =>
        val x1 = segment.lowerStrike
        val y1 = segment.payoffAtLowerBound
        val x2 = 2
        val xDistance = x2 - x1
        val y2 = (xDistance * segment.slope) + y1
        val series = new XYSeries(name)
        series.add(x1, y1)
        series.add(x2, y2)
        series
    }


  }

  var optionCounter = 0

  def seriesFromOption(option: OptionInstrument): XYSeries = {
    optionCounter = optionCounter + 1
    val series = new XYSeries("Option-" + optionCounter)

    val strikeEnd = 2


    option.optionType match {
      case OptionType.Call =>
        val strike = option.strike
        val notional = option.notional
        val quantity = option.quantity

        val productAtStrike = strike * notional * quantity

        series.add(strike, productAtStrike)
        series.add(strikeEnd, strikeEnd * notional * quantity)

      case OptionType.Put =>
        val strike = option.strike
        val notional = option.notional
        val quantity = option.quantity

        val productAtStrike = -1 * strike * notional * quantity

        series.add(0, 0)
        series.add(strike, productAtStrike)
    }

    series
  }

}