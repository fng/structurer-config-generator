package com.github.fng.structurer.ui.table

import collection.mutable.Buffer
import javax.swing.table.{TableCellEditor, AbstractTableModel}
import com.github.fng.structurer.ui.table.GenericTableModel.Column
import java.awt.Color
import com.github.fng.structurer.ui.instrument.TextFieldType.ExpressionField
import javax.swing._
import java.awt.event.{ActionEvent, ActionListener}
import swing.{Button, Publisher, Component}

object GenericTableModel {

  class ColumnEventPublisher extends Publisher

  trait ComponentCellRenderer[T] {
    def rendererComponent(tableModel: GenericTableModel[T], isSelected: Boolean, focused: Boolean, row: Int, column: Int,
                          defaultRendererComponent: () => Component): Component
  }

  case class NonEditableBackgroundColorCellRenderer[T](background: Color) extends ComponentCellRenderer[T] {
    def rendererComponent(tableModel: GenericTableModel[T], isSelected: Boolean, focused: Boolean, row: Int,
                          column: Int, defaultRendererComponent: () => Component): Component = {
      val component = defaultRendererComponent()
      if (!tableModel.columns(column).isEditable(tableModel.values(row))) {
        component.background = background
      }
      component
    }
  }


  abstract class EditableMode[T]

  object EditableMode {

    case class IsEditable[T] extends EditableMode[T]

    case class IsNotEditable[T] extends EditableMode[T]

    trait CustomEditableMode[T] extends EditableMode[T] {
      def isEditable(t: T): Boolean
    }

  }

  class Column[T, F](var _name: String, var _editableMode: EditableMode[T], var _extractor: (T) => F)(implicit val m: Manifest[F]) {


    val columnEventPublisher: ColumnEventPublisher = new ColumnEventPublisher

    var _updateHandler: (T, F) => Unit = (a: T, b: F) => {
      sys.error("not supported b: " + b)
    }
    var _customCellEditor: Option[ComponentCellEditor[T]] = None
    var _customCellRenderer: Option[ComponentCellRenderer[T]] = None

    def name_=(name: String) {
      _name = name
    }

    def name: String = _name


    def editableMode_=(editableMode: EditableMode[T]) {
      _editableMode = editableMode
    }

    def editableMode: EditableMode[T] = _editableMode

    def isEditable(t: T): Boolean = editableMode match {
      case EditableMode.IsEditable() => true
      case EditableMode.IsNotEditable() => false
      case custom: EditableMode.CustomEditableMode[T] => custom.isEditable(t)
    }


    def extractor_=(extractor: (T) => F) {
      _extractor = extractor
    }

    def extractor: (T) => F = _extractor


    def updateHandler_=(update: (T, F) => Unit) {
      _updateHandler = update
    }

    def updateHandler: (T, F) => Unit = _updateHandler


    def update(t: T, value: AnyRef) {
      println("class: " + manifest.erasure)

      // TODO 27.10.11 15:32 wyd: allow to register a partial function to provide these custom casting from the outside!
      val castedValue: F = (value, manifest.erasure) match {
        case (s: String, c) if c == classOf[java.lang.Double] => java.lang.Double.valueOf(s).asInstanceOf[F]
        case _ => value.asInstanceOf[F]
      }

      updateHandler(t, castedValue)
    }


    def customCellEditor_=(customCellEditor: ComponentCellEditor[T]) {
      _customCellEditor = Option(customCellEditor)
    }

    def customCellEditor: Option[ComponentCellEditor[T]] = _customCellEditor


    def customCellRenderer_=(customCellRenderer: ComponentCellRenderer[T]) {
      _customCellRenderer = Option(customCellRenderer)
    }

    def customCellRenderer: Option[ComponentCellRenderer[T]] = _customCellRenderer

  }


}

class GenericTableModel[T](val columns: List[Column[T, _]], val values: Buffer[T]) extends AbstractTableModel {
  def getRowCount = values.length

  def getColumnCount = columns.length

  def getValueAt(row: Int, col: Int) = columns(col).extractor(values(row)).asInstanceOf[AnyRef]

  override def isCellEditable(row: Int, col: Int) = columns(col).isEditable(values(row))

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
