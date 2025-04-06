import Dependencies._

ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

ThisBuild / libraryDependencySchemes += "org.http4s" %% "http4s-core" % "always"

resolvers += Resolver.mavenCentral
resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")

lazy val root = (project in file("."))
  .settings(
    name := "ara-project",
    libraryDependencies ++= Seq(
      // GUI
      scalaSwing,


      // http4s stack
      http4sBlaze,
      http4sDsl,
      http4sCirce,

      // Circe
      circeGeneric,
      circeParser,

      // Cats Effect
      catsEffect,
      log4cats

    )

  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
