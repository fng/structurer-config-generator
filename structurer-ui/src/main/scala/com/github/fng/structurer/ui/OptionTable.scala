package com.github.fng.structurer.ui

import instrument.ExpressionOption
import com.github.fng.structurer.instrument.{OptionBarrierType, OptionType}
import com.github.fng.structurer.config.expression.{ExpressionParser, RichExpression}
import table.CellEditor.{ButtonTableCellEditor, ComboboxCellEditor, ExpressionCellEditor}
import swing.Table.ElementMode
import table.GenericTableModel.{EditableMode, ComponentCellRenderer, Column}
import table.{GenericTable, GenericTableModel}
import swing.{Component, Button, Table}
import swing.event.Event

object OptionTable {
  val columns = List(
    new Column[MutableOption, OptionType]("OptionType", EditableMode.IsEditable[MutableOption], (option: MutableOption) => option.optionType) {
      updateHandler = (option: MutableOption, newValue: OptionType) => option.optionType = newValue
      customCellEditor = new ComboboxCellEditor[MutableOption](List(OptionType.Call, OptionType.Put))
    },
    new Column[MutableOption, String]("Strike", EditableMode.IsEditable[MutableOption], (option: MutableOption) => option.strike.originalString) {
      updateHandler = (option: MutableOption, newValue: String) => option.strike = ExpressionParser.parse(newValue)
      customCellEditor = new ExpressionCellEditor[MutableOption]()
    },
    new Column[MutableOption, String]("Quantity", EditableMode.IsEditable[MutableOption], (option: MutableOption) => option.quantity.originalString) {
      updateHandler = (option: MutableOption, newValue: String) => option.quantity = ExpressionParser.parse(newValue)
      customCellEditor = new ExpressionCellEditor[MutableOption]()
    },
    new Column[MutableOption, String]("Notional", EditableMode.IsEditable[MutableOption], (option: MutableOption) => option.notional.originalString) {
      updateHandler = (option: MutableOption, newValue: String) => option.notional = ExpressionParser.parse(newValue)
      customCellEditor = new ExpressionCellEditor[MutableOption]()
    },
    new Column[MutableOption, OptionBarrierType]("OptionBarrierType", EditableMode.IsEditable[MutableOption], (option: MutableOption) => option.optionBarrierType) {
      updateHandler = (option: MutableOption, newValue: OptionBarrierType) => option.optionBarrierType = newValue
      customCellEditor = new ComboboxCellEditor[MutableOption](List(OptionBarrierType.NoBarrier, OptionBarrierType.KnockInBarrier,
        OptionBarrierType.KnockOutBarrier))
    },
    new Column[MutableOption, String]("Remove", EditableMode.IsEditable[MutableOption], (option: MutableOption) => "Remove") {
      updateHandler = (option: MutableOption, newValue: String) => {}
      customCellEditor = new ButtonTableCellEditor[MutableOption]((row) => {
        columnEventPublisher.publish(DeleteOptionTableRowEvent(row))
      })
      customCellRenderer = new ComponentCellRenderer[MutableOption] {
        def rendererComponent(tableModel: GenericTableModel[MutableOption], isSelected: Boolean, focused: Boolean, row: Int,
                              column: Int, defaultRendererComponent: () => Component): Component = new Button(tableModel.getValueAt(row, column).toString)
      }
    }
  )


  case class DeleteOptionTableRowEvent(row: Int) extends Event

}


class OptionTable(options: List[MutableOption]) extends GenericTable[MutableOption](OptionTable.columns, options) {

  peer.getColumnModel.getColumn(0).setPreferredWidth(50)
  peer.getColumnModel.getColumn(1).setPreferredWidth(200)
  peer.getColumnModel.getColumn(2).setPreferredWidth(200)
  peer.getColumnModel.getColumn(3).setPreferredWidth(100)
  peer.getColumnModel.getColumn(5).setPreferredWidth(60)

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