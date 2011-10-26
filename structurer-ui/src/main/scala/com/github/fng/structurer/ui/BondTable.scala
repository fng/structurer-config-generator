package com.github.fng.structurer.ui

import instrument.ExpressionBond
import swing.Table
import com.github.fng.structurer.config.expression.{ExpressionParser, RichExpression}
import com.github.fng.structurer.ui.table.GenericTableModel
import com.github.fng.structurer.ui.table.GenericTableModel.Column
import table.CellEditor.ExpressionCellEditor

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
      customCellEditor = Some(new ExpressionCellEditor()))
  )


  val tableModel = new GenericTableModel[MutableBond](columns, bonds.toBuffer)
  model = tableModel

  autoResizeMode = Table.AutoResizeMode.AllColumns


  def add(bond: MutableBond) {
    tableModel.add(bond)
  }

  def removeOne {
    tableModel.removeOne
  }


  def updateWithNewList(bonds: List[MutableBond]) {
    tableModel.updateWithNewList(bonds)
  }

  override protected def editor(row: Int, column: Int) = columns(column).customCellEditor match {
    case Some(editor) => editor
    case None => super.editor(row, column)
  }

  def getBonds: List[ExpressionBond] = {
    tableModel.values.map(_.toExpressionBond).toList
  }

}


case class MutableBond(var notional: RichExpression, var quantity: RichExpression) {
  def toExpressionBond: ExpressionBond = ExpressionBond(notional, quantity)
}

object MutableBond {
  def apply(bond: ExpressionBond): MutableBond =
    MutableBond(bond.notional, bond.quantity)

}