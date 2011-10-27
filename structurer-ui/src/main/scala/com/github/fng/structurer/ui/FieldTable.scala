package com.github.fng.structurer.ui

import table.CellEditor.{ComboboxCellEditor, ButtonTableCellEditor}
import table.GenericTableModel.EditableMode.CustomEditableMode
import table.GenericTableModel.{NonEditableBackgroundColorCellRenderer, ComponentCellRenderer, Column}
import swing.event.Event
import swing._
import java.awt.Color
import table.{ComponentCellEditor, GenericTable, GenericTableModel}
import javax.swing.table.TableCellEditor
import com.github.fng.structurer.config.FieldConfig
import com.github.fng.structurer.config.FieldConfig._

object FieldTable {

  val columns = List(
    new Column[MutableField, String]("Name", true, (field: MutableField) => field.name) {
      updateHandler = (field: MutableField, newValue: String) => field.name = newValue
    },
    new Column[MutableField, FieldType]("Type", true, (field: MutableField) => field.fieldType) {
      updateHandler = (field: MutableField, newValue: FieldType) => field.fieldType = newValue
      customCellEditor = new ComboboxCellEditor[MutableField](List(FieldType.NumberLevelField,
        FieldType.NumberRangeField, FieldType.ChooseField))
    },
    new Column[MutableField, ConstraintType]("Constraint Type", true, (field: MutableField) => field.constraintType) {
      updateHandler = (field: MutableField, newValue: ConstraintType) => field.constraintType = newValue

      customCellEditor = new ComponentCellEditor[MutableField] {
        def editor(tableModel: GenericTableModel[MutableField], row: Int, column: Int): TableCellEditor = {
          new ComboboxCellEditor[MutableField](tableModel.values(row).fieldType.constrainTypes).editor(tableModel, row, column)
        }
      }

    },
    new Column[MutableField, String]("Level", new CustomEditableMode[MutableField] {
      def isEditable(field: MutableField): Boolean = field.fieldType == FieldType.NumberLevelField
    }
      , (field: MutableField) => field.level) {
      updateHandler = (field: MutableField, newValue: String) => field.level = newValue
      customCellRenderer = NonEditableBackgroundColorCellRenderer[MutableField](Color.LIGHT_GRAY)
    },
    new Column[MutableField, String]("Range", new CustomEditableMode[MutableField] {
      def isEditable(field: MutableField): Boolean = field.fieldType == FieldType.NumberRangeField
    }
      , (field: MutableField) => field.range) {
      updateHandler = (field: MutableField, newValue: String) => field.range = newValue
      customCellRenderer = NonEditableBackgroundColorCellRenderer[MutableField](Color.LIGHT_GRAY)
    },
    new Column[MutableField, String]("Values", new CustomEditableMode[MutableField] {
      def isEditable(field: MutableField): Boolean = field.fieldType == FieldType.ChooseField
    }
      , (field: MutableField) => field.values) {
      updateHandler = (field: MutableField, newValue: String) => field.values = newValue
      customCellRenderer = NonEditableBackgroundColorCellRenderer[MutableField](Color.LIGHT_GRAY)
    },
    new Column[MutableField, String]("Delete", true, (field: MutableField) => "Remove") {
      updateHandler = (field: MutableField, newValue: String) => {}
      customCellEditor = new ButtonTableCellEditor[MutableField]((row) => {
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

  def updateWithNewList(fields: List[MutableField]) {
    tableModel.updateWithNewList(fields)
  }


  reactions += {
    case FieldTable.DeleteFieldTableRowEvent(row) => tableModel.removeRow(row)
    case MutableField.UpdateEvent => tableModel.fireTableDataChanged()
  }
}

object MutableField {

  case object UpdateEvent extends Event

  def apply(fieldConfig: FieldConfig): MutableField = fieldConfig match {
    case DoubleRangeFieldConfig(name, from, to, default) => MutableField(name, FieldType.NumberRangeField, ConstraintType.Between, "", from + ";" + to, "")
    case DoubleFieldConfig(name, validationType, level, default) => validationType match {
      case DoubleFieldValidationType.GreaterThan => MutableField(name, FieldType.NumberLevelField, ConstraintType.GreaterThan, level.toString, "", "")
      case DoubleFieldValidationType.GreaterThanEqual => MutableField(name, FieldType.NumberLevelField, ConstraintType.GreaterThanEqual, level.toString, "", "")
      case DoubleFieldValidationType.LessThan => MutableField(name, FieldType.NumberLevelField, ConstraintType.LessThan, level.toString, "", "")
      case DoubleFieldValidationType.LessThanEqual => MutableField(name, FieldType.NumberLevelField, ConstraintType.LessThanEqual, level.toString, "", "")
    }
    case ChooseFieldConfig(name, validationType, values, default) => validationType match {
      case ChooseFieldValidationType.OneOf => MutableField(name, FieldType.ChooseField, ConstraintType.OneOf, "", "", values.mkString(","))
      case ChooseFieldValidationType.ManyOf => MutableField(name, FieldType.ChooseField, ConstraintType.ManyOf, "", "", values.mkString(","))
    }
  }

}

case class MutableField(var name: String, private var _fieldType: FieldType, var constraintType: ConstraintType,
                        private var _level: String, private var _range: String,
                        private var _values: String) extends Publisher {
  fieldTypeChanged()

  private def fieldTypeChanged() {
    if (!fieldType.constrainTypes.exists(_ == constraintType)) constraintType = null
    fieldType match {
      case FieldType.NumberLevelField =>
        range = ""
        values = ""
      case FieldType.NumberRangeField =>
        level = ""
        values = ""
      case FieldType.ChooseField =>
        level = ""
        range = ""
    }
    publish(MutableField.UpdateEvent)
  }

  def fieldType_=(fieldType: FieldType) {
    _fieldType = fieldType
    fieldTypeChanged()
  }

  def fieldType: FieldType = _fieldType

  def level_=(level: String) {
    _level = level
  }

  def level: String = _level


  def range_=(range: String) {
    _range = range
  }

  def range: String = _range

  def values_=(values: String) {
    _values = values
  }

  def values: String = _values
}

abstract class FieldType(val header: String, val constrainTypes: List[ConstraintType]) {
  override def toString: String = header
}

object FieldType {

  case object NumberLevelField extends FieldType("Number Level", List(ConstraintType.GreaterThan, ConstraintType.GreaterThanEqual,
    ConstraintType.LessThanEqual, ConstraintType.LessThan))

  case object NumberRangeField extends FieldType("Number Range", List(ConstraintType.Between))

  case object ChooseField extends FieldType("Choose", List(ConstraintType.OneOf, ConstraintType.ManyOf))

  def forHeader(header: String): FieldType = header match {
    case NumberLevelField.header => NumberLevelField
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