package com.github.fng.structurer.ui

import swing.event.Event
import swing.{Component, Button}
import table.CellEditor.{ComboboxCellEditor, ButtonTableCellEditor, ExpressionCellEditor}
import table.GenericTableModel.{ComponentCellRenderer, Column}
import table.{GenericTable, GenericTableModel}

object FieldTable {
  val columns = List(
    new Column[MutableField, String]("Name", true, (field: MutableField) => field.name) {
      updateHandler = (field: MutableField, newValue: String) => field.name = newValue
    },
    new Column[MutableField, FieldType]("Type", true, (field: MutableField) => field.fieldType) {
      updateHandler = (field: MutableField, newValue: FieldType) => field.fieldType = newValue
      customCellEditor = new ComboboxCellEditor(List(FieldType.NumberField,
        FieldType.NumberRangeField, FieldType.ChooseField))
    },
    new Column[MutableField, ConstraintType]("Constraint Type", true, (field: MutableField) => field.constraintType) {
      updateHandler = (field: MutableField, newValue: ConstraintType) => field.constraintType = newValue
      customCellEditor = new ComboboxCellEditor(List(ConstraintType.GreaterThan,
        ConstraintType.GreaterThanEqual, ConstraintType.LessThanEqual,
        ConstraintType.LessThan, ConstraintType.OneOf, ConstraintType.ManyOf))
    },
    new Column[MutableField, String]("Number Value", true, (field: MutableField) => field.numberValue) {
      updateHandler = (field: MutableField, newValue: String) => field.numberValue = newValue
    },
    new Column[MutableField, String]("Number Range Value", true, (field: MutableField) => field.numberRangeValue) {
      updateHandler = (field: MutableField, newValue: String) => field.numberRangeValue = newValue
    },
    new Column[MutableField, String]("Choose Value", true, (field: MutableField) => field.chooseValue) {
      updateHandler = (field: MutableField, newValue: String) => field.chooseValue = newValue
    },
    new Column[MutableField, String]("Delete", true, (field: MutableField) => "Remove") {
      updateHandler = (field: MutableField, newValue: String) => {}
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

case class MutableField(var name: String, var fieldType: FieldType, var constraintType: ConstraintType,
                        private var _numberValue: String, var numberRangeValue: String, var chooseValue: String) {
  def numberValue_=(numberValue: String) {
    _numberValue = numberValue
  }

  def numberValue: String = _numberValue

}

abstract class FieldType(val header: String) {
  override def toString: String = header
}

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

abstract class ConstraintType(val header: String) {
  override def toString: String = header
}

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