package com.github.fng.structurer.ui

import java.io.File
import util.parsing.json.{JSONArray, JSONFormat, JSONObject}

object Test {
  def main(args: Array[String]) {

    val field = JSONObject(Map("name" -> "PARTICIPATION",
      "type" -> "number",
      "validationType" -> "GT",
      "validationValue" -> "100",
      "default" -> "120"))


    val map = Map("product-config" -> JSONObject(Map("productTypeId" -> "350.001",
      "payoffType" -> "Bullish",
      "field" -> JSONArray(List(field, field)))))

    println(JSONObject(map).toString(JSONFormat.defaultFormatter))

  }
}