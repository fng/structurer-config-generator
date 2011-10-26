resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

// See: https://github.com/mpeltonen/sbt-idea/tree/sbt-0.10
// Provides the `gen-idea` command to sync the IDEA project structure.
libraryDependencies += "com.github.mpeltonen" %% "sbt-idea" % "0.10.0"