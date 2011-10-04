package com.github.fng.structurer.ui
package instrument

import java.awt.{Color}
import swing._
import com.github.fng.structurer.instrument.{QuotationType, OptionBarrierType, OptionType}

abstract class Field[T](val label: String, val valueField: TextField) extends BoxPanel(Orientation.Horizontal) {

  contents += new Label {
    text = label
  }
  contents += valueField

  def getValue: T

  def setValue(value: T)

}

class StringField(label: String, defaultValue: String)
  extends Field[String](label, new TextField(defaultValue, 10)) {
  def getValue: String = valueField.text

  def setValue(value: String) {
    valueField.text = value
  }
}

class DoubleField(label: String, defaultValue: Double)
  extends Field[Double](label, new VerifiedTextField(defaultValue.toString, TextFieldType.DoubleField)) {
  def getValue: Double = java.lang.Double.valueOf(valueField.text).doubleValue()

  def setValue(value: Double) {
    valueField.text = value.toString
  }
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

class QuotationTypeField(label: String, defaultValue: QuotationType) extends BoxPanel(Orientation.Horizontal) {

  private val unitRadio = new RadioButton("Unit")
  private val notionalRadio = new RadioButton("Notional")
  private val group = new ButtonGroup(unitRadio, notionalRadio)

  setValue(defaultValue)

  contents += new Label {
    text = label
  }
  contents += new FlowPanel {
    contents += unitRadio
    contents += notionalRadio
  }

  def getValue: QuotationType = {
    group.selected match {
      case Some(`unitRadio`) => QuotationType.Unit
      case Some(`notionalRadio`) => QuotationType.Notional
      case _ => error("Choose Quotation Type!")
    }
  }

  def setValue(quotationType: QuotationType) {
    quotationType match {
      case QuotationType.Unit => unitRadio.selected = true
      case QuotationType.Notional => notionalRadio.selected = true
      case _ => {}
    }
  }
}


class OptionBarrierTypeField(label: String, defaultValue: OptionBarrierType) extends BoxPanel(Orientation.Horizontal) {

  private val noBarrierRadio = new RadioButton("Call")
  private val knockInRadio = new RadioButton("Put")
  private val knockOutRadio = new RadioButton("Put")
  private val group = new ButtonGroup(noBarrierRadio, knockInRadio, knockOutRadio)

  defaultValue match {
    case OptionBarrierType.NoBarrier => noBarrierRadio.selected = true
    case OptionBarrierType.KnockInBarrier => knockInRadio.selected = true
    case OptionBarrierType.KnockOutBarrier => knockOutRadio.selected = true
    case _ => {}
  }

  contents += new Label {
    text = label
  }
  contents += new FlowPanel {
    contents += noBarrierRadio
    contents += knockInRadio
    contents += knockOutRadio
  }

  def getValue: OptionBarrierType = {
    group.selected match {
      case Some(`noBarrierRadio`) => OptionBarrierType.NoBarrier
      case Some(`knockInRadio`) => OptionBarrierType.KnockInBarrier
      case Some(`knockOutRadio`) => OptionBarrierType.KnockOutBarrier
      case _ => error("Choose Option Barrier Type!")
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

