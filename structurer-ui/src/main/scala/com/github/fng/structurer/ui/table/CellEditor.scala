package com.github.fng.structurer.ui.table

import javax.swing.{JTextField, JComboBox, DefaultCellEditor}
import java.awt.Color
import com.github.fng.structurer.ui.instrument.TextFieldType.ExpressionField

object CellEditor {

  class ComboboxCellEditor(values: List[AnyRef]) extends DefaultCellEditor(new JComboBox(values.toArray))

  class ExpressionCellEditor() extends DefaultCellEditor(new JTextField()) {

    val originalBackgroundColor = editorComponent.getBackground

    private def verify(textField: JTextField): Boolean = {
      val textFieldType = ExpressionField
      val isVerified = textFieldType.verify(textField.getText)

      if (isVerified) {
        textField.setBackground(originalBackgroundColor)
        textField.setToolTipText("")
      } else {
        textField.setBackground(Color.RED)
        textField.setToolTipText(textFieldType.notVerifiedMessage)
      }

      isVerified
    }

    override def stopCellEditing(): Boolean = {
      //      println("stopCellEditing editorComponent: " + editorComponent)
      //      val text = editorComponent.asInstanceOf[JTextField].getText
      //      println("text: " + text)

      val isVerified = verify(editorComponent.asInstanceOf[JTextField])
      if (isVerified) {
        super.stopCellEditing()
      } else {
        false
      }
    }
  }

}