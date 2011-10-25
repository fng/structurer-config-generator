package com.github.fng.structurer.ui

import instrument.ExpressionOption
import javax.swing.table.AbstractTableModel
import swing.Table
import collection.mutable.Buffer


class OptionTable(options: List[ExpressionOption]) extends Table {

  val tableModel: OptionTable.OptionTableModel = new OptionTable.OptionTableModel(options.toBuffer)
  model = tableModel

  def add(option: ExpressionOption) {
    tableModel.add(option)
  }

  def removeOne {
    tableModel.removeOne
  }


  def updateWithNewList(options: List[ExpressionOption]) {
    tableModel.updateWithNewList(options)
  }
}

object OptionTable {
  private val columns = List("OptionType", "Strike", "Quantity", "Notional", "BarrierType")


  private class OptionTableModel(options: Buffer[ExpressionOption]) extends AbstractTableModel {
    def getRowCount = options.length

    def getColumnCount = columns.length

    def getValueAt(row: Int, col: Int) = col match {
      case 0 => options(row).optionType
      case 1 => options(row).strike
      case 2 => options(row).quantity
      case 3 => options(row).notional
      case 4 => options(row).optionBarrierType
    }

    override def getColumnName(col: Int) = columns(col)

    def add(option: ExpressionOption) {
      options += option
      fireTableDataChanged()
    }

    def removeOne {
      options.headOption.foreach {
        head =>
          options -= head
          fireTableDataChanged()
      }
    }

    def updateWithNewList(newOptions: List[ExpressionOption]) {
      options.clear()
      options ++= newOptions
      fireTableDataChanged()
    }

  }

}