import sbt._

object Dependencies {
  // Swing
  lazy val munit = "org.scalameta" %% "munit" % "0.7.29"
  lazy val scalaSwing = "org.scala-lang.modules" %% "scala-swing" % "3.0.0"  
  
   // http4s stack 
  lazy val http4sVersion = "1.0.0-M40"  
  lazy val http4sBlaze = "org.http4s" %% "http4s-blaze-server" % http4sVersion
  lazy val http4sDsl   = "org.http4s" %% "http4s-dsl" % http4sVersion
  lazy val http4sCirce = "org.http4s" %% "http4s-circe" % http4sVersion

  // Circe 
  lazy val circeVersion = "0.14.6"
  lazy val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  lazy val circeParser  = "io.circe" %% "circe-parser" % circeVersion

  // Cats Effect 
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % "3.5.2"
  lazy val log4cats = "org.typelevel" %% "log4cats-slf4j" % "2.6.0"

}
