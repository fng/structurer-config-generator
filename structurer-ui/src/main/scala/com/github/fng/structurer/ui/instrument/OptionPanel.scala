package com.github.fng.structurer.ui
package instrument

import javax.swing.BorderFactory
import swing._
import com.github.fng.structurer.instrument.{OptionBarrierType, OptionType, OptionInstrument}

class OptionPanel(default: OptionInstrument = OptionInstrument(OptionType.Call, 0.0, 10, 100, OptionBarrierType.NoBarrier)) extends InstrumentPanel{
  border = BorderFactory.createTitledBorder("Option")


  val optionTypeField = new OptionTypeField("OptionType", default.optionType)
  val strikeField = new DoubleField("Strike", default.strike)
  val quantityField = new DoubleField("Quantity", default.quantity)
  val notionalField = new DoubleField("Notional", default.notional)
  val barrierTypeField = new OptionBarrierTypeField("BarrierType", default.optionBarrierType)

  val removeButton = new Button(Action("Remove"){
    publish(InstrumentPanel.PanelEvent.RemovePanelEvent(OptionPanel.this))
  })

  contents += optionTypeField
  contents += strikeField
  contents += quantityField
  contents += notionalField
  contents += barrierTypeField
  contents += removeButton


  def optionType = optionTypeField.getValue
  def strike = strikeField.getValue
  def quantity = quantityField.getValue
  def notional = notionalField.getValue
  def optionBarrierType = barrierTypeField.getValue

  def optionInstrument = OptionInstrument(optionType, strike, quantity, notional, optionBarrierType)

}


