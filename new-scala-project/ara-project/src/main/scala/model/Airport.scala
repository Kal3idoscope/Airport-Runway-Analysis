package model
import scala.util.Try


case class Airport(
  id: Int,
  ident: String,
  airportType: String,
  name: String,
  latitudeDeg: Option[Double],
  longitudeDeg: Option[Double],
  elevationFt: Option[Int],
  continent: String,
  isoCountry: String,
  isoRegion: String,
  municipality: Option[String],
  gpsCode: Option[String],
  iataCode: Option[String],
  localCode: Option[String],
  homeLink: Option[String],
  wikipediaLink: Option[String],
  keywords: Option[String]
)

object Airport {
  def from(line: String): Option[Airport] = {
    val parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1).map(_.trim.replace("\"", ""))
    if (parts.length >= 18) {
      Some(Airport(
        parts(0).toInt,
        parts(1),
        parts(2),
        parts(3),
        Try(parts(4).toDouble).toOption,
        Try(parts(5).toDouble).toOption,
        Try(parts(6).toInt).toOption,
        parts(7),
        parts(8), // isoCountry
        parts(9),
        Option(parts(10)).filter(_.nonEmpty),
        Option(parts(12)).filter(_.nonEmpty), // Skip scheduled_service (parts(11))
        Option(parts(13)).filter(_.nonEmpty),
        Option(parts(14)).filter(_.nonEmpty),
        Option(parts(15)).filter(_.nonEmpty),
        Option(parts(16)).filter(_.nonEmpty),
        Option(parts(17)).filter(_.nonEmpty)
      ))
    } else {
      println(s"Debug: Failed to parse airport line: $line") // Debug statement
      None
    }
  }
}