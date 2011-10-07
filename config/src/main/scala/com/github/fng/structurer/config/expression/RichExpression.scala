package com.github.fng.structurer.config
package expression


import java.util.HashMap
import com.efgfp.commons.expression.{Constant, Expression}


object RichExpression {
  implicit def expressionToConstant(e: Expression) = new {
    def asConstant = e match {
      case c: Constant => c
      case _ => error("not a Constant")
    }
  }

  implicit def mapToJavaMap[K, V](map: Map[K, V]): java.util.Map[K, V] = {
    map.foldLeft(new HashMap[K, V])((accu, entry) => {
      accu.put(entry._1, entry._2)
      accu
    })
  }
}

case class RichExpression(val expression: Expression) {
  import RichExpression._

  def describe: String = expression.describe

  def evaluate(values: Map[Expression, Expression] = Map()): Number = {
    println("evaluate expression: " + expression)
    println("values: " + values)
    val evaluatedExpression = expression.evaluate(values)
    println("evaluatedExpression expression: " + expression)
    evaluatedExpression.asConstant.getNumber
  }

  //  @Test def unaryOperation() {
  //    var expression = ExpressionParser.parse("((-Participation)*10)")
  //    Assert.assertEquals("[-Participation] × 10.0", expression.describe)
  //    var map = Map[Expression, Expression](PARTICIPATION -> new Constant(1))
  //
  //    //    var res = map.foldLeft(new HashMap[Expression, Expression])((accu, mapentry) => {
  //    //      accu.put(mapentry._1, mapentry._2)
  //    //      accu
  //    //    })
  //
  //    var res: java.util.Map[Expression, Expression] = map
  //
  //    var result: Constant = expression.evaluate(map).asConstant
  //    Assert.assertEquals(-10.0, result.getNumber)
  //  }
}