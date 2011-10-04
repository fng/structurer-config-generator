package com.github.fng.structurer
package chart

import org.jfree.chart.{ChartFactory, ChartPanel}
import org.jfree.chart.plot.{XYPlot, PlotOrientation}
import org.jfree.data.xy.{XYSeries, XYSeriesCollection}
import payoff.{PayoffSegment, PayoffBuilder, BondInstrument, OptionInstrument}

class PayoffChartCreator {
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