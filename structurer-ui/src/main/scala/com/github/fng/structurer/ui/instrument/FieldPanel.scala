package com.github.fng.structurer.ui
package instrument

import swing.{Label, Orientation, BoxPanel}
import instrument.TextFieldType.DoubleField
import com.github.fng.structurer.config.FieldConfig._
import com.github.fng.structurer.config.expression.{StringConstant, ExpressionParser}
import com.efgfp.commons.expression.{Variable, Constant, Expression}
import com.github.fng.structurer.config.{KnownVariables, FieldConfig}

class FieldPanel extends BoxPanel(Orientation.Vertical) {

  //  contents += new StringField("String", "100")
  //  contents += new GreaterThanDoubleField("Double", 30, 20)
  //  contents += new ComboBoxTypeField("Choise", List("annually", "semi-annually", "quarterly", "monthly"))
  //  contents += new RangeDoubleField("Range", 20, 10, 30)
  //  contents += new ExpressionField("Expression", ExpressionParser.parse(0))

  def refreshFieldPanel(fields: List[FieldConfig]) {
    contents.clear()
    contents ++= fields.map {
      case DoubleRangeFieldConfig(name, from, to, default) => new RangeDoubleField(name, default.getOrElse(0), from, to)
      case DoubleFieldConfig(name, validationType, level, default) => validationType match {
        case DoubleFieldValidationType.GreaterThan => new GreaterThanDoubleField(name, default.getOrElse(0), level)
        case DoubleFieldValidationType.GreaterThanEqual => new GreaterThanEqualDoubleField(name, default.getOrElse(0), level)
        case DoubleFieldValidationType.LessThan => new LessThanDoubleField(name, default.getOrElse(0), level)
        case DoubleFieldValidationType.LessThanEqual => new LessThanEqualDoubleField(name, default.getOrElse(0), level)
      }

      case ChooseFieldConfig(name, validationType, values, default) => validationType match {
        case ChooseFieldValidationType.OneOf => new ComboBoxTypeField(name, values, default)
      }
    }
  }


  def getFieldsWithValues: Map[Expression, Expression] = {
    contents.toList.map {
      case doubleField: Field[Double] => List(KnownVariables.forName(doubleField.label) -> new Constant(doubleField.getValue))
      case otherField: Field[_] => error("field of type: " + otherField + " is not supported!")
      case combobox: ComboBoxTypeField => Nil
      case other => {
        error(other + " is not a field!")
      }
    }.flatten.toMap
  }


}