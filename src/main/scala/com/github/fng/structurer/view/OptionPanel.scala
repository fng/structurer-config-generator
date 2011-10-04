package com.github.fng.structurer
package view

import javax.swing.BorderFactory
import swing._
import payoff.{OptionBarrierType, OptionType, OptionInstrument}

class OptionPanel(default: OptionInstrument = OptionInstrument(OptionType.Call, 0.0, 1000, OptionBarrierType.NoBarrier)) extends InstrumentPanel{
  border = BorderFactory.createTitledBorder("Option")


  val optionTypeField = new OptionTypeField("OptionType", default.optionType)
  val strikeField = new DoubleField("Strike", default.strike)
  val quantityField = new DoubleField("Quantity", default.quantity)
  val barrierTypeField = new OptionBarrierTypeField("BarrierType", default.optionBarrierType)

  val removeButton = new Button(Action("Remove"){
    publish(InstrumentPanel.PanelEvent.RemovePanelEvent(OptionPanel.this))
  })

  contents += optionTypeField
  contents += strikeField
  contents += quantityField
  contents += barrierTypeField
  contents += removeButton


  def optionType = optionTypeField.getValue
  def strike = strikeField.getValue
  def quantity = quantityField.getValue
  def optionBarrierType = barrierTypeField.getValue

  def optionInstrument = OptionInstrument(optionType, strike, quantity, optionBarrierType)

}


