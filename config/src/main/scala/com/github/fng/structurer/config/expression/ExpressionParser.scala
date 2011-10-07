package com.github.fng.structurer.config
package expression

import _root_.scala.util.parsing.combinator.syntactical.StandardTokenParsers
import com.efgfp.commons.expression._
import com.github.fng.structurer.config.KnownVariables

object ExpressionParser extends StandardTokenParsers {
  def parse(d: Double): RichExpression = if (d < 0) {
    parse("(" + d + ")")
  } else {
    parse(d.toString)
  }

  def parse(s: String): RichExpression = {
    val result: ParseResult[Expression] = phrase(expr)(new lexical.Scanner(s))
    RichExpression(result.getOrElse(error(result.toString)), s)
  }

  override val lexical = new ExprLexical
  lexical.delimiters ++= List("(", ")", "+", "-", "*", "/", "$$")

  def expr: Parser[Expression] = unary_operation | binary_operation | atom | ("(" ~> expr <~ ")")

  def atom: Parser[Expression] = nameConstant | constant | ("$$" ~> variable <~ "$$")

  def unary_operation: Parser[Expression] = "(" ~> operator ~ expr <~ ")" ^^ {
    case o ~ e => new UnaryOperation(o, e)
  }

  def binary_operation: Parser[Expression] = "(" ~> expr ~ operator ~ expr <~ ")" ^^ {
    case l ~ o ~ r => new BinaryOperation(l, o, r)
  }

  def binary_operation_atoms: Parser[Expression] = expr ~ operator ~ expr <~ ")" ^^ {
    case l ~ o ~ r => new BinaryOperation(l, o, r)
  }

  def variable: Parser[Expression] = ident ^^ {
    s => KnownVariables.forName(s)
  }


  def nameConstant: Parser[Expression] = ident ^^ {
    s => new StringConstant(s)
  }


  def constant: Parser[Constant] = numericLit ^^ {
    s => new Constant(s.toDouble)
  }

  def operator: Parser[Operator] = plus | minus | mul | div

  def plus: Parser[Operator] = "+" ^^ {
    _ => Operator.ADD
  }

  def minus: Parser[Operator] = "-" ^^ {
    _ => Operator.SUB
  }

  def mul: Parser[Operator] = "*" ^^ {
    _ => Operator.MUL
  }

  def div: Parser[Operator] = "/" ^^ {
    _ => Operator.DIV
  }
}
