package com.github.fng.structurer.ui
package instrument

import javax.swing.BorderFactory
import swing._
import collection.mutable.ListBuffer
import com.github.fng.structurer.instrument._

class PackagePanel(default: PackageInstrument = PackageInstrument(1000, QuotationType.Percent,
  OptionInstrument(OptionType.Call, 0.0, 1000, OptionBarrierType.NoBarrier))) extends BorderPanel {
  border = BorderFactory.createTitledBorder("Package")

  val instrumentPanel = new BoxPanel(Orientation.Horizontal) {
    contents ++= ListBuffer[InstrumentPanel](new OptionPanel)
  }


  val denominationField = new DoubleField("Denomination", default.denomination)
  val quotationTypeField = new QuotationTypeField("QuotationType", default.quotationType)

  add(new FlowPanel(FlowPanel.Alignment.Left)(denominationField, quotationTypeField),
    BorderPanel.Position.North)

  add(instrumentPanel, BorderPanel.Position.Center)

  def update(packageInstrument: PackageInstrument) {

    denominationField.setValue(packageInstrument.denomination)
    quotationTypeField.setValue(packageInstrument.quotationType)


    instrumentPanel.contents.clear()

    instrumentPanel.contents ++= packageInstrument.components.map {
      case o: OptionInstrument => new OptionPanel(o)
      case b: BondInstrument => new BondPanel(b)
      case unsupported => error("Unsupported Instrument: " + unsupported)
    }

    instrumentPanel.contents.collect({
      case p: Publisher => p
    }).foreach(listenTo(_))


    instrumentPanel.revalidate()
  }

}