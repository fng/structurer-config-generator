package com.github.fng.structurer.ui
package instrument

import swing.MenuItem
import com.github.fng.structurer.instrument.{PackageInstrument, BondInstrument, OptionInstrument, Instrument}

class SampleMenuItem(title: String, val packageInstrument: PackageInstrument) extends MenuItem(title){

  def getComponentPanels: List[InstrumentPanel] = packageInstrument.components.map{
    case o: OptionInstrument => new OptionPanel(o)
    case b: BondInstrument => new BondPanel(b)
    case unsupported => error("Unsupported Instrument: " + unsupported)
  }

}