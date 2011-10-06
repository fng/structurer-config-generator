package com.github.fng.structurer.ui
package instrument

import swing.{Label, Orientation, BoxPanel}
import com.github.fng.structurer.config.FieldConfig

class FieldPanel extends BoxPanel(Orientation.Vertical) {

  contents += new Label("Variable 1")
  contents += new Label("Variable 2")
  contents += new Label("Variable 3")

  def refreshFieldPanel(fields: List[FieldConfig]) {
    println("fields: " + fields)
    contents.clear()
    contents ++= fields.map {
      fieldConfig => new Label(fieldConfig.name)
    }
  }


}