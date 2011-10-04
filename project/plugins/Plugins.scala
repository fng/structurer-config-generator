import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {

  val efgfpNexusSnaphotsRepository = "Nexus Snapshots" at "http://nexus/nexus/content/groups/public-snapshots"
  val efgfpNexusReleasesRepository = "Nexus Releases" at "http://nexus/nexus/content/groups/public"

  val efgHelper = "com.efgfp.simplebuildtool" % "efg-helper" % "2.2"

  val junitXmlRepo = "Christoph's Maven Repo" at "http://maven.henkelmann.eu/"
  val junitXml = "eu.henkelmann" % "junit_xml_listener" % "0.2"


  //  val onejarSBT = "com.github.retronym" % "sbt-onejar" % "0.3_splash_screen_BETA"

}