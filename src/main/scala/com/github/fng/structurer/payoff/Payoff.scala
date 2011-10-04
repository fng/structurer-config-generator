package com.github.fng.structurer
package payoff

sealed abstract class Payoff

object Payoff  {
   case class BarrierPayoff(barrierEventSegments: List[PayoffSegment], noBarrierEventSegments: List[PayoffSegment]) extends Payoff
   case class UnconditionalPayoff(segments: List[PayoffSegment]) extends Payoff
}
