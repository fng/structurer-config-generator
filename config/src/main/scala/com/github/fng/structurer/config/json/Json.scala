package com.github.fng.structurer.config
package json

import com.efgfp.commons.expression.Expression
import expression.ExpressionParser


object Json {
  implicit def richMap(map: Map[String, _]) = new {
    def forceString(key: String) = map.get(key) match {
      case Some(a: String) => a
      case Some(noString) => error("Key " + key + " contains a value bit it's not a String!")
      case None => error("Required key " + key + " is not set!")
    }

    def forceExpression(key: String): Expression = ExpressionParser.parse(forceString(key)).expression
    def listMap(key: String): Option[List[Map[String, _]]] = map.get(key).map(b => b match {
      case a: List[Map[String, _]] => a
      case _ => error("Key " + key + " contains a value but its not a list!")
    })
  }

}