package com.github.fng.structurer.ui

import instrument.SampleMenuItem
import com.github.fng.structurer.instrument._

trait PayoffSamples {
  val payoffSamples = List(new SampleMenuItem("Reverse Convertible",
    PackageInstrument("330.001", PayoffType.Bullish, 1000, QuotationType.Notional,
      BondInstrument(1000, 1),
      OptionInstrument(OptionType.Put, 1.0, -1000, OptionBarrierType.NoBarrier))),
    new SampleMenuItem("Outperformance Certificate",
      PackageInstrument("230.001", PayoffType.Bullish, 1000, QuotationType.Notional,
        OptionInstrument(OptionType.Call, 0, 100, OptionBarrierType.NoBarrier),
        OptionInstrument(OptionType.Call, 1.0, 50, OptionBarrierType.NoBarrier))),
    new SampleMenuItem("Capped Outperformance Certificate",
      PackageInstrument("350.001", PayoffType.Bullish, 100, QuotationType.Notional,
        OptionInstrument(OptionType.Call, 0, 100, OptionBarrierType.NoBarrier),
        OptionInstrument(OptionType.Call, 1.0, 50, OptionBarrierType.NoBarrier),
        OptionInstrument(OptionType.Call, 1.5, -150, OptionBarrierType.NoBarrier))),
    new SampleMenuItem("Capital Protected w/o Cap",
      PackageInstrument("410.001", PayoffType.Bullish, 1000, QuotationType.Notional,
        BondInstrument(950, 1),
        OptionInstrument(OptionType.Call, 1.0, 1000, OptionBarrierType.NoBarrier))),
    new SampleMenuItem("Capital Protected with Cap",
      PackageInstrument("450.001", PayoffType.Bullish, 1000, QuotationType.Notional,
        BondInstrument(950, 1),
        OptionInstrument(OptionType.Call, 1.0, 1000, OptionBarrierType.NoBarrier),
        OptionInstrument(OptionType.Call, 1.4, -1000, OptionBarrierType.NoBarrier))),
    new SampleMenuItem("Barrier Reverse Convertible",
      PackageInstrument("340.001", PayoffType.Bullish, 1000, QuotationType.Notional,
        BondInstrument(1000, 1),
        OptionInstrument(OptionType.Put, 1.0, -1000, OptionBarrierType.KnockInBarrier)))
  )

}