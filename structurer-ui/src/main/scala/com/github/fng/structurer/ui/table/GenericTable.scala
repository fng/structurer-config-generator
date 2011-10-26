package com.github.fng.structurer.ui.table

import com.github.fng.structurer.ui.table.GenericTableModel.Column
import swing.{Component, Table}

abstract class GenericTable[T](columns: List[Column[T]], data: List[T]) extends Table {

  val tableModel: GenericTableModel[T] = new GenericTableModel[T](columns, data.toBuffer)
  model = tableModel

  override protected def editor(row: Int, column: Int) = columns(column).customCellEditor match {
    case Some(editor) => editor
    case None => super.editor(row, column)
  }

  override protected def rendererComponent(isSelected: Boolean, focused: Boolean, row: Int, column: Int): Component = columns(column).customCellRenderer match {
    case Some(renderer) => renderer.rendererComponent(tableModel, isSelected, focused, row, column)
    case None => super.rendererComponent(isSelected, focused, row, column)
  }

}