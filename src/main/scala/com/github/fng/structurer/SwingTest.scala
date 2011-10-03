package com.github.fng.structurer

import swing._
import org.jfree.chart.{ChartFactory, ChartPanel}
import org.jfree.data.general.DefaultPieDataset
import org.jfree.util.Rotation
import org.jfree.data.xy.{XYSeriesCollection, XYSeries}
import org.jfree.chart.plot.{XYPlot, PlotOrientation, PiePlot3D}
import org.jfree.chart.axis.NumberAxis

object SwingTest extends SimpleSwingApplication {
  def top = new MainFrame {

    title = "Test"

    val framewidth = 640
    val frameheight = 480
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

    val firstPanel = new FlowPanel {
      contents += new Label {
        text = "firstPanel - A"
      }
      contents += new Label {
        text = "firstPanel - B"
      }
    }

    val secondPanel = new FlowPanel {
      contents += new Label {
        text = "secondPanel - A"
      }
      contents += new Label {
        text = "secondPanel - B"
      }
    }


    val chartPanel = new Panel {
      override lazy val peer = createXYChartPanel()
    }


    contents = new SplitPane(Orientation.Vertical, firstPanel, chartPanel) {
      dividerLocation = 250
      dividerSize = 8
      oneTouchExpandable = true
    }


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
      series.add(1,1000)
      series.add(2,2000)
      series
    }

    val dataSet = new XYSeriesCollection
    //dataSet.addSeries(defaultUnderlyingSeries)
    //dataSet.addSeries(seriesFromOption(Option(OptionType.Call, 0.0, 10)))
    dataSet.addSeries(seriesFromOption(Option(OptionType.Call, 1.2, -10)))
    dataSet.addSeries(seriesFromOption(Option(OptionType.Put, 1, -10)))
    val chart = ChartFactory.createXYLineChart("XY Chart", "Underlying", "Product", dataSet, PlotOrientation.VERTICAL,
      true, true, false)

    val plot = chart.getPlot.asInstanceOf[XYPlot]
    plot.getRangeAxis().asInstanceOf[NumberAxis].setAutoRangeIncludesZero(true)
    plot.getDomainAxis.asInstanceOf[NumberAxis].setAutoRangeIncludesZero(true)

    new ChartPanel(chart)

  }

  var optionCounter = 0

  def seriesFromOption(option: Option): XYSeries = {
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

        series.add(0,0)
        series.add(strike, productAtStrike)
    }

    series
  }

}