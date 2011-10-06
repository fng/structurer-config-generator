package com.github.fng.structurer.ui
package instrument

import java.awt.{Color}
import swing._
import com.github.fng.structurer.instrument.{PayoffType, QuotationType, OptionBarrierType, OptionType}

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

abstract class ConstrainedDoubleField(label: String, defaultValue: Double, textFieldType: TextFieldType.ConstrainedDoubleField)
  extends Field[Double](label + "(" + textFieldType.constraint + " " + textFieldType.level + ")", new VerifiedTextField(defaultValue.toString, textFieldType)) {
  def getValue: Double = java.lang.Double.valueOf(valueField.text).doubleValue()

  def setValue(value: Double) {
    valueField.text = value.toString
  }
}

class GreaterThanDoubleField(label: String, defaultValue: Double, level: Double)
  extends ConstrainedDoubleField(label, defaultValue, new TextFieldType.GreaterThanDoubleField(level))

class GreaterThanEqualDoubleField(label: String, defaultValue: Double, level: Double)
  extends ConstrainedDoubleField(label, defaultValue, new TextFieldType.GreaterThanEqualDoubleField(level))

class LessThanDoubleField(label: String, defaultValue: Double, level: Double)
  extends ConstrainedDoubleField(label, defaultValue, new TextFieldType.LessThanDoubleField(level))

class LessThanEqualDoubleField(label: String, defaultValue: Double, level: Double)
  extends ConstrainedDoubleField(label, defaultValue, new TextFieldType.LessThanEqualDoubleField(level))


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


class PayoffTypeField(label: String, defaultValue: PayoffType) extends BoxPanel(Orientation.Horizontal) {

  private val bullishRadio = new RadioButton("Bullish")
  private val bearishRadio = new RadioButton("Bearish")
  private val group = new ButtonGroup(bullishRadio, bearishRadio)

  setValue(defaultValue)

  contents += new Label {
    text = label
  }
  contents += new FlowPanel {
    contents += bullishRadio
    contents += bearishRadio
  }

  def getValue: PayoffType = {
    group.selected match {
      case Some(`bullishRadio`) => PayoffType.Bullish
      case Some(`bearishRadio`) => PayoffType.Bearish
      case _ => error("Choose Payoff Type!")
    }
  }

  def setValue(payoffType: PayoffType) {
    payoffType match {
      case PayoffType.Bullish => bullishRadio.selected = true
      case PayoffType.Bearish => bearishRadio.selected = true
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


class RadioTypeField(label: String, options: List[String]) extends BoxPanel(Orientation.Horizontal) {

  private val radioButtons = options.map(new RadioButton(_))
  private val group = new ButtonGroup(radioButtons: _*)
  radioButtons.headOption.foreach {
    radioButton => radioButton.selected = true
  }

  contents += new Label {
    text = label
  }
  contents += new FlowPanel {
    contents ++= radioButtons
  }

  def getValue: String = group.selected match {
    case Some(radioButton) => radioButton.text
    case None => error("please choose a value!")
  }

}

class ComboBoxTypeField(label: String, options: List[String]) extends BoxPanel(Orientation.Horizontal) {

  private val comboBox = new ComboBox[String](options)

  contents += new Label {
    text = label
  }
  contents += new FlowPanel {
    contents += comboBox
  }

  def getValue: String = comboBox.selection.item
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


  abstract class ConstrainedDoubleField(val level: Double, val constraint: String, valueConstraintLevel: (Double, Double) => Boolean)
    extends TextFieldType("Not " + constraint + " " + level) {
    def verify(value: String): Boolean = try {
      valueConstraintLevel(java.lang.Double.valueOf(value).doubleValue(), level)
    } catch {
      case e: Exception =>
        println(e.getMessage)
        false
    }

  }

   class GreaterThanDoubleField(level: Double) extends ConstrainedDoubleField(level, ">", (value, level) => value > level)

   class GreaterThanEqualDoubleField(level: Double) extends ConstrainedDoubleField(level, ">=", (value, level) => value >= level)

   class LessThanDoubleField(level: Double) extends ConstrainedDoubleField(level, "<", (value, level) => value < level)

   class LessThanEqualDoubleField(level: Double) extends ConstrainedDoubleField(level, "<=", (value, level) => value <= level)


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

