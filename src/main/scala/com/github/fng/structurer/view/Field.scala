package com.github.fng.structurer
package view

import java.awt.{Color}
import payoff.OptionType
import swing._

abstract class Field[T](val label: String, val valueField: TextField) extends BoxPanel(Orientation.Horizontal) {

  contents += new Label {
    text = label
  }
  contents += valueField

  def getValue: T

}

class StringField(label: String, defaultValue: String)
        extends Field[String](label, new TextField(defaultValue, 10)) {
  def getValue: String = valueField.text
}

class DoubleField(label: String, defaultValue: Double)
        extends Field[Double](label, new VerifiedTextField(defaultValue.toString, TextFieldType.DoubleField)) {
  def getValue: Double = java.lang.Double.valueOf(valueField.text).doubleValue()
}

class OptionTypeField(label: String, defaultValue: OptionType) extends BoxPanel(Orientation.Horizontal) {

  private val optionTypeCallRadio = new RadioButton("Call")
  private val optionTypePutRadio = new RadioButton("Put")
  private val group = new ButtonGroup(optionTypeCallRadio, optionTypePutRadio)

  defaultValue match {
    case OptionType.Call => optionTypeCallRadio.selected = true
    case OptionType.Put => optionTypePutRadio.selected = true
    case _ => {}
  }

  contents += new Label {
    text = label
  }
  contents += new FlowPanel {
    contents += optionTypeCallRadio
    contents += optionTypePutRadio
  }

  def getValue: OptionType = {
    group.selected match {
      case Some(`optionTypeCallRadio`) => OptionType.Call
      case Some(`optionTypePutRadio`) => OptionType.Put
      case _ => error("Choose Option Type!")
    }
  }
}

class VerifiedTextField(defaultValue: String, textFieldType: TextFieldType) extends TextField(defaultValue, 10) {
  val myVerifier = new TextFieldVerifier(this, textFieldType)
  shouldYieldFocus = myVerifier.verify _
}

sealed abstract class TextFieldType(val notVerifiedMessage: String) {
  def verify(value: String): Boolean
}

object TextFieldType {

  case object DoubleField extends TextFieldType("Not a Double!") {
    def verify(value: String): Boolean = try {
      java.lang.Double.valueOf(value)
      true
    } catch {
      case e: Exception =>
        println(e.getMessage)
        false
    }
  }

}

class TextFieldVerifier(textField: TextField, textFieldType: TextFieldType) {
  val originalBackgroundColor = textField.background

  def verify(value: String): Boolean = {
    val isVerified = textFieldType.verify(value)

    if (isVerified) {
      textField.background = originalBackgroundColor
      textField.tooltip = ""
    } else {
      textField.background = Color.RED
      textField.tooltip = textFieldType.notVerifiedMessage
    }

    isVerified
  }

}

