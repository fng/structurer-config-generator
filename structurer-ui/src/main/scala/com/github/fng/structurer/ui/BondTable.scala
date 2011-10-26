package com.github.fng.structurer.ui

import instrument.ExpressionBond
import com.github.fng.structurer.config.expression.{ExpressionParser, RichExpression}
import com.github.fng.structurer.ui.table.GenericTableModel
import table.CellEditor.{ButtonTableCellEditor, ExpressionCellEditor}
import swing.{Button, Component, Table}
import table.GenericTableModel.{ComponentCellRenderer, Column}
import swing.Table.ElementMode

class BondTable(bonds: List[MutableBond]) extends Table {

  val columns = List(
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
        tableModel.removeRow(row)
      })),
      customCellRenderer = Some(new ComponentCellRenderer {
        def rendererComponent(isSelected: Boolean, focused: Boolean, row: Int, column: Int): Component = new Button(tableModel.getValueAt(row, column).toString)
      }))
  )


  val tableModel: GenericTableModel[MutableBond] = new GenericTableModel[MutableBond](columns, bonds.toBuffer)
  model = tableModel

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

  override protected def editor(row: Int, column: Int) = columns(column).customCellEditor match {
    case Some(editor) => editor
    case None => super.editor(row, column)
  }

  override protected def rendererComponent(isSelected: Boolean, focused: Boolean, row: Int, column: Int): Component = columns(column).customCellRenderer match {
    case Some(renderer) => renderer.rendererComponent(isSelected, focused, row, column)
    case None => super.rendererComponent(isSelected, focused, row, column)
  }


}


case class MutableBond(var notional: RichExpression, var quantity: RichExpression) {
  def toExpressionBond: ExpressionBond = ExpressionBond(notional, quantity)
}

object MutableBond {
  def apply(bond: ExpressionBond): MutableBond =
    MutableBond(bond.notional, bond.quantity)

}