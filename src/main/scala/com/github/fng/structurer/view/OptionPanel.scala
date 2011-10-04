package com.github.fng.structurer
package view

import swing.{BoxPanel, Orientation}
import payoff.{OptionType, OptionInstrument}

class OptionPanel extends BoxPanel(Orientation.Vertical) {
  val optionTypeField = new StringField("OptionType", "Call")
  val strikeField = new DoubleField("Strike", 1.0)
  val quantityField = new DoubleField("Quantity", -1000.0)

  contents += optionTypeField
  contents += strikeField
  contents += quantityField


  def optionType = optionTypeField.getValue match {
    case call if call.equalsIgnoreCase("Call") => OptionType.Call
    case put if put.equalsIgnoreCase("Put") => OptionType.Put
  }
  def strike = strikeField.getValue
  def quantity = quantityField.getValue


  def optionInstrument = OptionInstrument(optionType, strike, quantity)

}


