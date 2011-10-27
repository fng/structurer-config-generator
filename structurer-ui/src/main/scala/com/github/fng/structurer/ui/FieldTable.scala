package com.github.fng.structurer.ui

import table.CellEditor.{ComboboxCellEditor, ButtonTableCellEditor}
import table.GenericTableModel.EditableMode.CustomEditableMode
import table.GenericTableModel.{NonEditableBackgroundColorCellRenderer, ComponentCellRenderer, Column}
import table.{GenericTable, GenericTableModel}
import swing.event.Event
import swing._
import java.awt.Color

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
        ConstraintType.LessThan, ConstraintType.Between, ConstraintType.OneOf, ConstraintType.ManyOf))
    },
    new Column[MutableField, String]("Number Value", new CustomEditableMode[MutableField] {
      def isEditable(field: MutableField): Boolean = field.fieldType == FieldType.NumberField
    }
      , (field: MutableField) => field.numberValue) {
      updateHandler = (field: MutableField, newValue: String) => field.numberValue = newValue
      customCellRenderer = NonEditableBackgroundColorCellRenderer[MutableField](Color.LIGHT_GRAY)
    },
    new Column[MutableField, String]("Number Range Value", new CustomEditableMode[MutableField] {
      def isEditable(field: MutableField): Boolean = field.fieldType == FieldType.NumberRangeField
    }
      , (field: MutableField) => field.numberRangeValue) {
      updateHandler = (field: MutableField, newValue: String) => field.numberRangeValue = newValue
      customCellRenderer = NonEditableBackgroundColorCellRenderer[MutableField](Color.LIGHT_GRAY)
    },
    new Column[MutableField, String]("Choose Value", new CustomEditableMode[MutableField] {
      def isEditable(field: MutableField): Boolean = field.fieldType == FieldType.ChooseField
    }
      , (field: MutableField) => field.chooseValue) {
      updateHandler = (field: MutableField, newValue: String) => field.chooseValue = newValue
      customCellRenderer = NonEditableBackgroundColorCellRenderer[MutableField](Color.LIGHT_GRAY)
    },
    new Column[MutableField, String]("Delete", true, (field: MutableField) => "Remove") {
      updateHandler = (field: MutableField, newValue: String) => {}
      customCellEditor = new ButtonTableCellEditor((row) => {
        println("row to Remove: " + row);
        columnEventPublisher.publish(DeleteFieldTableRowEvent(row))
      })
      customCellRenderer = new ComponentCellRenderer[MutableField] {
        def rendererComponent(tableModel: GenericTableModel[MutableField], isSelected: Boolean, focused: Boolean, row: Int,
                              column: Int, defaultRendererComponent: () => Component): Component = new Button(tableModel.getValueAt(row, column).toString)
      }
    }

  )

  case class DeleteFieldTableRowEvent(row: Int) extends Event

}

class FieldTable(fields: List[MutableField]) extends GenericTable[MutableField](FieldTable.columns, fields) {

  fields.foreach(listenTo(_))

  reactions += {
    case FieldTable.DeleteFieldTableRowEvent(row) => tableModel.removeRow(row)
    case MutableField.UpdateEvent => tableModel.fireTableDataChanged()
  }
}

object MutableField {

  case object UpdateEvent extends Event

}

case class MutableField(var name: String, private var _fieldType: FieldType, var constraintType: ConstraintType,
                        private var _numberValue: String, private var _numberRangeValue: String,
                        private var _chooseValue: String) extends Publisher {
  fieldTypeChanged()

  private def fieldTypeChanged() {
    fieldType match {
      case FieldType.NumberField =>
        numberRangeValue = ""
        chooseValue = ""
      case FieldType.NumberRangeField =>
        numberValue = ""
        chooseValue = ""
      case FieldType.ChooseField =>
        numberValue = ""
        numberRangeValue = ""
    }
    publish(MutableField.UpdateEvent)
  }

  def fieldType_=(fieldType: FieldType) {
    _fieldType = fieldType
    fieldTypeChanged()
  }

  def fieldType: FieldType = _fieldType

  def numberValue_=(numberValue: String) {
    _numberValue = numberValue
  }

  def numberValue: String = _numberValue


  def numberRangeValue_=(numberRangeValue: String) {
    _numberRangeValue = numberRangeValue
  }

  def numberRangeValue: String = _numberRangeValue

  def chooseValue_=(chooseValue: String) {
    _chooseValue = chooseValue
  }

  def chooseValue: String = _chooseValue
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

  case object Between extends ConstraintType("Between")

  case object OneOf extends ConstraintType("OneOf")

  case object ManyOf extends ConstraintType("ManyOf")


  def forHeader(header: String): ConstraintType = header match {
    case GreaterThan.header => GreaterThan
    case GreaterThanEqual.header => GreaterThanEqual
    case LessThanEqual.header => LessThanEqual
    case LessThan.header => LessThan
    case Between.header => Between
    case OneOf.header => OneOf
    case ManyOf.header => ManyOf
    case other => sys.error("No ConstraintType found for header: " + header)
  }

}