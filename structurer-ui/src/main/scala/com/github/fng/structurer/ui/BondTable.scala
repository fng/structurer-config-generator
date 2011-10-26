package com.github.fng.structurer.ui

import instrument.ExpressionBond
import com.github.fng.structurer.config.expression.{ExpressionParser, RichExpression}
import table.CellEditor.{ButtonTableCellEditor, ExpressionCellEditor}
import swing.{Button, Component, Table}
import swing.Table.ElementMode
import table.GenericTableModel.{ComponentCellRenderer, Column}
import table.{GenericTable, GenericTableModel}
import swing.event.Event


object BondTable {

  val columns = {
    List(
      new Column[MutableBond]("Notional", true, (bond: MutableBond) => bond.notional.originalString) {
        updateHandler = (bond: MutableBond, newValue: AnyRef) => bond.notional = newValue match {
          case s: String => ExpressionParser.parse(s)
          case other => sys.error(other.getClass + " is not supported for notional field")
        }
        customCellEditor = new ExpressionCellEditor()
      },
      new Column[MutableBond]("Quantity", true, (bond: MutableBond) => bond.quantity.originalString) {
        updateHandler = (bond: MutableBond, newValue: AnyRef) => bond.quantity = newValue match {
          case s: String => ExpressionParser.parse(s)
          case other => sys.error(other.getClass + " is not supported for quantity field")
        }
        customCellEditor = new ExpressionCellEditor()
      },
      new Column[MutableBond]("Delete", true, (bond: MutableBond) => "Remove") {
        updateHandler = (bond: MutableBond, newValue: AnyRef) => {}
        customCellEditor = new ButtonTableCellEditor((row) => {
          println("row to Remove: " + row);
          columnEventPublisher.publish(DeleteBondTableRowEvent(row))
        })
        customCellRenderer = new ComponentCellRenderer[MutableBond] {
          def rendererComponent(tableModel: GenericTableModel[MutableBond], isSelected: Boolean, focused: Boolean, row: Int, column: Int): Component = new Button(tableModel.getValueAt(row, column).toString)
        }
      }

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