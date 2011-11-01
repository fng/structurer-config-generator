package com.github.fng.structurer.ui

import instrument.TextFieldType.ExpressionField
import javax.swing.{JTextField, DefaultCellEditor}
import javax.swing.DefaultCellEditor._
import java.awt.Color
import com.github.fng.commonsscalaswing.table.{GenericTableModel, ComponentCellEditor}

class ExpressionCellEditor[T]() extends ComponentCellEditor[T] {
    val theEditor = new DefaultCellEditor(new JTextField()) {

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
        val isVerified = verify(editorComponent.asInstanceOf[JTextField])
        if (isVerified) {
          super.stopCellEditing()
        } else {
          false
        }
      }
    }

    def editor(tableModel: GenericTableModel[T], row: Int, column: Int) = theEditor
  }
