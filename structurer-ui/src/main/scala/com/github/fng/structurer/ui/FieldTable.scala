package com.github.fng.structurer.ui

import table.CellEditor.{ComboboxCellEditor, ButtonTableCellEditor}
import table.GenericTableModel.EditableMode.CustomEditableMode
import swing.event.Event
import swing._
import java.awt.Color
import table.GenericTableModel.{EditableMode, NonEditableBackgroundColorCellRenderer, ComponentCellRenderer, Column}
import table.{ComponentCellEditor, GenericTable, GenericTableModel}
import javax.swing.table.TableCellEditor
import com.github.fng.structurer.config.FieldConfig
import com.github.fng.structurer.config.FieldConfig._

object FieldTable {

  val columns = List(
    new Column[MutableField, String]("Name", EditableMode.IsEditable[MutableField], (field: MutableField) => field.name) {
      updateHandler = (field: MutableField, newValue: String) => field.name = newValue
    },
    new Column[MutableField, FieldType]("Type", EditableMode.IsEditable[MutableField], (field: MutableField) => field.fieldType) {
      updateHandler = (field: MutableField, newValue: FieldType) => field.fieldType = newValue
      customCellEditor = new ComboboxCellEditor[MutableField](List(FieldType.NumberLevelField,
        FieldType.NumberRangeField, FieldType.ChooseField))
    },
    new Column[MutableField, ConstraintType]("Constraint Type", EditableMode.IsEditable[MutableField], (field: MutableField) => field.constraintType) {
      updateHandler = (field: MutableField, newValue: ConstraintType) => field.constraintType = newValue

      customCellEditor = new ComponentCellEditor[MutableField] {
        def editor(tableModel: GenericTableModel[MutableField], row: Int, column: Int): TableCellEditor = {
          new ComboboxCellEditor[MutableField](tableModel.values(row).fieldType.constrainTypes).editor(tableModel, row, column)
        }
      }

    },
    new Column[MutableField, java.lang.Double]("Level", new CustomEditableMode[MutableField] {
      def isEditable(field: MutableField): Boolean = field.fieldType == FieldType.NumberLevelField
    }
      , (field: MutableField) => field.level) {
      updateHandler = (field: MutableField, newValue: java.lang.Double) => field.level = newValue
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
    new Column[MutableField, String]("Default", EditableMode.IsEditable[MutableField], (field: MutableField) => field.default) {
      updateHandler = (field: MutableField, newValue: String) => field.default = newValue
    },
    new Column[MutableField, String]("Delete", EditableMode.IsEditable[MutableField], (field: MutableField) => "Remove") {
      updateHandler = (field: MutableField, newValue: String) => {}
      customCellEditor = new ButtonTableCellEditor[MutableField]((row) => {
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

class FieldTable(fields: List[MutableField] = Nil) extends GenericTable[MutableField](FieldTable.columns, fields) {


  fields.foreach(listenTo(_))

  def add(mutableField: MutableField) {
    tableModel.add(mutableField)
  }

  def updateWithNewList(fields: List[MutableField]) {
    tableModel.updateWithNewList(fields)
  }

  def getFields: List[FieldConfig] = {
    tableModel.values.map(_.toFieldConfig).toList
  }

  reactions += {
    case FieldTable.DeleteFieldTableRowEvent(row) => tableModel.removeRow(row)
    case MutableField.UpdateEvent => tableModel.fireTableDataChanged()
  }
}

object MutableField {

  case object UpdateEvent extends Event

  def apply(fieldConfig: FieldConfig): MutableField = fieldConfig match {
    case DoubleRangeFieldConfig(name, from, to, default) => MutableField(name, FieldType.NumberRangeField, ConstraintType.Between, null, from + ";" + to, "", default.map(_.toString).orNull)
    case DoubleFieldConfig(name, validationType, level, default) => validationType match {
      case DoubleFieldValidationType.GreaterThan => MutableField(name, FieldType.NumberLevelField, ConstraintType.GreaterThan, level, "", "", default.map(_.toString).orNull)
      case DoubleFieldValidationType.GreaterThanEqual => MutableField(name, FieldType.NumberLevelField, ConstraintType.GreaterThanEqual, level, "", "", default.map(_.toString).orNull)
      case DoubleFieldValidationType.LessThan => MutableField(name, FieldType.NumberLevelField, ConstraintType.LessThan, level, "", "", default.map(_.toString).orNull)
      case DoubleFieldValidationType.LessThanEqual => MutableField(name, FieldType.NumberLevelField, ConstraintType.LessThanEqual, level, "", "", default.map(_.toString).orNull)
    }
    case ChooseFieldConfig(name, validationType, values, default) => validationType match {
      case ChooseFieldValidationType.OneOf => MutableField(name, FieldType.ChooseField, ConstraintType.OneOf, null, "", values.mkString(","), default.orNull)
      case ChooseFieldValidationType.ManyOf => MutableField(name, FieldType.ChooseField, ConstraintType.ManyOf, null, "", values.mkString(","), default.orNull)
    }
  }

}

case class MutableField(var name: String, private var _fieldType: FieldType, var constraintType: ConstraintType,
                        private var _level: java.lang.Double, private var _range: String,
                        private var _values: String, var default: String) extends Publisher {
  fieldTypeChanged()

  private def fieldTypeChanged() {
    if (!fieldType.constrainTypes.exists(_ == constraintType)) constraintType = null
    fieldType match {
      case FieldType.NumberLevelField =>
        range = ""
        values = ""
      case FieldType.NumberRangeField =>
        level = null
        values = ""
      case FieldType.ChooseField =>
        level = null
        range = ""
    }
    publish(MutableField.UpdateEvent)
  }

  def fieldType_=(fieldType: FieldType) {
    _fieldType = fieldType
    fieldTypeChanged()
  }

  def fieldType: FieldType = _fieldType

  def level_=(level: java.lang.Double) {
    _level = level
  }

  def level: java.lang.Double = _level


  def range_=(range: String) {
    _range = range
  }

  def range: String = _range

  def values_=(values: String) {
    _values = values
  }

  def values: String = _values


  implicit def tryToCastToDouble(value: String): Option[Double] = value match {
    case null => None
    case _ =>
      try {
        Some(value.toDouble)
      } catch {
        case e: Exception => None
      }
  }

  implicit def wrapString(value: String): Option[String] = value match {
    case null => None
    case notNull => Some(notNull)
  }


  def toFieldConfig: FieldConfig = fieldType match {
    case FieldType.NumberRangeField => range.split(";").toList match {
      case List(from, to) => DoubleRangeFieldConfig(name, from.toDouble, to.toDouble, default)
      case _ => sys.error("Not a valid range value!")
    }
    case FieldType.NumberLevelField => constraintType match {
      case ConstraintType.GreaterThan => DoubleFieldConfig(name, DoubleFieldValidationType.GreaterThan, level, default)
      case ConstraintType.GreaterThanEqual => DoubleFieldConfig(name, DoubleFieldValidationType.GreaterThanEqual, level, default)
      case ConstraintType.LessThan => DoubleFieldConfig(name, DoubleFieldValidationType.LessThan, level, default)
      case ConstraintType.LessThanEqual => DoubleFieldConfig(name, DoubleFieldValidationType.LessThanEqual, level, default)
    }
    case FieldType.ChooseField => constraintType match {
      case ConstraintType.OneOf => ChooseFieldConfig(name, ChooseFieldValidationType.OneOf, values.split(",").toList, default)
      case ConstraintType.ManyOf => ChooseFieldConfig(name, ChooseFieldValidationType.ManyOf, values.split(",").toList, default)
    }
  }


}

abstract class FieldType(val header: String, val constrainTypes: List[ConstraintType]) {
  override def toString: String = header
}

object FieldType {

  case object NumberLevelField extends FieldType("Number Level", List(ConstraintType.GreaterThan, ConstraintType.GreaterThanEqual,
    ConstraintType.LessThan, ConstraintType.LessThanEqual))

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

  case object LessThan extends ConstraintType("LT")

  case object LessThanEqual extends ConstraintType("LE")

  case object Between extends ConstraintType("Between")

  case object OneOf extends ConstraintType("OneOf")

  case object ManyOf extends ConstraintType("ManyOf")


  def forHeader(header: String): ConstraintType = header match {
    case GreaterThan.header => GreaterThan
    case GreaterThanEqual.header => GreaterThanEqual
    case LessThan.header => LessThan
    case LessThanEqual.header => LessThanEqual
    case Between.header => Between
    case OneOf.header => OneOf
    case ManyOf.header => ManyOf
    case other => sys.error("No ConstraintType found for header: " + header)
  }

}