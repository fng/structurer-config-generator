package com.github.fng.structurer

import payoff._
import swing._
import event.ButtonClicked
import org.jfree.chart.{ChartFactory, ChartPanel}
import org.jfree.data.general.DefaultPieDataset
import org.jfree.util.Rotation
import org.jfree.data.xy.{XYSeriesCollection, XYSeries}
import org.jfree.chart.plot.{XYPlot, PlotOrientation, PiePlot3D}
import org.jfree.chart.axis.NumberAxis
import javax.swing.JPanel
import view.{BondPanel, OptionPanel, DoubleField, StringField}
import collection.mutable.ListBuffer

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




    val optionPanel1 = new OptionPanel
    val optionPanel2 = new OptionPanel
    val bondPanel1 = new BondPanel

    val instrumentPanels = ListBuffer[Panel](optionPanel1, optionPanel2, bondPanel1)

    val drawButton = new Button("Draw")


    val instrumentPanel = new BorderPanel {

      add(new BoxPanel(Orientation.Horizontal) {
        contents ++= instrumentPanels
//        contents += optionPanel1
//        contents += optionPanel2
//        contents += bondPanel1
      }, BorderPanel.Position.North)

      add(drawButton, BorderPanel.Position.South)

    }


    val chartPanel = new Panel {
      override lazy val peer = createPayoffChart(Nil, Nil)
    }


    listenTo(drawButton)
    reactions += {
      case ButtonClicked(`drawButton`) =>
      splitPane.contents_=(splitPane.leftComponent, new Panel {
        override lazy val peer: JPanel = createPayoffChart(List(optionPanel1.optionInstrument),
          List(bondPanel1.bondInstrument))
      })
    }

    val splitPane = new SplitPane(Orientation.Vertical, instrumentPanel, chartPanel) {
      dividerLocation = 450
      dividerSize = 8
      oneTouchExpandable = true
    }
    contents = splitPane
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

  def createPayoffChart(options: List[OptionInstrument], bonds: List[BondInstrument]): ChartPanel = {

    val dataSet = new XYSeriesCollection
    for (series <- seriesForOptionsAndBonds("RC",
      options = options,
      bonds = bonds)) {
      dataSet.addSeries(series)
    }


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


}