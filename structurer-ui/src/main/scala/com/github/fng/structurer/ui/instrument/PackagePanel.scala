package com.github.fng.structurer.ui
package instrument

import javax.swing.BorderFactory
import swing._
import com.github.fng.structurer.instrument._

class PackagePanel(default: PackageInstrument = PackageInstrument("xxx.xxx", PayoffType.Bullish, 1000, QuotationType.Notional,
  OptionInstrument(OptionType.Call, 0.0, 10, 100, OptionBarrierType.NoBarrier))) extends BorderPanel {
  border = BorderFactory.createTitledBorder("Package")


  val productTypeIdField = new StringField("ProductTypeId", default.productTypeId)
  val payoffTypeField = new PayoffTypeField("PayoffType", default.payoffType)
  val denominationField = new DoubleField("Denomination", default.denomination)
  val quotationTypeField = new QuotationTypeField("QuotationType", default.quotationType)

  add(new FlowPanel(FlowPanel.Alignment.Left)(productTypeIdField, payoffTypeField, denominationField, quotationTypeField),
    BorderPanel.Position.North)


  def update(packageInstrument: PackageInstrument) {

    payoffTypeField.setValue(packageInstrument.payoffType)
    productTypeIdField.setValue(packageInstrument.productTypeId)
    denominationField.setValue(packageInstrument.denomination)
    quotationTypeField.setValue(packageInstrument.quotationType)

  }

}