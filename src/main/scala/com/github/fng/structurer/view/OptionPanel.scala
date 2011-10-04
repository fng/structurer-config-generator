package com.github.fng.structurer
package view

import payoff.{OptionType, OptionInstrument}
import javax.swing.BorderFactory
import swing._

class OptionPanel(default: OptionInstrument = OptionInstrument(OptionType.Put, 1.0, -1000)) extends InstrumentPanel{
  border = BorderFactory.createTitledBorder("Option")


  val optionTypeField = new OptionTypeField("OptionType", default.optionType)
  val strikeField = new DoubleField("Strike", default.strike)
  val quantityField = new DoubleField("Quantity", default.quantity)

  val removeButton = new Button(Action("Remove"){
    publish(InstrumentPanel.PanelEvent.RemovePanelEvent(OptionPanel.this))
  })

  contents += optionTypeField
  contents += strikeField
  contents += quantityField
  contents += removeButton


  def optionType = optionTypeField.getValue
  def strike = strikeField.getValue
  def quantity = quantityField.getValue


  def optionInstrument = OptionInstrument(optionType, strike, quantity)

}


