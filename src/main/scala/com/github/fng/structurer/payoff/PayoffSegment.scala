package com.github.fng.structurer
package payoff

case class PayoffSegment(slope: Double, payoffAtLowerBound: Double, lowerStrike: Double, upperStrike: Option[Double])