import Dependencies._

ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

resolvers += Resolver.mavenCentral

lazy val root = (project in file("."))
  .settings(
    name := "ara-project",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-swing" % "3.0.0"
    )

    //libraryDependencies += munit % Test
    //libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"

    //libraryDependencies ++= Dependencies.allDependencies

  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
