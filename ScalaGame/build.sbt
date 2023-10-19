
name := "LestersGame"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.18"

offline := true

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "20.0.0-R31",
  "org.scalafx" %% "scalafxml-core-sfx8" % "0.5"
)

fork := true