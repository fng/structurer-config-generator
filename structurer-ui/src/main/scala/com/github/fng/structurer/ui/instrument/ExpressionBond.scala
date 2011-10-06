package com.github.fng.structurer.ui.instrument


import com.github.fng.structurer.config.expression.{ExpressionParser, RichExpression}
import ExpressionParser._
import com.github.fng.structurer.instrument._

case class ExpressionBond(notional: RichExpression, quantity: RichExpression) extends Instrument

object ExpressionBond {
  def apply(notional: Double, quantity: Double): ExpressionBond =
    ExpressionBond(parse(notional), parse(quantity))

  def apply(bondInstrument: BondInstrument): ExpressionBond =
    ExpressionBond(bondInstrument.notional, bondInstrument.quantity)
}