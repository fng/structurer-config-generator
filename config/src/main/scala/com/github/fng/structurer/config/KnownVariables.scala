package com.github.fng.structurer.config

import com.efgfp.commons.expression.{Constant, Expression, Variable}
import expression.StringConstant
import java.lang.String
import org.apache.commons.lang.builder.{HashCodeBuilder, EqualsBuilder}

object KnownVariables {
  def forName(name: String): KnownVariable = name match {
    case "Participation" => PARTICIPATION
    case "PARTICIPATION" => PARTICIPATION
    case "SINGLE_UNDERLYING_FIXING" => SINGLE_UNDERLYING_FIXING
    case "CAP" => CAP
    case "STRIKE" => STRIKE
    case "BARRIER" => BARRIER
    case "COUPONRATE" => COUPONRATE
    case "COUPONFREQUENCY" => COUPONFREQUENCY
    case _ => new UNKNOWN_VARIABLE(name)
  //    case _ => error("Unkown variable: " + name)
  }
}

sealed abstract class KnownVariable(val name: String) extends Variable(name) {
  def valueToExpression(value: String): Expression = new Constant(BigDecimal(value))
}

class UNKNOWN_VARIABLE(name: String) extends KnownVariable(name) {
  override def equals(obj: Any): Boolean = {
    EqualsBuilder.reflectionEquals(this, obj);
  }

  override def hashCode(): Int = {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  override def toString(): String = {
    return name;
  }

}

case object PARTICIPATION extends KnownVariable("PARTICIPATION")
case object SINGLE_UNDERLYING_FIXING extends KnownVariable("SINGLE_UNDERLYING_FIXING")
case object CAP extends KnownVariable("CAP")
case object STRIKE extends KnownVariable("STRIKE")
case object BARRIER extends KnownVariable("BARRIER")
case object COUPONRATE extends KnownVariable("COUPONRATE")
case object COUPONFREQUENCY extends KnownVariable("COUPONFREQUENCY") {
  override def valueToExpression(value: String) = new StringConstant(value)
}
