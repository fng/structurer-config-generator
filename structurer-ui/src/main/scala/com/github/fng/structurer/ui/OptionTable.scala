package com.github.fng.structurer.ui

import instrument.TextFieldType.ExpressionField
import instrument.{TextFieldVerifier, ExpressionOption}
import swing.Table
import collection.mutable.Buffer
import javax.swing.table.{TableCellEditor, AbstractTableModel}
import com.github.fng.structurer.instrument.{OptionBarrierType, OptionType}
import com.github.fng.structurer.config.expression.{ExpressionParser, RichExpression}
import com.github.fng.structurer.ui.table.GenericTableModel
import com.github.fng.structurer.ui.table.GenericTableModel.Column
import javax.swing._
import java.awt.Color

class OptionTable(options: List[MutableOption]) extends Table {

  val tableModel = new GenericTableModel[MutableOption](OptionTable.columns, options.toBuffer)
  model = tableModel

  autoResizeMode = Table.AutoResizeMode.AllColumns

  peer.getColumnModel.getColumn(1).setPreferredWidth(150)
  peer.getColumnModel.getColumn(2).setPreferredWidth(150)
  peer.getColumnModel.getColumn(3).setPreferredWidth(100)

  def add(option: MutableOption) {
    tableModel.add(option)
  }

  def removeOne {
    tableModel.removeOne
  }


  def updateWithNewList(options: List[MutableOption]) {
    tableModel.updateWithNewList(options)
  }

  override protected def editor(row: Int, column: Int) = OptionTable.columns(column).customCellEditor match {
    case Some(editor) => editor
    case None => super.editor(row, column)
  }

}


object OptionTable {


  val columns = List(
    Column[MutableOption]("OptionType", true, _.optionType,
      update = (option, newValue) => option.optionType = newValue.asInstanceOf[OptionType],
      customCellEditor = Some(new OptionTable.ComboboxCellEditor(List(OptionType.Call, OptionType.Put)))),
    Column[MutableOption]("Strike", true, _.strike.originalString,
      update = (option, newValue) => option.strike = newValue match {
        case s: String => ExpressionParser.parse(s)
        case other => sys.error(other.getClass + " is not supported for strike field")
      },
      customCellEditor = Some(new OptionTable.ExpressionCellEditor())),
    Column[MutableOption]("Quantity", true, _.quantity.originalString,
      update = (option, newValue) => option.quantity = newValue match {
        case s: String => ExpressionParser.parse(s)
        case other => sys.error(other.getClass + " is not supported for quantity field")
      },
      customCellEditor = Some(new OptionTable.ExpressionCellEditor())),
    Column[MutableOption]("Notional", true, _.notional.originalString,
      update = (option, newValue) => option.notional = newValue match {
        case s: String => ExpressionParser.parse(s)
        case other => sys.error(other.getClass + " is not supported for notional field")
      },
      customCellEditor = Some(new OptionTable.ExpressionCellEditor())),
    Column[MutableOption]("BarrierType", true, _.optionBarrierType,
      update = (option, newValue) => option.optionBarrierType = newValue.asInstanceOf[OptionBarrierType],
      customCellEditor = Some(new OptionTable.ComboboxCellEditor(List(OptionBarrierType.NoBarrier, OptionBarrierType.KnockInBarrier, OptionBarrierType.KnockOutBarrier))))
  )


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

case class MutableOption(var optionType: OptionType, var strike: RichExpression, var quantity: RichExpression,
                         var notional: RichExpression,
                         var optionBarrierType: OptionBarrierType)

object MutableOption {
  def apply(option: ExpressionOption): MutableOption =
    MutableOption(option.optionType, option.strike, option.quantity, option.notional, option.optionBarrierType)

}