package com.github.fng.structurer
package config

import _root_.scala.util.parsing.json.JSON
import com.efgfp.commons.expression.Expression
import expression.{RichExpression, ExpressionParser}
import java.io.Serializable
import json.Json._

abstract class FieldConfig

object FieldConfig {

  def fromJsonMap(map: Map[String, _]): FieldConfig = {
    val name = map.forceString("name")
    val fieldType = map.forceString("type")
    val validationType = map.forceString("validationType")
    val validationValue = map.forceString("validationValue")

    fieldType match {
      case "number" => DoubleFieldConfig(name, DoubleFieldValidationType.fromString(validationType), validationValue.toDouble)
      case "choose" => ChooseFieldConfig(name, ChooseFieldValidationType.fromString(validationType), validationValue.split(",").toList)
    }

  }

  case class DoubleFieldConfig(name: String, validationType: DoubleFieldValidationType, level: Double) extends FieldConfig

  abstract class DoubleFieldValidationType

  object DoubleFieldValidationType {

    case object GreaterThan extends DoubleFieldValidationType

    case object GreaterThanEqual extends DoubleFieldValidationType

    case object LessThanEqual extends DoubleFieldValidationType

    case object LessThan extends DoubleFieldValidationType

    def fromString(validationType: String): DoubleFieldValidationType = validationType match {
      case "GT" => GreaterThan
      case "GE" => GreaterThanEqual
      case "LT" => LessThan
      case "LE" => LessThanEqual
    }
  }

  case class ChooseFieldConfig(name: String, validation: ChooseFieldValidationType, values: List[String]) extends FieldConfig

  abstract class ChooseFieldValidationType

  object ChooseFieldValidationType {

    case object OneOf extends ChooseFieldValidationType

    case object ManyOf extends ChooseFieldValidationType

    def fromString(validationType: String): ChooseFieldValidationType = validationType match {
      case "OneOf" => OneOf
      case "ManyOf" => ManyOf
    }

  }

}


case class BarrierConfig(barrierType: String, level: RichExpression)

object BarrierConfig {
  def apply(barrierType: String, level: String): BarrierConfig = {
    BarrierConfig(barrierType, ExpressionParser.parse(level))
  }
}

case class OptionConfig(quantity: RichExpression, optionType: String, setup: String, strike: RichExpression, basis: RichExpression, notional: RichExpression, barrier: BarrierConfig)

object OptionConfig {
  def apply(quantity: String, optionType: String, setup: String, strike: String, basis: String, notional: String, barrier: BarrierConfig): OptionConfig = {
    OptionConfig(ExpressionParser.parse(quantity), optionType, setup, ExpressionParser.parse(strike), ExpressionParser.parse(basis), ExpressionParser.parse(notional), barrier)
  }
}

case class BondConfig(quantity: String, notional: String, frequency: RichExpression, fixedRate: RichExpression)

object BondConfig {
  def apply(quantity: String, notional: String, frequency: String, fixedRate: String): BondConfig = {
    BondConfig(quantity, notional, ExpressionParser.parse(frequency), ExpressionParser.parse(fixedRate))
  }
}

case class ProductConfig(productTypeId: String, payoffType: String, quotationType: String, underlyingType: String,
                         autocallalbe: String, allotment: String,
                         fields: List[FieldConfig], options: List[OptionConfig], bonds: List[BondConfig]) extends Serializable

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
        bond.forceString("quantity"),
        bond.forceString("notional"),
        bond.forceExpression("frequency"),
        bond.forceExpression("fixedRate")
      )
    })).getOrElse(List[BondConfig]())

    ProductConfig(productTypeId, payoffType, quotationType, underlyingType, autocallable, allotment, fields, options, bonds)
  }
}