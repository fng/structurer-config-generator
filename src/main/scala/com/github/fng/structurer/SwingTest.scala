package com.github.fng.structurer

import swing._
import org.jfree.chart.{ChartFactory, ChartPanel}
import org.jfree.data.general.DefaultPieDataset
import org.jfree.chart.plot.PiePlot3D
import org.jfree.util.Rotation

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
      override lazy val peer = createChartPanel()
    }


    contents = new SplitPane(Orientation.Vertical, firstPanel, chartPanel) {
      dividerLocation = 250
      dividerSize = 8
      oneTouchExpandable = true
    }


  }

  def createChartPanel(): ChartPanel = {
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

}