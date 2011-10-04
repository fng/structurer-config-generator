package com.github.fng.structurer

import payoff.{BondInstrument, OptionInstrument, OptionType, OptionBarrierType}
import view.SampleMenuItem

trait PayoffSamples {
  val payoffSamples = List(new SampleMenuItem("Reverse Convertible",
    BondInstrument(1000, 1),
    OptionInstrument(OptionType.Put, 1.0, -1000, OptionBarrierType.NoBarrier)),
    new SampleMenuItem("Outperformance Certificate",
      OptionInstrument(OptionType.Call, 0, 100, OptionBarrierType.NoBarrier),
      OptionInstrument(OptionType.Call, 1.0, 50, OptionBarrierType.NoBarrier)),
    new SampleMenuItem("Capped Outperformance Certificate",
      OptionInstrument(OptionType.Call, 0, 100, OptionBarrierType.NoBarrier),
      OptionInstrument(OptionType.Call, 1.0, 50, OptionBarrierType.NoBarrier),
      OptionInstrument(OptionType.Call, 1.5, -150, OptionBarrierType.NoBarrier)),
    new SampleMenuItem("Capital Protected w/o Cap",
      BondInstrument(950, 1),
      OptionInstrument(OptionType.Call, 1.0, 1000, OptionBarrierType.NoBarrier)),
    new SampleMenuItem("Capital Protected with Cap",
      BondInstrument(950, 1),
      OptionInstrument(OptionType.Call, 1.0, 1000, OptionBarrierType.NoBarrier),
      OptionInstrument(OptionType.Call, 1.4, -1000, OptionBarrierType.NoBarrier)),
    new SampleMenuItem("Barrier Reverse Convertible",
      BondInstrument(1000, 1),
      OptionInstrument(OptionType.Put, 1.0, -1000, OptionBarrierType.KnockInBarrier))
  )

}