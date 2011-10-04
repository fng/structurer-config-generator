package com.github.fng.structurer
package payoff

import collection.mutable.ListBuffer

case class PayoffComponent(lowerBound: Double, valueAtLowerBound: Double, slope: Double)

class PayoffComponentListGenerator {

  private val payoffComponents = new ListBuffer[PayoffComponent]()


  private def addPayoffComponent(component: PayoffComponent) {
    payoffComponents += component
  }

  def addOption(optionType: OptionType, quantity: Double, strike: Double) {
    optionType match {
      case OptionType.Call =>
        val valueAtLowerBound = 0
        addPayoffComponent(PayoffComponent(strike, valueAtLowerBound, quantity))
      case OptionType.Put =>
        addBond(strike * quantity, 1)
        addOption(OptionType.Call, quantity * -1, 0)
        addOption(OptionType.Call, quantity, strike)
    }
  }

  def addBond(notional: Double, quantity: Double) {
    val bondValue = notional * quantity
    addPayoffComponent(PayoffComponent(0, bondValue, 0))
  }

  def getComponents: List[PayoffComponent] = {
    payoffComponents.toList
  }

}

class PayoffSegmentListBuilder {

  private var currentState: SegmentBuilderState = SegmentBuilderState.Starting

  class BuilderAccumulator {
    private val payoffSegmentList = new ListBuffer[PayoffSegment]()

    private var _payoffAtLowerBound: Double = 0;
    var currentStrike: Double = 0;
    private var _slope: Double = 0;

    def payoffAtLowerBound = _payoffAtLowerBound

    def slope = _slope

    def addToPayoffAtLowerBound(valueAtLowerBound: Double) {
      if (_payoffAtLowerBound == 0) {
        _payoffAtLowerBound = valueAtLowerBound
      } else if (valueAtLowerBound != 0) {
        _payoffAtLowerBound = _payoffAtLowerBound + valueAtLowerBound
      }
    }

    def updateQuantity(slope: Double) {
      if (this._slope == 0) {
        this._slope = slope
      } else {
        this._slope = this._slope + slope
      }
    }

    def addPayoffSegment(segment: PayoffSegment) {
      payoffSegmentList += segment
    }

    def getPayoffSegments: List[PayoffSegment] = payoffSegmentList.toList

  }

  def build(payoffComponents: List[PayoffComponent]): List[PayoffSegment] = {
    val sorted = payoffComponents.sortBy(_.lowerBound)

    val accumulator = new BuilderAccumulator

    for (component <- sorted) {
      currentState = currentState.addComponent(component, accumulator)
    }

    if (!payoffComponents.isEmpty) {
      val openEndSegment = new PayoffSegment(accumulator.slope, accumulator.payoffAtLowerBound, accumulator.currentStrike, None)
      accumulator.addPayoffSegment(openEndSegment)
    }

    accumulator.getPayoffSegments
  }

  sealed abstract class SegmentBuilderState {
    def addComponent(component: PayoffComponent, accumulator: BuilderAccumulator): SegmentBuilderState
  }

  object SegmentBuilderState {

    case object Starting extends SegmentBuilderState {
      def addComponent(component: PayoffComponent, accumulator: BuilderAccumulator): SegmentBuilderState = {
        accumulator.addToPayoffAtLowerBound(component.valueAtLowerBound)
        accumulator.updateQuantity(component.slope)
        accumulator.currentStrike = component.lowerBound
        AtSegmentStart
      }
    }

    case object AtSegmentStart extends SegmentBuilderState {
      def addComponent(component: PayoffComponent, accumulator: BuilderAccumulator): SegmentBuilderState = {
        accumulator.addToPayoffAtLowerBound(component.valueAtLowerBound)
        if (accumulator.currentStrike == component.lowerBound) {
          accumulator.updateQuantity(component.slope)
          AtSegmentStart
        } else {
          AtSegmentEnd.addComponent(PayoffComponent(component.lowerBound, component.valueAtLowerBound, component.slope), accumulator)
        }
      }
    }


    case object AtSegmentEnd extends SegmentBuilderState {
      def addComponent(component: PayoffComponent, accumulator: BuilderAccumulator): SegmentBuilderState = {
        val currentStrike = accumulator.currentStrike

        val segment = new PayoffSegment(accumulator.slope, accumulator.payoffAtLowerBound, currentStrike, Some(component.lowerBound))
        accumulator.addPayoffSegment(segment)

        val currentSlope = accumulator.slope
        accumulator.updateQuantity(component.slope)

        if (currentSlope != 0) {
          val extra = (component.lowerBound - currentStrike) * currentSlope
          accumulator.addToPayoffAtLowerBound(extra)
        }

        accumulator.currentStrike = component.lowerBound

        AtSegmentStart
      }
    }


  }

}

class PayoffBuilder {

  def build(options: List[OptionInstrument], bonds: List[BondInstrument]): List[PayoffSegment] = {
    val componentListGenerator = new PayoffComponentListGenerator
    for (option <- options) {
      componentListGenerator.addOption(option.optionType, option.quantity, option.strike)
    }
    for (bond <- bonds) {
      componentListGenerator.addBond(bond.notional, bond.quantity)
    }
    val components = componentListGenerator.getComponents

    val segmentListBuilder = new PayoffSegmentListBuilder
    segmentListBuilder.build(components)
  }

}