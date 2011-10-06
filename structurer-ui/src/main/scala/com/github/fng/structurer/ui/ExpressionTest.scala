package com.github.fng.structurer.ui

import com.github.fng.structurer.config.expression.ExpressionParser


object ExpressionTest {

  def main(args: Array[String]){
    val richExpression = ExpressionParser.parse("(-10)")
    println(richExpression.expression)
  }

}