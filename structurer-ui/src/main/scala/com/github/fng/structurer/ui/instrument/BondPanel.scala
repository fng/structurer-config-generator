package com.github.fng.structurer.ui
package instrument

import javax.swing.BorderFactory
import com.github.fng.structurer.instrument.BondInstrument
import swing.{Button, Action, BoxPanel, Orientation}

class BondPanel(default: ExpressionBond = ExpressionBond(1000, 1)) extends InstrumentPanel {
  border = BorderFactory.createTitledBorder("Bond")


  val notionalField = new ExpressionField("Notional", default.notional)
  val quantityField = new ExpressionField("Quantity", default.quantity)

  val removeButton = new Button(Action("Remove") {
    publish(InstrumentPanel.PanelEvent.RemovePanelEvent(BondPanel.this))
  })

  contents += notionalField
  contents += quantityField
  contents += removeButton


  def notional = notionalField.getValue

  def notionalValue = notional.evaluate().doubleValue()

  def quantity = quantityField.getValue

  def quantityValue = quantity.evaluate().doubleValue()

  @Deprecated
  def bondInstrument = BondInstrument(notionalValue, quantityValue)

  def expressionBond = ExpressionBond(notional, quantity)

}


