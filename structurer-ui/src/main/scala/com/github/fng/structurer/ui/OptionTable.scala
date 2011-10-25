package com.github.fng.structurer.ui

import instrument.ExpressionOption
import swing.Table
import collection.mutable.Buffer
import javax.swing.{JComboBox, DefaultCellEditor}
import javax.swing.table.{TableCellEditor, AbstractTableModel}
import com.github.fng.structurer.instrument.{OptionBarrierType, OptionType}
import com.github.fng.structurer.config.expression.{ExpressionParser, RichExpression}

class OptionTable(options: List[MutableOption]) extends Table {

  val tableModel: OptionTable.OptionTableModel = new OptionTable.OptionTableModel(options.toBuffer)
  model = tableModel

  autoResizeMode = Table.AutoResizeMode.AllColumns

  def add(option: MutableOption) {
    tableModel.add(option)
  }

  def removeOne {
    tableModel.removeOne
  }


  def updateWithNewList(options: List[MutableOption]) {
    tableModel.updateWithNewList(options)
  }

  override protected def editor(row: Int, column: Int) = OptionTable.columns(column).customCellEditor match {
    case Some(editor) => editor
    case None => super.editor(row, column)
  }

}


object OptionTable {


  case class Column(name: String, editable: Boolean, extractor: (MutableOption) => AnyRef,
                    update: (MutableOption, AnyRef) => Unit = (a, b) => {
                      error("not supported b: " + b)
                    },
                    customCellEditor: Option[TableCellEditor] = None)

  val columns = List(
    Column("OptionType", true, _.optionType,
      update = (option, newValue) => option.optionType = newValue.asInstanceOf[OptionType],
      customCellEditor = Some(new OptionTable.ComboboxCellEditor(List(OptionType.Call, OptionType.Put)))),
    Column("Strike", true, _.strike.originalString,
      update = (option, newValue) => option.strike = newValue match {
        case s: String => ExpressionParser.parse(s)
        case other => error(other.getClass + " is not supported for strike field")
      }),
    Column("Quantity", true, _.quantity.originalString,
      update = (option, newValue) => option.quantity = newValue match {
        case s: String => ExpressionParser.parse(s)
        case other => error(other.getClass + " is not supported for quantity field")
      }),
    Column("Notional", true, _.notional.originalString,
      update = (option, newValue) => option.notional = newValue match {
        case s: String => ExpressionParser.parse(s)
        case other => error(other.getClass + " is not supported for notional field")
      }),
    Column("BarrierType", true, _.optionBarrierType,
      update = (option, newValue) => option.optionBarrierType = newValue.asInstanceOf[OptionBarrierType],
      customCellEditor = Some(new OptionTable.ComboboxCellEditor(List(OptionBarrierType.NoBarrier, OptionBarrierType.KnockInBarrier, OptionBarrierType.KnockOutBarrier))))
  )


  private class OptionTableModel(options: Buffer[MutableOption]) extends AbstractTableModel {
    def getRowCount = options.length

    def getColumnCount = columns.length

    def getValueAt(row: Int, col: Int) = columns(col).extractor(options(row))

    override def isCellEditable(row: Int, col: Int) = columns(col).editable

    override def getColumnName(col: Int) = columns(col).name


    override def setValueAt(value: AnyRef, row: Int, col: Int) {
      println("update row: " + row + " col: " + col + " with value: " + value)
      columns(col).update(options(row), value)

    }

    def add(option: MutableOption) {
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

    def updateWithNewList(newOptions: List[MutableOption]) {
      options.clear()
      options ++= newOptions
      fireTableDataChanged()
    }

  }

  class ComboboxCellEditor(values: List[AnyRef]) extends DefaultCellEditor(new JComboBox(values.toArray))

}

case class MutableOption(var optionType: OptionType, var strike: RichExpression, var quantity: RichExpression,
                         var notional: RichExpression,
                         var optionBarrierType: OptionBarrierType)

object MutableOption {
  def apply(option: ExpressionOption): MutableOption =
    MutableOption(option.optionType, option.strike, option.quantity, option.notional, option.optionBarrierType)

}