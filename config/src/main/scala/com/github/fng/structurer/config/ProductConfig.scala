package com.github.fng.structurer
package config

import com.efgfp.commons.expression.Expression
import expression.{RichExpression, ExpressionParser}
import java.io.Serializable
import json.Json._
import util.parsing.json.JSONObject._
import util.parsing.json.JSONArray._
import util.parsing.json.{JSONArray, JSONObject, JSONFormat, JSON}
import config.FieldConfig.{ChooseFieldConfig, DoubleRangeFieldConfig, DoubleFieldConfig}

abstract class FieldConfig {
  def toJson: JSONObject
}

object FieldConfig {

  def fromJsonMap(map: Map[String, _]): FieldConfig = {
    val name = map.forceString("name")
    val fieldType = map.forceString("type")
    val validationType = map.forceString("validationType")
    val validationValue = map.forceString("validationValue")
    val default = map.forceStringOption("default")

    fieldType match {
      case "number" => validationType match {
        case "between" => validationValue.split(";").toList match {
          case List(a, b) => DoubleRangeFieldConfig(name, a.toDouble, b.toDouble, default.map(_.toDouble))
          case _ => error("not like '10;20'!")
        }
        case other => DoubleFieldConfig(name, DoubleFieldValidationType.fromString(other), validationValue.toDouble, default.map(_.toDouble))
      }
      case "choose" => ChooseFieldConfig(name, ChooseFieldValidationType.fromString(validationType), validationValue.split(",").toList, default)
    }

  }

  case class DoubleFieldConfig(name: String, validationType: DoubleFieldValidationType, level: Double, default: Option[Double]) extends FieldConfig {
    def toJson: JSONObject = JSONObject(Map(
      "name" -> name,
      "type" -> "number",
      "validationType" -> validationType.jsonKey,
      "validationValue" -> level.toString,
      "default" -> default.map(_.toString).orNull
    ))

  }

  case class DoubleRangeFieldConfig(name: String, from: Double, to: Double, default: Option[Double]) extends FieldConfig {
    def toJson: JSONObject = JSONObject(Map(
      "name" -> name,
      "type" -> "number",
      "validationType" -> "between",
      "validationValue" -> (from + ";" + to),
      "default" -> default.map(_.toString).orNull
    ))

  }

  abstract class DoubleFieldValidationType(val jsonKey: String)

  object DoubleFieldValidationType {

    case object GreaterThan extends DoubleFieldValidationType("GT")

    case object GreaterThanEqual extends DoubleFieldValidationType("GE")

    case object LessThanEqual extends DoubleFieldValidationType("LTE")

    case object LessThan extends DoubleFieldValidationType("LT")

    def fromString(validationType: String): DoubleFieldValidationType = validationType match {
      case GreaterThan.jsonKey => GreaterThan
      case GreaterThanEqual.jsonKey => GreaterThanEqual
      case LessThan.jsonKey => LessThan
      case LessThanEqual.jsonKey => LessThanEqual
    }
  }

  case class ChooseFieldConfig(name: String, validation: ChooseFieldValidationType, values: List[String], default: Option[String]) extends FieldConfig {
    def toJson: JSONObject = JSONObject(Map(
      "name" -> name,
      "type" -> "choose",
      "validationType" -> validation.jsonKey,
      "validationValue" -> values.mkString(","),
      "default" -> default.orNull
    ))

  }

  abstract class ChooseFieldValidationType(val jsonKey: String)

  object ChooseFieldValidationType {

    case object OneOf extends ChooseFieldValidationType("OneOf")

    case object ManyOf extends ChooseFieldValidationType("ManyOf")

    def fromString(validationType: String): ChooseFieldValidationType = validationType match {
      case OneOf.jsonKey => OneOf
      case ManyOf.jsonKey => ManyOf
    }

  }

}


case class BarrierConfig(barrierType: String, level: RichExpression)

object BarrierConfig {
  def apply(barrierType: String, level: String): BarrierConfig = {
    BarrierConfig(barrierType, ExpressionParser.parse(level))
  }
}

case class OptionConfig(quantity: RichExpression, optionType: String, setup: String, strike: RichExpression,
                        basis: RichExpression, notional: RichExpression, barrier: BarrierConfig) {
  def toJson: JSONObject = JSONObject(Map(
    "quantity" -> quantity.originalString,
    "type" -> optionType,
    "setup" -> setup,
    "strike" -> strike.originalString,
    "basis" -> basis.originalString,
    "notional" -> notional.originalString,
    "barrier" -> JSONObject(Map(
      "type" -> barrier.barrierType,
      "level" -> barrier.level.originalString
    ))))
}

object OptionConfig {
  def apply(quantity: String, optionType: String, setup: String, strike: String, basis: String, notional: String, barrier: BarrierConfig): OptionConfig = {
    OptionConfig(ExpressionParser.parse(quantity), optionType, setup, ExpressionParser.parse(strike), ExpressionParser.parse(basis), ExpressionParser.parse(notional), barrier)
  }
}

case class BondConfig(quantity: RichExpression, notional: RichExpression, frequency: RichExpression, fixedRate: RichExpression) {
  def toJson: JSONObject = JSONObject(Map(
    "quantity" -> quantity.originalString,
    "notional" -> notional.originalString,
    "frequency" -> frequency.originalString,
    "fixedRate" -> fixedRate.originalString
  ))

}

object BondConfig {
  def apply(quantity: String, notional: String, frequency: String, fixedRate: String): BondConfig = {
    BondConfig(ExpressionParser.parse(quantity), ExpressionParser.parse(notional), ExpressionParser.parse(frequency), ExpressionParser.parse(fixedRate))
  }
}

case class ProductConfig(productTypeId: String, payoffType: String, quotationType: String, underlyingType: String,
                         autocallalbe: String, allotment: String,
                         fields: List[FieldConfig], options: List[OptionConfig], bonds: List[BondConfig]) extends Serializable {
  def toJson: String = {


    val map = Map("product-config" -> JSONObject(Map(
      "productTypeId" -> productTypeId,
      "payoffType" -> payoffType,
      "quotationType" -> quotationType,
      "underlyingType" -> underlyingType,
      "autocallable" -> autocallalbe,
      "allotment" -> allotment,
      "field" -> JSONArray(fields.map(_.toJson)),
      "option" -> JSONArray(options.map(_.toJson)),
      "bond" -> JSONArray(bonds.map(_.toJson))
    )))

    JSONObject(map).toString(JSONFormat.defaultFormatter)
  }
}

object ProductConfig {


  def apply(json: String): ProductConfig = {
    JSON.parseFull(json) match {
      case Some(b: Map[String, Map[String, _]]) => b.get("product-config") match {
        case Some(productConfig) => apply(productConfig)
        case _ => error("Required key: product-config not found!")
      }
      case _ => error("json input is not a Map")
    }
  }


  def apply(productConfig: Map[String, _]): ProductConfig = {
    val productTypeId = productConfig.forceString("productTypeId")
    val payoffType = productConfig.forceString("payoffType")
    val quotationType = productConfig.forceString("quotationType")
    val underlyingType = productConfig.forceString("underlyingType")
    val autocallable = productConfig.forceString("autocallable")
    val allotment = productConfig.forceString("allotment")
    val fields = productConfig.listMap("field").map(fields => fields.map((field) => {
      FieldConfig.fromJsonMap(field)
    })).getOrElse(List[FieldConfig]())
    val options = productConfig.listMap("option").map(options => options.map((option) => {

      val rawBarrier = option.get("barrier") match {
        case Some(b: Map[String, _]) => Some(b)
        case Some(x) => error("unkown x: " + x)
        case _ => None
      }

      val barrier = rawBarrier.map((bar) => {
        BarrierConfig(
          bar.forceString("type"),
          bar.forceExpression("level")
        )
      })

      OptionConfig(
        option.forceExpression("quantity"),
        option.forceString("type"),
        option.forceString("setup"),
        option.forceExpression("strike"),
        option.forceExpression("basis"),
        option.forceExpression("notional"),
        barrier.getOrElse(null)
      )
    })).getOrElse(List[OptionConfig]())
    val bonds = productConfig.listMap("bond").map(bonds => bonds.map((bond) => {
      BondConfig(
        bond.forceExpression("quantity"),
        bond.forceExpression("notional"),
        bond.forceExpression("frequency"),
        bond.forceExpression("fixedRate")
      )
    })).getOrElse(List[BondConfig]())

    ProductConfig(productTypeId, payoffType, quotationType, underlyingType, autocallable, allotment, fields, options, bonds)
  }
}