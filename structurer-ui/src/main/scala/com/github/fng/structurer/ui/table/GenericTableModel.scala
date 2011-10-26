package com.github.fng.structurer.ui.table

import collection.mutable.Buffer
import javax.swing.table.AbstractTableModel._
import javax.swing.table.{TableCellEditor, AbstractTableModel}
import com.github.fng.structurer.ui.table.GenericTableModel.Column
import swing.Component


object GenericTableModel {

  case class Column[T](name: String, editable: Boolean, extractor: (T) => AnyRef,
                       update: (T, AnyRef) => Unit = (a: T, b: AnyRef) => {
                         error("not supported b: " + b)
                       },
                       customCellEditor: Option[TableCellEditor] = None,
                              customCellRenderer: Option[ComponentCellRenderer] = None)

  trait ComponentCellRenderer{
     def rendererComponent(isSelected: Boolean, focused: Boolean, row: Int, column: Int): Component
  }

}

class GenericTableModel[T](columns: List[Column[T]], val values: Buffer[T]) extends AbstractTableModel {
  def getRowCount = values.length

  def getColumnCount = columns.length

  def getValueAt(row: Int, col: Int) = columns(col).extractor(values(row))

  override def isCellEditable(row: Int, col: Int) = columns(col).editable

  override def getColumnName(col: Int) = columns(col).name


  override def setValueAt(value: AnyRef, row: Int, col: Int) {
    println("update row: " + row + " col: " + col + " with value: " + value)
    if(values.length > row){
      columns(col).update(values(row), value)
    }
  }

  def add(option: T) {
    values += option
    fireTableDataChanged()
  }

  def removeOne {
    values.headOption.foreach {
      head =>
        values -= head
        fireTableDataChanged()
    }
  }

  def updateWithNewList(newOptions: List[T]) {
    values.clear()
    values ++= newOptions
    fireTableDataChanged()
  }

  def removeRow(row: Int){
    values.remove(row)
    fireTableDataChanged()
  }

}
