package com.github.fng.structurer.ui
package instrument

import com.github.fng.structurer.config.expression.{ExpressionParser, RichExpression}
import ExpressionParser._
import com.github.fng.structurer.instrument.{OptionInstrument, OptionBarrierType, OptionType, Instrument}

case class ExpressionOption(optionType: OptionType, strike: RichExpression, quantity: RichExpression,
                            notional: RichExpression,
                            optionBarrierType: OptionBarrierType) extends Instrument

object ExpressionOption {
  def apply(optionType: OptionType, strike: Double, quantity: Double,
            notional: Double, optionBarrierType: OptionBarrierType): ExpressionOption =
    ExpressionOption(optionType, parse(strike), parse(quantity), parse(notional), optionBarrierType)

  def apply(optionInstrument: OptionInstrument):ExpressionOption =
      ExpressionOption(optionInstrument.optionType, optionInstrument.strike,
        optionInstrument.quantity, optionInstrument.notional, optionInstrument.optionBarrierType)
}
