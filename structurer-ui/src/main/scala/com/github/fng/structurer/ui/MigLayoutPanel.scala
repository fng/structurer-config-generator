package com.github.fng.structurer.ui

import javax.swing.JPanel
import net.miginfocom.swing.MigLayout
import swing._

class MigLayoutPanel(val constraints: String = "", val colConstraints: String = "", val rowConstraints: String = "",  val insets: String = "insets 5 5 5 5") extends Panel with LayoutContainer {

  type Constraints = String

  override lazy val peer = new JPanel(new MigLayout(if (constraints.length > 0) constraints + ", " + insets else insets, colConstraints, rowConstraints))
  private def layoutManager = peer.getLayout.asInstanceOf[MigLayout]

  protected def constraintsFor(comp: Component) =
  layoutManager.getComponentConstraints(comp.peer).asInstanceOf[String]
  protected def areValid(c: Constraints): (Boolean, String) = (true, "")
  def addLabel(text:String, constraint: String = "") = add(label(text), constraint)
  def add(c: Component, constraint: String = "") { peer.add(c.peer, constraint) }
  def wrap(c: Component, constraint:String = "") = {add(c, (if (constraint.length > 0) "wrap, " + constraint else "wrap"))}
  def remove(c: Component) = peer.remove(c.peer)

  private def label(text: String) = new Label(text) {
    horizontalAlignment = Alignment.Left; horizontalTextPosition = Alignment.Left
  }
}