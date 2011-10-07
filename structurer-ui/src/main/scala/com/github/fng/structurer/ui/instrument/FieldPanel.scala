package com.github.fng.structurer.ui
package instrument

import swing.{Label, Orientation, BoxPanel}
import com.github.fng.structurer.config.FieldConfig
import instrument.TextFieldType.DoubleField
import com.github.fng.structurer.config.FieldConfig._
import com.efgfp.commons.expression.{Constant, Variable, Expression}
import com.github.fng.structurer.config.expression.{StringConstant, ExpressionParser}

class FieldPanel extends BoxPanel(Orientation.Vertical) {

  //  contents += new StringField("String", "100")
  //  contents += new GreaterThanDoubleField("Double", 30, 20)
  //  contents += new ComboBoxTypeField("Choise", List("annually", "semi-annually", "quarterly", "monthly"))
  //  contents += new RangeDoubleField("Range", 20, 10, 30)
  //  contents += new ExpressionField("Expression", ExpressionParser.parse(0))

  def refreshFieldPanel(fields: List[FieldConfig]) {
    println("fields: " + fields)
    contents.clear()
    contents ++= fields.map {
      case DoubleRangeFieldConfig(name, from, to) => new RangeDoubleField(name, 0, from, to)
      case DoubleFieldConfig(name, validationType, level) => validationType match {
        case DoubleFieldValidationType.GreaterThan => new GreaterThanDoubleField(name, 0, level)
        case DoubleFieldValidationType.GreaterThanEqual => new GreaterThanEqualDoubleField(name, 0, level)
        case DoubleFieldValidationType.LessThan => new LessThanDoubleField(name, 0, level)
        case DoubleFieldValidationType.LessThanEqual => new LessThanEqualDoubleField(name, 0, level)
      }

      case ChooseFieldConfig(name, validationType, values) => validationType match {
        case ChooseFieldValidationType.OneOf => new ComboBoxTypeField(name, values)
      }
    }
  }


  def getFieldsWithValues: Map[Expression, Expression] = {
    contents.toList.map {
      case doubleField: Field[Double] => List(new StringConstant(doubleField.label) -> new Constant(doubleField.getValue))
      case otherField: Field[_] => error("field of type: " + otherField + " is not supported!")
      case combobox: ComboBoxTypeField => Nil
      case other => {
        error(other + " is not a field!")
      }
    }.flatten.toMap
  }


}