package com.github.fng.structurer.ui

import instrument.ExpressionBond
import com.github.fng.structurer.config.expression.{ExpressionParser, RichExpression}
import table.CellEditor.{ButtonTableCellEditor, ExpressionCellEditor}
import swing.{Button, Component, Table}
import swing.Table.ElementMode
import table.GenericTableModel.{ColumnEventPublisher, ComponentCellRenderer, Column}
import table.{GenericTable, GenericTableModel}
import swing.event.Event


object BondTable {
  val columns = {
    val deleteColumnEventPublisher = new ColumnEventPublisher
    List(
      Column[MutableBond]("Notional", true, _.notional.originalString,
        update = (bond, newValue) => bond.notional = newValue match {
          case s: String => ExpressionParser.parse(s)
          case other => sys.error(other.getClass + " is not supported for notional field")
        },
        customCellEditor = Some(new ExpressionCellEditor())),
      Column[MutableBond]("Quantity", true, _.quantity.originalString,
        update = (bond, newValue) => bond.quantity = newValue match {
          case s: String => ExpressionParser.parse(s)
          case other => sys.error(other.getClass + " is not supported for quantity field")
        },
        customCellEditor = Some(new ExpressionCellEditor())),
      Column[MutableBond]("Delete", true, _ => "Remove",
        update = (bond, newValue) => {},
        customCellEditor = Some(new ButtonTableCellEditor((row) => {
          println("row to Remove: " + row);
          deleteColumnEventPublisher.publish(DeleteBondTableRowEvent(row))
        })),
        customCellRenderer = Some(new ComponentCellRenderer[MutableBond] {
          def rendererComponent(tableModel: GenericTableModel[MutableBond], isSelected: Boolean, focused: Boolean, row: Int, column: Int): Component = new Button(tableModel.getValueAt(row, column).toString)
        }),
        columnEventPublisher = deleteColumnEventPublisher)
    )

  }

  case class DeleteBondTableRowEvent(row: Int) extends Event

}


class BondTable(bonds: List[MutableBond]) extends GenericTable(BondTable.columns, bonds) {


  autoResizeMode = Table.AutoResizeMode.AllColumns
  selection.elementMode = ElementMode.Cell


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