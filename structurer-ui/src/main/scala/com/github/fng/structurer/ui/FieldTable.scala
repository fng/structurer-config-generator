package com.github.fng.structurer.ui

import swing.event.Event
import swing.{Component, Button}
import table.CellEditor.{ComboboxCellEditor, ButtonTableCellEditor, ExpressionCellEditor}
import table.GenericTableModel.{ComponentCellRenderer, Column}
import table.{GenericTable, GenericTableModel}

object FieldTable {
  val columns = List(
    new Column[MutableField]("Name", true, (field: MutableField) => field.name) {
      updateHandler = (field: MutableField, newValue: AnyRef) => field.name = newValue match {
        case s: String => s
        case other => sys.error(other.getClass + " is not supported for name field")
      }
    },
    new Column[MutableField]("Type", true, (field: MutableField) => field.fieldType.header) {
      updateHandler = (field: MutableField, newValue: AnyRef) => field.fieldType = FieldType.forHeader(newValue.asInstanceOf[String])
      customCellEditor = new ComboboxCellEditor(List(FieldType.NumberField.header,
        FieldType.NumberRangeField.header, FieldType.ChooseField.header))
    },
    new Column[MutableField]("Constraint Type", true, (field: MutableField) => field.constraintType.header) {
      updateHandler = (field: MutableField, newValue: AnyRef) => field.constraintType = ConstraintType.forHeader(newValue.asInstanceOf[String])
      customCellEditor = new ComboboxCellEditor(List(ConstraintType.GreaterThan.header,
        ConstraintType.GreaterThanEqual.header, ConstraintType.LessThanEqual.header,
        ConstraintType.LessThan.header, ConstraintType.OneOf.header, ConstraintType.ManyOf.header))
    },
    new Column[MutableField]("Value", true, (field: MutableField) => field.value) {
      updateHandler = (field: MutableField, newValue: AnyRef) => field.value = newValue match {
        case s: String => s
        case other => sys.error(other.getClass + " is not supported for value field")
      }
    },
    new Column[MutableField]("Delete", true, (field: MutableField) => "Remove") {
      updateHandler = (field: MutableField, newValue: AnyRef) => {}
      customCellEditor = new ButtonTableCellEditor((row) => {
        println("row to Remove: " + row);
        columnEventPublisher.publish(DeleteFieldTableRowEvent(row))
      })
      customCellRenderer = new ComponentCellRenderer[MutableField] {
        def rendererComponent(tableModel: GenericTableModel[MutableField], isSelected: Boolean, focused: Boolean, row: Int, column: Int): Component = new Button(tableModel.getValueAt(row, column).toString)
      }
    }

  )

  case class DeleteFieldTableRowEvent(row: Int) extends Event

}

class FieldTable(fields: List[MutableField]) extends GenericTable[MutableField](FieldTable.columns, fields) {
  reactions += {
    case FieldTable.DeleteFieldTableRowEvent(row) => tableModel.removeRow(row)
  }
}

case class MutableField(var name: String, var fieldType: FieldType, var constraintType: ConstraintType, var value: String)

abstract class FieldType(val header: String)

object FieldType {

  case object NumberField extends FieldType("Number")

  case object NumberRangeField extends FieldType("Number Range")

  case object ChooseField extends FieldType("Choose")

  def forHeader(header: String): FieldType = header match {
    case NumberField.header => NumberField
    case NumberRangeField.header => NumberRangeField
    case ChooseField.header => ChooseField
    case other => sys.error("No FieldType found for header: " + header)
  }

}

abstract class ConstraintType(val header: String)

object ConstraintType {

  case object GreaterThan extends ConstraintType("GT")

  case object GreaterThanEqual extends ConstraintType("GE")

  case object LessThanEqual extends ConstraintType("LT")

  case object LessThan extends ConstraintType("LE")
  case object OneOf extends ConstraintType("OneOf")
  case object ManyOf extends ConstraintType("ManyOf")
  

  def forHeader(header: String): ConstraintType = header match {
    case GreaterThan.header => GreaterThan
    case GreaterThanEqual.header => GreaterThanEqual
    case LessThanEqual.header => LessThanEqual
    case LessThan.header => LessThan
    case OneOf.header => OneOf
    case ManyOf.header => ManyOf
    case other => sys.error("No ConstraintType found for header: " + header)
  }

}