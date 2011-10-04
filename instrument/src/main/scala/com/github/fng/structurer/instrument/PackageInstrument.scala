package com.github.fng.structurer.instrument


case class PackageInstrument(productTypeId: String, payoffType: PayoffType,
                             denomination: Double, quotationType: QuotationType,
                             components: List[Instrument]) extends Instrument

object PackageInstrument {
  def apply(productTypeId: String, payoffType: PayoffType,
            denomination: Double, quotationType: QuotationType, components: Instrument*): PackageInstrument =
    PackageInstrument(productTypeId, payoffType, denomination, quotationType, components.toList)
}

sealed abstract class QuotationType

object QuotationType {

  case object Unit extends QuotationType

  case object Notional extends QuotationType

}

sealed abstract class PayoffType
object PayoffType{
  case object Bullish extends PayoffType
  case object Bearish extends PayoffType
}