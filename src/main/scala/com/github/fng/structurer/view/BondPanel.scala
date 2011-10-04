package com.github.fng.structurer.view

import javax.swing.BorderFactory
import com.github.fng.structurer.payoff.BondInstrument
import swing.{Button, Action, BoxPanel, Orientation}

class BondPanel(default: BondInstrument = BondInstrument(1000, 1)) extends InstrumentPanel {
  border = BorderFactory.createTitledBorder("Bond")


  val notionalField = new DoubleField("Notional", default.notional)
  val quantityField = new DoubleField("Quantity", default.quantity)

  val removeButton = new Button(Action("Remove") {
    publish(InstrumentPanel.PanelEvent.RemovePanelEvent(BondPanel.this))
  })

  contents += notionalField
  contents += quantityField
  contents += removeButton


  def notional = notionalField.getValue

  def quantity = quantityField.getValue


  def bondInstrument = BondInstrument(notional, quantity)

}


