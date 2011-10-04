package com.github.fng.structurer.payoff

import com.github.fng.structurer.instrument.{OptionBarrierType, OptionInstrument}

sealed abstract class PayoffFilter {
  def optionBelongsToPayoff(option: OptionInstrument): Boolean
}

object PayoffFilter {

  case object BarrierEvent extends PayoffFilter {
    def optionBelongsToPayoff(option: OptionInstrument): Boolean = option.optionBarrierType match {
      case OptionBarrierType.KnockOutBarrier => false
      case _ => true
    }
  }

  case object NoBarrierEvent extends PayoffFilter {
    def optionBelongsToPayoff(option: OptionInstrument): Boolean = option.optionBarrierType match {
      case OptionBarrierType.KnockInBarrier => false
      case _ => true
    }

  }

}