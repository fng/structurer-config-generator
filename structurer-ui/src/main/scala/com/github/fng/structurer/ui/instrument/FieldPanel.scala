package com.github.fng.structurer.ui
package instrument

import swing.{Label, Orientation, BoxPanel}
import com.github.fng.structurer.config.FieldConfig
import instrument.TextFieldType.DoubleField
import com.github.fng.structurer.config.FieldConfig.{DoubleFieldValidationType, ChooseFieldValidationType, ChooseFieldConfig, DoubleFieldConfig}

class FieldPanel extends BoxPanel(Orientation.Vertical) {

  contents += new StringField("Varibale 1", "100")
  contents += new GreaterThanDoubleField("Variable 2", 30, 20)
  contents += new ComboBoxTypeField("Variable 3", List("annually", "semi-annually", "quarterly", "monthly"))

  def refreshFieldPanel(fields: List[FieldConfig]) {
    println("fields: " + fields)
    contents.clear()
    contents ++= fields.map {
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


}