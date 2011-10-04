package com.github.fng.structurer
package payoff

case class OptionInstrument(optionType: OptionType, strike: Double, quantity: Double) extends Instrument {
  val notional = 100.0
}

sealed abstract class OptionType

object OptionType {

  case object Call extends OptionType

  case object Put extends OptionType

}

