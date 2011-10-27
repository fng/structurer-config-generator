package com.github.fng.structurer.ui

import instrument.LoadableFileConfigMenuItem
import org.apache.commons.io.FileUtils
import javax.swing.filechooser.FileNameExtensionFilter
import java.io.{FileFilter, File}
import swing.{Dialog, Component, FileChooser}
import util.parsing.json.JSONObject

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

  val configFileFilter = new FileNameExtensionFilter("structuer-config", "json")

  def loadableFileConfigMenuItems: List[LoadableFileConfigMenuItem] = configsDir.listFiles(new FileFilter {
    def accept(file: File): Boolean = configFileFilter.accept(file)
  }).toList.map {
    file => new LoadableFileConfigMenuItem(file.getAbsolutePath, file)
  }

  def save(component: Component) {
    val fileChooser = new FileChooser(configsDir)

    fileChooser.fileFilter = configFileFilter
    if (fileChooser.showSaveDialog(component) == FileChooser.Result.Approve) {
      if (fileChooser.selectedFile.getName.endsWith(".json")) {
        FileUtils.writeStringToFile(fileChooser.selectedFile, "test", "UTF-8")
      } else {
        Dialog.showMessage(message = "File hast to end with .json")
        save(component)
      }
    }

  }

  def dummyJson = {
      println(JSONObject(Map("A" -> "1", "B" -> 2)).toString())
  }
}

//  def mapToJson(myMap: Map[String, String]) = {
//    val json = jsonObject {
//      field("cheese", myMap.get("name")
//      jsonObject("Score") {
//        field("aroma", myMap.get("smell")
//        field("flavor", myMap.get("taste")
//        field("texture", myMap.get("squidginess")
//      }
//      jsonArray("servedOn") {
//        jsonObject {
//          field("cracker", myMap.get("crackerType")
//          field("maker", myMap.get("crackerMaker")
//        }
//      }
//      jsonArray("sampledOn") {
//        value(myMap.get("firstTried")
//        value(myMap.get("secondTried")
//        value(myMap.get("thirdTried")
//      }
//  }
//
//
//
//}