package com.github.fng.structurer.instrument

case class OptionInstrument(optionType: OptionType, strike: Double, quantity: Double,
                            notional: Double,
                            optionBarrierType: OptionBarrierType) extends Instrument

sealed abstract class OptionType

object OptionType {

  case object Call extends OptionType

  case object Put extends OptionType

}

sealed abstract class OptionBarrierType

object OptionBarrierType  {
   case object NoBarrier extends OptionBarrierType
   case object KnockInBarrier extends OptionBarrierType
   case object KnockOutBarrier extends OptionBarrierType
}

