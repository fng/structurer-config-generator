package com.github.fng.structurer.payoff

import com.github.fng.structurer.instrument.{OptionBarrierType, BondInstrument, OptionInstrument}
import com.github.fng.structurer.payoff.Payoff.{BarrierPayoff, UnconditionalPayoff}

class PayoffBuilder {

  def buildPayoff(options: List[OptionInstrument], bonds: List[BondInstrument]): Payoff = {
    if(options.exists(_.optionBarrierType != OptionBarrierType.NoBarrier)){
      buildBarrierPayoff(options, bonds)
    }else{
      buildUnconditionalPayoff(options, bonds)
    }
  }


  def buildUnconditionalPayoff(options: List[OptionInstrument], bonds: List[BondInstrument]): UnconditionalPayoff = {
    val segments = buildSegments(options, bonds)
    UnconditionalPayoff(segments)
  }

  def buildBarrierPayoff(options: List[OptionInstrument], bonds: List[BondInstrument]): BarrierPayoff = {
    val barrierEventSegments = buildSegments(options.filter(PayoffFilter.BarrierEvent.optionBelongsToPayoff(_)), bonds)
    val noBarrierEventSegments = buildSegments(options.filter(PayoffFilter.NoBarrierEvent.optionBelongsToPayoff(_)), bonds)

    BarrierPayoff(barrierEventSegments, noBarrierEventSegments)
  }

  private def buildSegments(options: List[OptionInstrument], bonds: List[BondInstrument]): List[PayoffSegment] = {
    val componentListGenerator = new PayoffComponentListGenerator
    for (option <- options) {
      componentListGenerator.addOption(option.optionType, option.quantity * option.notional, option.strike / 100)
    }
    for (bond <- bonds) {
      componentListGenerator.addBond(bond.notional, bond.quantity)
    }
    val components = componentListGenerator.getComponents

    val segmentListBuilder = new PayoffSegmentListBuilder
    segmentListBuilder.build(components)
  }

}





