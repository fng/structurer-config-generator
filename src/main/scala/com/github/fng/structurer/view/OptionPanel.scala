package com.github.fng.structurer
package view

import payoff.{OptionType, OptionInstrument}
import swing.{RadioButton, ButtonGroup, BoxPanel, Orientation}
import javax.swing.BorderFactory

class OptionPanel extends BoxPanel(Orientation.Vertical) {
  border = BorderFactory.createTitledBorder("Option")


  val optionTypeField = new OptionTypeField("OptionType", OptionType.Put)
  val strikeField = new DoubleField("Strike", 1.0)
  val quantityField = new DoubleField("Quantity", -1000.0)

  contents += optionTypeField
  contents += strikeField
  contents += quantityField


  def optionType = optionTypeField.getValue
  def strike = strikeField.getValue
  def quantity = quantityField.getValue


  def optionInstrument = OptionInstrument(optionType, strike, quantity)

}


