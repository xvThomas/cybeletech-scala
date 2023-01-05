ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / scapegoatVersion := "1.4.12"

lazy val root = (project in file("."))
  .settings(
    name := "cybeletech-scala",
    libraryDependencies ++= Seq(
      "io.spray"      %% "spray-json" % "1.3.6",
      "org.scalatest" %% "scalatest"  % "3.2.14" % Test
    )
  )

import org.scoverage.coveralls.Imports.CoverallsKeys._
coverallsTokenFile := sys.env.get("COVERALLS_TOKEN")

coverageHighlighting            := true
coverageFailOnMinimum           := false
coverageMinimumStmtTotal        := 70
coverageMinimumBranchTotal      := 70
coverageMinimumStmtPerPackage   := 70
coverageMinimumBranchPerPackage := 70
coverageMinimumStmtPerFile      := 70
coverageMinimumBranchPerFile    := 70

Test / publishArtifact   := false
Test / parallelExecution := false
