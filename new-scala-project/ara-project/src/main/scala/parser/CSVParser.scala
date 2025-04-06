package parser

import scala.io.Source
import model.{Country, Airport, Runway}

object CSVParser {
  def parseCountries(filePath: String): List[Country] = {
    val lines = Source.fromFile(filePath).getLines().toList
    lines.tail.flatMap(Country.from)
  }

  def parseAirports(filePath: String): List[Airport] = {
    val lines = Source.fromFile(filePath).getLines().toList
    lines.tail.flatMap(Airport.from)
  }

  def parseRunways(filePath: String): List[Runway] = {
    val lines = Source.fromFile(filePath).getLines().toList
    lines.tail.flatMap(Runway.from)
  }
}