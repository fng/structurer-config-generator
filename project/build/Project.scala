import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {
	
  val junitXmlRepo = "Christoph's Maven Repo" at "http://maven.henkelmann.eu/"

  val specs = "org.scala-tools.testing" %% "specs" % "1.6.8" % "test" withSources
  val scalaz = "org.scalaz" %% "scalaz-core" % "6.0" withSources
  val scalaSwing = "org.scala-lang" % "scala-swing" % "2.8.1" withSources
  val jfreechart = "jfree" % "jfreechart" % "1.0.13"
}