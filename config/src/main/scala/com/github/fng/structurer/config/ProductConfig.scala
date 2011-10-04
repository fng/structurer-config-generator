package com.github.fng.structurer
package config

import _root_.scala.util.parsing.json.JSON
import com.efgfp.commons.expression.Expression
import java.io.Serializable
import json.Json._
import expression.ExpressionParser


case class FieldConfig(name: String, direction: String, value: String)

case class BarrierConfig(barrierType: String, level: Expression)
object BarrierConfig{
  def apply(barrierType: String, level: String): BarrierConfig = {
    BarrierConfig(barrierType, ExpressionParser.parse(level).expression)
  }
}

case class OptionConfig(quantity: Expression, optionType: String, setup: String, strike: Expression, basis: Expression, notional: Expression, barrier: BarrierConfig)

object OptionConfig{
 def apply(quantity: String, optionType: String, setup: String, strike: String, basis: String, notional: String, barrier: BarrierConfig): OptionConfig = {
   OptionConfig(ExpressionParser.parse(quantity).expression, optionType, setup, ExpressionParser.parse(strike).expression, ExpressionParser.parse(basis).expression, ExpressionParser.parse(notional).expression, barrier)
 }
}

case class BondConfig(quantity: String, notional: String, frequency: Expression, fixedRate: Expression )
object BondConfig{
  def apply(quantity: String, notional: String, frequency: String, fixedRate: String): BondConfig = {
    BondConfig(quantity, notional, ExpressionParser.parse(frequency).expression, ExpressionParser.parse(fixedRate).expression)
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
      FieldConfig(
        field.forceString("name"),
        field.forceString("direction"),
        field.forceString("value")
        )
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