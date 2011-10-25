package com.github.fng.structurer.ui

import instrument.ExpressionOption
import swing.Table
import collection.mutable.Buffer
import javax.swing.{JComboBox, DefaultCellEditor}
import javax.swing.table.{TableCellEditor, AbstractTableModel}
import com.github.fng.structurer.instrument.{OptionBarrierType, OptionType}
import com.github.fng.structurer.config.expression.{ExpressionParser, RichExpression}
import table.GenericTableModel
import table.GenericTableModel.Column

class OptionTable(options: List[MutableOption]) extends Table {

  val tableModel = new GenericTableModel[MutableOption](OptionTable.columns, options.toBuffer)
  model = tableModel

  autoResizeMode = Table.AutoResizeMode.AllColumns

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
        case other => error(other.getClass + " is not supported for strike field")
      }),
    Column[MutableOption]("Quantity", true, _.quantity.originalString,
      update = (option, newValue) => option.quantity = newValue match {
        case s: String => ExpressionParser.parse(s)
        case other => error(other.getClass + " is not supported for quantity field")
      }),
    Column[MutableOption]("Notional", true, _.notional.originalString,
      update = (option, newValue) => option.notional = newValue match {
        case s: String => ExpressionParser.parse(s)
        case other => error(other.getClass + " is not supported for notional field")
      }),
    Column[MutableOption]("BarrierType", true, _.optionBarrierType,
      update = (option, newValue) => option.optionBarrierType = newValue.asInstanceOf[OptionBarrierType],
      customCellEditor = Some(new OptionTable.ComboboxCellEditor(List(OptionBarrierType.NoBarrier, OptionBarrierType.KnockInBarrier, OptionBarrierType.KnockOutBarrier))))
  )
  


  class ComboboxCellEditor(values: List[AnyRef]) extends DefaultCellEditor(new JComboBox(values.toArray))

}

case class MutableOption(var optionType: OptionType, var strike: RichExpression, var quantity: RichExpression,
                         var notional: RichExpression,
                         var optionBarrierType: OptionBarrierType)

object MutableOption {
  def apply(option: ExpressionOption): MutableOption =
    MutableOption(option.optionType, option.strike, option.quantity, option.notional, option.optionBarrierType)

}