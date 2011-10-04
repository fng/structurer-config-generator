package com.github.fng.structurer.view

import swing.{BoxPanel, Orientation}
import javax.swing.BorderFactory
import com.github.fng.structurer.payoff.BondInstrument

class BondPanel extends BoxPanel(Orientation.Vertical) {
  border = BorderFactory.createTitledBorder("Bond")


  val notionalField = new DoubleField("Notional", 1000.0)
  val quantityField = new DoubleField("Quantity", 1)

  contents += notionalField
  contents += quantityField


  def notional = notionalField.getValue
  def quantity = quantityField.getValue


  def bondInstrument = BondInstrument(notional, quantity)

}


