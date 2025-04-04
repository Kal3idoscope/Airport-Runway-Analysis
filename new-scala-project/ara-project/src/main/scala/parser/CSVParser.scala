package parser

import scala.io.Source
import scala.util.Try
import model.{Country, Airport, Runway}

object CSVParser {
  def parseCountries(filePath: String): List[Country] = {
    val lines = Source.fromFile(filePath).getLines().toList
    lines.tail.flatMap(Country.from) // Skip the header row using `.tail`
  }

  def parseAirports(filePath: String): List[Airport] = {
    val lines = Source.fromFile(filePath).getLines().toList
    lines.tail.flatMap(Airport.from) // Skip the header row using `.tail`
  }

  def parseRunways(filePath: String): List[Runway] = {
    val lines = Source.fromFile(filePath).getLines().toList
    lines.tail.flatMap(Runway.from) // Skip the header row using `.tail`
  }
}