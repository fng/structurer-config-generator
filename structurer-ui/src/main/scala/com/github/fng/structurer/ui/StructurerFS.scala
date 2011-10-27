package com.github.fng.structurer.ui

import instrument.LoadableFileConfigMenuItem
import java.io.File

object StructurerFS {

  private val property = System.getProperty("user.home")
  private val userHome = new File(property)
  if (!userHome.exists()) sys.error("user home: " + userHome.getAbsolutePath + " does not exist!")

  val structurerDir = new File(userHome, ".structurer")
  if (!structurerDir.exists()) {
    if (!structurerDir.mkdirs()) sys.error("Could not create structurer directory: " + structurerDir.getAbsolutePath)
  }

  val configsDir = new File(structurerDir, "configs")
  if (!configsDir.exists()) {
    if (!configsDir.mkdirs()) sys.error("Could not create structurer directory: " + configsDir.getAbsolutePath)
  }

  println("structurer directory: " + structurerDir)


  def loadableFileConfigMenuItems: List[LoadableFileConfigMenuItem] = configsDir.listFiles().toList.map{
    file => new LoadableFileConfigMenuItem(file.getAbsolutePath, file)
  }

}