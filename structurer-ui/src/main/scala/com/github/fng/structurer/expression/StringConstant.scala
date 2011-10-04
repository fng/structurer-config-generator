package com.github.fng.structurer.expression

import com.efgfp.commons.expression.{Precedence, Expression, ExpressionDescribeContext, BaseExpression}
import java.util.{Arrays, Map}

class StringConstant(name: String) extends BaseExpression {
  def evaluateInternal(values: Map[Expression, Expression]) = {
    if (values.containsKey(this)) {
      values.get(this);
    } else {
      this
    }
  }

  def simplify = this

  def getLeafList = Arrays.asList(this)

  def getPrecedence = Precedence.NO

  def describeInMathMl(context: ExpressionDescribeContext) = name

  def describe(context: ExpressionDescribeContext) = name
}