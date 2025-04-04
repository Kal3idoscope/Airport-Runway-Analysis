package model
import scala.util.Try


case class Runway(
  id: Int,
  airportRef: Int,
  airportIdent: String,
  lengthFt: Option[Int],
  widthFt: Option[Int],
  surface: Option[String],
  lighted: Boolean,
  closed: Boolean,
  leIdent: Option[String],
  leLatitudeDeg: Option[Double],
  leLongitudeDeg: Option[Double],
  leElevationFt: Option[Int],
  leHeadingDegT: Option[Double],
  leDisplacedThresholdFt: Option[Int],
  heIdent: Option[String],
  heLatitudeDeg: Option[Double],
  heLongitudeDeg: Option[Double],
  heElevationFt: Option[Int],
  heHeadingDegT: Option[Double],
  heDisplacedThresholdFt: Option[Int]
)

object Runway {
  def from(line: String): Option[Runway] = {
    val parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1).map(_.trim.replace("\"", ""))
    if (parts.length == 20) {
      // Helper function to parse "0" and "1" as boolean values
      def parseBoolean(value: String): Boolean = {
        value match {
          case "1" | "true" | "yes" => true
          case "0" | "false" | "no" => false
          case _ => false // Default to false for unexpected values
        }
      }

      Some(Runway(
        parts(0).toInt,
        parts(1).toInt, // airportRef
        parts(2),
        Try(parts(3).toInt).toOption,
        Try(parts(4).toInt).toOption,
        Option(parts(5)).filter(_.nonEmpty),
        parseBoolean(parts(6)), // Parse lighted as a boolean
        parseBoolean(parts(7)), // Parse closed as a boolean
        Option(parts(8)).filter(_.nonEmpty),
        Try(parts(9).toDouble).toOption,
        Try(parts(10).toDouble).toOption,
        Try(parts(11).toInt).toOption,
        Try(parts(12).toDouble).toOption,
        Try(parts(13).toInt).toOption,
        Option(parts(14)).filter(_.nonEmpty),
        Try(parts(15).toDouble).toOption,
        Try(parts(16).toDouble).toOption,
        Try(parts(17).toInt).toOption,
        Try(parts(18).toDouble).toOption,
        Try(parts(19).toInt).toOption
      ))
    } else {
      println(s"Debug: Failed to parse runway line: $line") // Debug statement
      None
    }
  }
}