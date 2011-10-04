import sbt._
import com.efgfp.simplebuildtool._


final class Project(info: ProjectInfo) extends EfgParentProject(info) {


  lazy val config = project("config", "config", new Config(_))
  lazy val instrument = project("instrument", "instrument", new Instrument(_))
  lazy val payoff = project("payoff", "payoff", new Payoff(_), instrument)
  lazy val structurerUI = project("structurer-ui", "structurer-ui", new StructurerUI(_), instrument, payoff)


  class Config(info: ProjectInfo) extends ProjectDefaults(info)
  class Instrument(info: ProjectInfo) extends ProjectDefaults(info)
  class Payoff(info: ProjectInfo) extends ProjectDefaults(info)
  class StructurerUI(info: ProjectInfo) extends ProjectDefaults(info)

}

abstract class ProjectDefaults(info: ProjectInfo) extends EfgDefaultProject(info){
  val mavenLocal = "Local Maven Repository" at "file://"+Path.userHome+"/.m2/repository"
  val junitXmlRepo = "Christoph's Maven Repo" at "http://maven.henkelmann.eu/"

  val specs = "org.scala-tools.testing" %% "specs" % "1.6.8" % "test" withSources
  val scalaz = "org.scalaz" %% "scalaz-core" % "6.0" withSources
  val scalaSwing = "org.scala-lang" % "scala-swing" % "2.8.1" withSources
  val jfreechart = "jfree" % "jfreechart" % "1.0.13"
  val EfgMeasuresAndUnits = "com.efgfp.commons" % "efg-measures-and-units" % "0.10"
  val SpringWrapper = "com.efgfp.commons" % "spring-wrapper" % "0.15"
}