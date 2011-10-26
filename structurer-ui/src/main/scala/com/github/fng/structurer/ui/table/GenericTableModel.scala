package com.github.fng.structurer.ui.table

import collection.mutable.Buffer
import javax.swing.table.AbstractTableModel._
import javax.swing.table.{TableCellEditor, AbstractTableModel}
import swing.{Publisher, Component}
import com.github.fng.structurer.ui.table.GenericTableModel.{ColumnEventPublisher, Column}

object GenericTableModel {

  case class Column[T](name: String, editable: Boolean, extractor: (T) => AnyRef,
                       update: (T, AnyRef) => Unit = (a: T, b: AnyRef) => {
                         error("not supported b: " + b)
                       },
                       customCellEditor: Option[TableCellEditor] = None,
                       customCellRenderer: Option[ComponentCellRenderer[T]] = None,
                       columnEventPublisher: ColumnEventPublisher = new ColumnEventPublisher)

  class ColumnEventPublisher extends Publisher

  trait ComponentCellRenderer[T] {
    def rendererComponent(tableModel: GenericTableModel[T], isSelected: Boolean, focused: Boolean, row: Int, column: Int): Component
  }

  class ColumnBuilder[T](var _name: String, var _editable: Boolean, var _extractor: (T) => AnyRef) {

    def this() = this(null, false, null)

    val columnEventPublisher: ColumnEventPublisher = new ColumnEventPublisher

    var _update: (T, AnyRef) => Unit = (a: T, b: AnyRef) => {
      sys.error("not supported b: " + b)
    }
    var _customCellEditor: Option[TableCellEditor] = None
    var _customCellRenderer: Option[ComponentCellRenderer[T]] = None

    def name_=(name: String) {
      _name = name
    }

    def name: String = _name


    def editable_=(editable: Boolean) {
      _editable = editable
    }

    def editable: Boolean = _editable


    def extractor_=(extractor: (T) => AnyRef) {
      _extractor = extractor
    }

    def extractor: (T) => AnyRef = _extractor


    def updateHandler_=(update: (T, AnyRef) => Unit) {
      _update = update
    }

    def updateHandler: (T, AnyRef) => Unit = _update


    def customCellEditor_=(customCellEditor: TableCellEditor) {
      _customCellEditor = Option(customCellEditor)
    }

    def customCellEditor: TableCellEditor = _customCellEditor.orNull


    def customCellRenderer_=(customCellRenderer: ComponentCellRenderer[T]) {
      _customCellRenderer = Option(customCellRenderer)
    }

    def customCellRenderer: ComponentCellRenderer[T] = _customCellRenderer.orNull


    def build: Column[T] = Column[T](_name, _editable, _extractor, _update,
      customCellRenderer = _customCellRenderer, customCellEditor = _customCellEditor,
      columnEventPublisher = columnEventPublisher)

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
    if (values.length > row) {
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

  def removeRow(row: Int) {
    values.remove(row)
    fireTableDataChanged()
  }

}
