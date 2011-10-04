package com.github.fng.structurer.ui
package instrument

import swing.MenuItem
import com.github.fng.structurer.instrument.{BondInstrument, OptionInstrument, Instrument}

class SampleMenuItem(title: String, instruments: Instrument*) extends MenuItem(title){

  def asInstrumentPanels: List[InstrumentPanel] = instruments.toList.map{
    case o: OptionInstrument => new OptionPanel(o)
    case b: BondInstrument => new BondPanel(b)
    case unsupported => error("Unsupported Instrument: " + unsupported)
  }

}