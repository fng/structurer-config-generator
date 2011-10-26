import sbt._
import Keys._

object build extends Build {

  val standardSettings: Seq[Project.Setting[_]] =
    Defaults.defaultSettings ++ Seq[Project.Setting[_]](
      organization := "com.github.fng",
      version := "1.0.0-SNAPSHOT",
      scalaVersion := "2.9.1",
      crossPaths := false
    )

  lazy val structurerConfigGenerator = Project(
    id = "structurer-config-generator",
    base = file("."),
    settings = standardSettings,
    aggregate = Seq[ProjectReference](config, instrument, payoff, structurerUi)
  )

  lazy val config = Project(
    id = "config",
    base = file("config"),
    settings = standardSettings ++ Seq(
      libraryDependencies ++= Seq(EfgMeasuresAndUnits, SpringWrapper)
    )
  )

  lazy val instrument = Project(
    id = "instrument",
    base = file("instrument"),
    settings = standardSettings ++ Seq(
      libraryDependencies ++= Seq()
    )
  )

  lazy val payoff = Project(
    id = "payoff",
    base = file("payoff"),
    dependencies = Seq(instrument),
    settings = standardSettings ++ Seq(
      libraryDependencies ++= Seq()
    )
  )

  lazy val structurerUi = Project(
    id = "structurer-ui",
    base = file("structurer-ui"),
    dependencies = Seq(instrument, payoff, config),
    settings = standardSettings ++ Seq(
      libraryDependencies ++= Seq(EfgMeasuresAndUnits, scalaSwing, jfreechart)
    )
  )


  //  val specs = "org.scala-tools.testing" %% "specs" % "1.6.8" % "test" withSources
  //  val scalaz = "org.scalaz" %% "scalaz-core" % "6.0" withSources
    val scalaSwing = "org.scala-lang" % "scala-swing" % "2.9.1" withSources
    val jfreechart = "jfree" % "jfreechart" % "1.0.13"
    val EfgMeasuresAndUnits = "com.efgfp.commons" % "efg-measures-and-units" % "0.12" withSources
    val SpringWrapper = "com.efgfp.commons" % "spring-wrapper" % "0.15"

}