package com.github.fng.structurer.ui

import instrument.ExpressionBond
import com.github.fng.structurer.config.expression.{ExpressionParser, RichExpression}
import swing.{Button, Component, Table}
import swing.Table.ElementMode
import swing.event.Event
import com.github.fng.commonsscalaswing.table.CellEditor.ButtonTableCellEditor
import com.github.fng.commonsscalaswing.table.GenericTableModel.{EditableMode, ComponentCellRenderer, Column}
import com.github.fng.commonsscalaswing.table.{GenericTable, GenericTableModel}

object BondTable {

  val columns = List(
    new Column[MutableBond, String]("Notional", EditableMode.IsEditable[MutableBond], (bond: MutableBond) => bond.notional.originalString) {
      updateHandler = (bond: MutableBond, newValue: String) => bond.notional = ExpressionParser.parse(newValue)
      customCellEditor = new ExpressionCellEditor[MutableBond]()
    },
    new Column[MutableBond, String]("Quantity", EditableMode.IsEditable[MutableBond], (bond: MutableBond) => bond.quantity.originalString) {
      updateHandler = (bond: MutableBond, newValue: String) => bond.quantity = ExpressionParser.parse(newValue)
      customCellEditor = new ExpressionCellEditor[MutableBond]()
    },
    new Column[MutableBond, String]("Delete", EditableMode.IsEditable[MutableBond], (bond: MutableBond) => "Remove") {
      updateHandler = (bond: MutableBond, newValue: String) => {}
      customCellEditor = new ButtonTableCellEditor[MutableBond]((row) => {
        columnEventPublisher.publish(DeleteBondTableRowEvent(row))
      })
      customCellRenderer = new ComponentCellRenderer[MutableBond] {
        def rendererComponent(tableModel: GenericTableModel[MutableBond], isSelected: Boolean, focused: Boolean, row: Int,
                              column: Int, defaultRendererComponent: () => Component): Component = new Button(tableModel.getValueAt(row, column).toString)
      }
    }

  )


  case class DeleteBondTableRowEvent(row: Int) extends Event

}


class BondTable(bonds: List[MutableBond] = Nil) extends GenericTable(BondTable.columns, bonds) {


  def add(bond: MutableBond) {
    tableModel.add(bond)
  }

  def removeOne {
    tableModel.removeOne
  }


  def updateWithNewList(bonds: List[MutableBond]) {
    tableModel.updateWithNewList(bonds)
  }

  def getBonds: List[ExpressionBond] = {
    tableModel.values.map(_.toExpressionBond).toList
  }

  reactions += {
    case BondTable.DeleteBondTableRowEvent(row) => tableModel.removeRow(row)
  }

}


case class MutableBond(var notional: RichExpression, var quantity: RichExpression) {
  def toExpressionBond: ExpressionBond = ExpressionBond(notional, quantity)
}

object MutableBond {
  def apply(bond: ExpressionBond): MutableBond =
    MutableBond(bond.notional, bond.quantity)

}