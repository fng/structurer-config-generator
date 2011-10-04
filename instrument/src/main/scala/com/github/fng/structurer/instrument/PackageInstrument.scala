package com.github.fng.structurer.instrument


case class PackageInstrument(denomination: Double, quotationType: QuotationType, components: List[Instrument]) extends Instrument

object PackageInstrument {
  def apply(denomination: Double, quotationType: QuotationType, components: Instrument*): PackageInstrument =
    PackageInstrument(denomination, quotationType, components.toList)
}

sealed abstract class QuotationType

object QuotationType {

  case object Unit extends QuotationType

  case object Notional extends QuotationType

}