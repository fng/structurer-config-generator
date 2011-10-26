package com.github.fng.structurer.ui

import instrument.ExpressionOption
import com.github.fng.structurer.instrument.{OptionBarrierType, OptionType}
import com.github.fng.structurer.config.expression.{ExpressionParser, RichExpression}
import table.CellEditor.{ButtonTableCellEditor, ComboboxCellEditor, ExpressionCellEditor}
import swing.Table.ElementMode
import table.GenericTableModel.{ComponentCellRenderer, Column}
import table.{GenericTable, GenericTableModel}
import swing.{Publisher, Component, Button, Table}
import swing.event.Event

object OptionTable {
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
    Column[MutableOption]("Delete", true, _ => "Remove",
      update = (option, newValue) => {},
      customCellEditor = Some(new ButtonTableCellEditor((row) => {
        println("row to Remove: " + row);
        //        tableModel.removeRow(row)
        OptionTableRemoveRowPublisher.publish(DeleteOptionTableRowEvent(row))
      })),
      customCellRenderer = Some(new ComponentCellRenderer[MutableOption] {
        def rendererComponent(tableModel: GenericTableModel[MutableOption], isSelected: Boolean, focused: Boolean, row: Int, column: Int): Component = new Button(tableModel.getValueAt(row, column).toString)
      }))
  )

  case object OptionTableRemoveRowPublisher extends Publisher

  case class DeleteOptionTableRowEvent(row: Int) extends Event

}


class OptionTable(options: List[MutableOption]) extends GenericTable[MutableOption](OptionTable.columns, options) {

  listenTo(OptionTable.OptionTableRemoveRowPublisher)

  autoResizeMode = Table.AutoResizeMode.AllColumns
  selection.elementMode = ElementMode.Cell

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

  def getOptions: List[ExpressionOption] = {
    tableModel.values.map(_.toExpressionOption).toList
  }

  reactions += {
    case OptionTable.DeleteOptionTableRowEvent(row) => tableModel.removeRow(row)
  }

}


case class MutableOption(var optionType: OptionType, var strike: RichExpression, var quantity: RichExpression,
                         var notional: RichExpression,
                         var optionBarrierType: OptionBarrierType) {
  def toExpressionOption: ExpressionOption = ExpressionOption(optionType, strike, quantity, notional, optionBarrierType)
}

object MutableOption {
  def apply(option: ExpressionOption): MutableOption =
    MutableOption(option.optionType, option.strike, option.quantity, option.notional, option.optionBarrierType)

}