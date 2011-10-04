package com.github.fng.structurer.payoff

case class PayoffSegment(slope: Double, payoffAtLowerBound: Double, lowerStrike: Double, upperStrike: Option[Double])