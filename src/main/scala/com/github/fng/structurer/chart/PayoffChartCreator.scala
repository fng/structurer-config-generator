package com.github.fng.structurer
package chart

import org.jfree.chart.{ChartFactory, ChartPanel}
import org.jfree.chart.plot.{XYPlot, PlotOrientation}
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.xy.{XYDataset, XYSeries, XYSeriesCollection}
import java.awt.BasicStroke
import payoff._
import instrument._

class PayoffChartCreator {

  case class SeriesDataSet(dataSet: XYDataset, isBarrierPayoff: Boolean)

  def createPayoffChart(options: List[OptionInstrument], bonds: List[BondInstrument]): ChartPanel = {

    val dataSet = dataSetForOptionsAndBonds(options, bonds)


    val chart = ChartFactory.createXYLineChart("XY Chart", "Underlying", "Product", dataSet.dataSet, PlotOrientation.VERTICAL,
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

    val renderer = new XYLineAndShapeRenderer()

    if (dataSet.isBarrierPayoff) {
      renderer.setSeriesStroke(0, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
        1.0f, Array(2.0f, 6.0f), 0.0f))
    }

    plot.setRenderer(renderer)

    new ChartPanel(chart)
  }


  def dataSetForOptionsAndBonds(options: List[OptionInstrument], bonds: List[BondInstrument]): SeriesDataSet = {

    val payoff = new PayoffBuilder().buildPayoff(options, bonds)

    val dataSet = new XYSeriesCollection

    val isBarrierPayoff = payoff match {
      case Payoff.UnconditionalPayoff(segments) =>
        val series = new XYSeries("Product")
        segments.foreach {
          segment => addSeriesFromPayoffSegment(series, segment)
        }
        dataSet.addSeries(series)
        false
      case Payoff.BarrierPayoff(barrierEventSegments, noBarrierEventSegments) =>
        val barrierEventSeries = new XYSeries("Barrier Event")
        barrierEventSegments.foreach {
          segment => addSeriesFromPayoffSegment(barrierEventSeries, segment)
        }
        dataSet.addSeries(barrierEventSeries)

        val noBarrierEventSeries = new XYSeries("No Barrier Event")
        noBarrierEventSegments.foreach {
          segment => addSeriesFromPayoffSegment(noBarrierEventSeries, segment)
        }
        dataSet.addSeries(noBarrierEventSeries)
        true
    }

    SeriesDataSet(dataSet, isBarrierPayoff)
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