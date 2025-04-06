package api

import parser.CSVParser
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import model.{Country, Airport, Runway}
import api.Queries
import io.circe.generic.auto._
import org.http4s.circe._

object Routes {
  // load data
  val countries = CSVParser.parseCountries("data/countries.csv")
  val airports = CSVParser.parseAirports("data/airports.csv")
  val runways = CSVParser.parseRunways("data/runways.csv")

  // JSON serialization
  implicit val countryEncoder: EntityEncoder[IO, Country] = jsonEncoderOf[Country]
  implicit val airportEncoder: EntityEncoder[IO, Airport] = jsonEncoderOf[Airport]
  implicit val runwayEncoder: EntityEncoder[IO, Runway] = jsonEncoderOf[Runway]
  implicit val resultEncoder: EntityEncoder[IO, List[(Airport, List[Runway])]] = jsonEncoderOf[List[(Airport, List[Runway])]]
  implicit val reportEncoder: EntityEncoder[IO, List[(Country, Int)]] = jsonEncoderOf[List[(Country, Int)]]
  implicit val runwayTypesReportEncoder: EntityEncoder[IO, List[(Country, Set[String])]] = jsonEncoderOf[List[(Country, Set[String])]]
  implicit val identFrequencyEncoder: EntityEncoder[IO, List[(String, Int)]] = jsonEncoderOf[List[(String, Int)]]


  def queryRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {

    // QUERIES
    // http://localhost:8080/query?country=France
    case GET -> Root / "query" :? CountryParamMatcher(countryParam) =>
      val result = Queries.query(countries, airports, runways, countryParam)
      Ok(result)

    // REPORTS 
    // TOP 10 HIGHEST NB OF AIRPORTS
    // http://localhost:8080/reports/top
    case GET -> Root / "reports" / "top" =>
      Ok(Queries.getTopCountries(countries, airports, top = true))

    // TOP 10 LOWEST NB OF AIRPORTS
    // http://localhost:8080/reports/bottom
    case GET -> Root / "reports" / "bottom" =>
      Ok(Queries.getTopCountries(countries, airports, top = false))

    // RUNWAY TYPES
    // http://localhost:8080/reports/runway-types
    case GET -> Root / "reports" / "runway-types" =>
      Ok(Queries.getRunwayTypesByCountry(countries, airports, runways))

    // TOP 10 RUNWAY TYPES
    // http://localhost:8080/reports/frequent-runways
    case GET -> Root / "reports" / "frequent-runways" =>
      Ok(Queries.getMostCommonRunwayIdentifications(runways, top = true))

  }

  object CountryParamMatcher extends QueryParamDecoderMatcher[String]("country")
}