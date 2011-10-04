package com.github.fng.structurer
package view

import swing.event.Event
import swing.{BoxPanel, Orientation, Publisher}

abstract class InstrumentPanel extends BoxPanel(Orientation.Vertical) with Publisher

object InstrumentPanel{
  sealed abstract class PanelEvent extends Event
  object PanelEvent{
    case class RemovePanelEvent(panel: InstrumentPanel) extends PanelEvent
    case object AddBondPanel extends PanelEvent
    case object AddOptionPanel extends PanelEvent
  }
}