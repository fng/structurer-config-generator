package com.github.fng.structurer.ui

import instrument.SampleMenuItem
import com.github.fng.structurer.instrument._

trait PayoffSamples {
  val payoffSamples = List(new SampleMenuItem("Reverse Convertible",
    PackageInstrument("330.001", PayoffType.Bullish, 1000, QuotationType.Notional,
      BondInstrument(1000, 1),
      OptionInstrument(OptionType.Put, 100, -10, 100, OptionBarrierType.NoBarrier))),
    new SampleMenuItem("Outperformance Certificate",
      PackageInstrument("230.001", PayoffType.Bullish, 1000, QuotationType.Notional,
        OptionInstrument(OptionType.Call, 0, 1, 100, OptionBarrierType.NoBarrier),
        OptionInstrument(OptionType.Call, 100, 0.5, 100, OptionBarrierType.NoBarrier))),
    new SampleMenuItem("Capped Outperformance Certificate",
      PackageInstrument("350.001", PayoffType.Bullish, 100, QuotationType.Notional,
        OptionInstrument(OptionType.Call, 0, 1, 100, OptionBarrierType.NoBarrier),
        OptionInstrument(OptionType.Call, 100, 0.5, 100, OptionBarrierType.NoBarrier),
        OptionInstrument(OptionType.Call, 150, -1.5, 100, OptionBarrierType.NoBarrier))),
    new SampleMenuItem("Capital Protected w/o Cap",
      PackageInstrument("410.001", PayoffType.Bullish, 1000, QuotationType.Notional,
        BondInstrument(950, 1),
        OptionInstrument(OptionType.Call, 100, 10, 100, OptionBarrierType.NoBarrier))),
    new SampleMenuItem("Capital Protected with Cap",
      PackageInstrument("450.001", PayoffType.Bullish, 1000, QuotationType.Notional,
        BondInstrument(950, 1),
        OptionInstrument(OptionType.Call, 100, 10, 100, OptionBarrierType.NoBarrier),
        OptionInstrument(OptionType.Call, 140, -10, 100, OptionBarrierType.NoBarrier))),
    new SampleMenuItem("Barrier Reverse Convertible",
      PackageInstrument("340.001", PayoffType.Bullish, 1000, QuotationType.Notional,
        BondInstrument(1000, 1),
        OptionInstrument(OptionType.Put, 100, -10, 100, OptionBarrierType.KnockInBarrier)))
  )

}