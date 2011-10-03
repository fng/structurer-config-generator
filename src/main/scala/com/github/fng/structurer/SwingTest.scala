package com.github.fng.structurer

import swing._

object SwingTest extends SimpleSwingApplication {
  def top = new MainFrame {

    title = "Test"

    val framewidth = 640
    val frameheight = 480
    val screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize()
    location = new java.awt.Point((screenSize.width - framewidth) / 2, (screenSize.height - frameheight) / 2)
    minimumSize = new java.awt.Dimension(framewidth, frameheight)


    menuBar = new MenuBar{
      contents += new Menu("File"){
        contents += new MenuItem(Action("Quit"){
          System.exit(0)
        })
      }
      contents += new Menu("Test"){
        contents += new MenuItem(Action("Dialog ..."){
          Dialog.showMessage(message = "This is a dialog!")
        })
      }

    }

  }
}