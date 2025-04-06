package model

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
      val idOption = Utils.toIntOption(parts(0))
      val airportRefOption = Utils.toIntOption(parts(1))
      
      (idOption, airportRefOption) match {
        case (Some(id), Some(airportRef)) =>
          def parseBoolean(value: String): Boolean = 
            value match {
              case "1" | "true" | "yes" => true
              case "0" | "false" | "no" => false
              case _ => false
            }
          
          Some(Runway(
            id,
            airportRef,
            parts(2),
            Utils.toIntOption(parts(3)),
            Utils.toIntOption(parts(4)),
            Option(parts(5)).filter(_.nonEmpty),
            parseBoolean(parts(6)),
            parseBoolean(parts(7)),
            Option(parts(8)).filter(_.nonEmpty),
            Utils.toDoubleOption(parts(9)),
            Utils.toDoubleOption(parts(10)),
            Utils.toIntOption(parts(11)),
            Utils.toDoubleOption(parts(12)),
            Utils.toIntOption(parts(13)),
            Option(parts(14)).filter(_.nonEmpty),
            Utils.toDoubleOption(parts(15)),
            Utils.toDoubleOption(parts(16)),
            Utils.toIntOption(parts(17)),
            Utils.toDoubleOption(parts(18)),
            Utils.toIntOption(parts(19))
          ))
        case _ => 
          println(s"Debug: Failed to parse runway line due to invalid id or airportRef: $line")
          None
      }
    } else {
      println(s"Debug: Failed to parse runway line: $line")
      None
    }
  }
}