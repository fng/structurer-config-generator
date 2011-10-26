package com.github.fng.structurer.ui

import instrument.ExpressionOption
import swing.Table
import com.github.fng.structurer.instrument.{OptionBarrierType, OptionType}
import com.github.fng.structurer.config.expression.{ExpressionParser, RichExpression}
import com.github.fng.structurer.ui.table.GenericTableModel
import com.github.fng.structurer.ui.table.GenericTableModel.Column
import table.CellEditor.{ComboboxCellEditor, ExpressionCellEditor}

class OptionTable(options: List[MutableOption]) extends Table {

  val columns = List(
    Column[MutableOption]("OptionType", true, _.optionType,
      update = (option, newValue) => option.optionType = newValue.asInstanceOf[OptionType],
      customCellEditor = Some(new ComboboxCellEditor(List(OptionType.Call, OptionType.Put)))),
    Column[MutableOption]("Strike", true, _.strike.originalString,
      update = (option, newValue) => option.strike = newValue match {
        case s: String => ExpressionParser.parse(s)
        case other => sys.error(other.getClass + " is not supported for strike field")
      },
      customCellEditor = Some(new ExpressionCellEditor())),
    Column[MutableOption]("Quantity", true, _.quantity.originalString,
      update = (option, newValue) => option.quantity = newValue match {
        case s: String => ExpressionParser.parse(s)
        case other => sys.error(other.getClass + " is not supported for quantity field")
      },
      customCellEditor = Some(new ExpressionCellEditor())),
    Column[MutableOption]("Notional", true, _.notional.originalString,
      update = (option, newValue) => option.notional = newValue match {
        case s: String => ExpressionParser.parse(s)
        case other => sys.error(other.getClass + " is not supported for notional field")
      },
      customCellEditor = Some(new ExpressionCellEditor())),
    Column[MutableOption]("BarrierType", true, _.optionBarrierType,
      update = (option, newValue) => option.optionBarrierType = newValue.asInstanceOf[OptionBarrierType],
      customCellEditor = Some(new ComboboxCellEditor(List(OptionBarrierType.NoBarrier, OptionBarrierType.KnockInBarrier, OptionBarrierType.KnockOutBarrier))))
  )


  val tableModel = new GenericTableModel[MutableOption](columns, options.toBuffer)
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

  override protected def editor(row: Int, column: Int) = columns(column).customCellEditor match {
    case Some(editor) => editor
    case None => super.editor(row, column)
  }

}


case class MutableOption(var optionType: OptionType, var strike: RichExpression, var quantity: RichExpression,
                         var notional: RichExpression,
                         var optionBarrierType: OptionBarrierType)

object MutableOption {
  def apply(option: ExpressionOption): MutableOption =
    MutableOption(option.optionType, option.strike, option.quantity, option.notional, option.optionBarrierType)

}