package com.github.fng.structurer.ui
package instrument

import javax.swing.BorderFactory
import swing._
import com.github.fng.structurer.instrument.{OptionBarrierType, OptionType, OptionInstrument}


class OptionPanel(default: ExpressionOption = ExpressionOption(OptionType.Call, 0.0, 10, 100, OptionBarrierType.NoBarrier)) extends InstrumentPanel{
  border = BorderFactory.createTitledBorder("Option")


  val optionTypeField = new OptionTypeField("OptionType", default.optionType)
  val strikeField = new ExpressionField("Strike", default.strike)
  val quantityField = new ExpressionField("Quantity", default.quantity)
  val notionalField = new ExpressionField("Notional", default.notional)
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
  def strike = strikeField.getValue.evaluate().doubleValue()
  def quantity = quantityField.getValue.evaluate().doubleValue()  
  def notional = notionalField.getValue.evaluate().doubleValue()
  def optionBarrierType = barrierTypeField.getValue

  def optionInstrument = OptionInstrument(optionType, strike, quantity, notional, optionBarrierType)

}


