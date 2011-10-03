package com.github.fng.structurer


case class Option(optionType: OptionType, strike: Double, quantity: Double) {
  val notional = 100.0
}

abstract class OptionType

object OptionType {

  case object Call extends OptionType

  case object Put extends OptionType

}

