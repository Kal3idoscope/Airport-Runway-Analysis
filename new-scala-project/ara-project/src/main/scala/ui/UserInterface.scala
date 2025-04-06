package ui

import scala.io.StdIn
import parser.CSVParser
import model.{Country, Airport, Runway}
import api.Queries
import java.text.Normalizer
import model.Utils

object UserInterface {
  def normalizeString(str: String): String = {
    Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
  }

  def start(): Unit = {
    val countries = CSVParser.parseCountries("data/countries.csv")
    val airports = CSVParser.parseAirports("data/airports.csv")
    val runways = CSVParser.parseRunways("data/runways.csv")

    println(s"Debug: Parsed countries = ${countries.length}")
    println(s"Debug: Parsed airports = ${airports.length}")
    println(s"Debug: Parsed runways = ${runways.length}")

    println("Choose an option: Query (1) or Reports (2)")
    val input = StdIn.readLine().trim
    Utils.toIntOption(input) match {
      case Some(1) => query(countries, airports, runways)
      case Some(2) => reports(countries, airports, runways)
      case _ => println("Invalid input. Please enter 1 or 2.")
    }
  }

  def query(countries: List[Country], airports: List[Airport], runways: List[Runway]): Unit = {
    println("Enter country name or code:")
    val input = StdIn.readLine().trim.toLowerCase
    val normalizedInput = normalizeString(input)

    val matchingCountries = countries.filter { country =>
      val normalizedCountryName = normalizeString(country.name.toLowerCase.trim)
      val normalizedCountryCode = normalizeString(country.code.toLowerCase.trim)
      println(s"Debug: Checking country = '$normalizedCountryName' (code: '$normalizedCountryCode')")
      normalizedCountryName.contains(normalizedInput) || normalizedCountryCode == normalizedInput
    }

    if (matchingCountries.isEmpty) {
      println("No matching country found.")
    } else {
      val countryListLines = s"\nFound ${matchingCountries.length} matching country(ies):" :: 
        matchingCountries.map(country => s"- ${country.name} (${country.code})")
      
      countryListLines.foreach(println)

      val outputLines = if (matchingCountries.length > 1) {
        println("Please enter the exact country code for more details:")
        val selectedCode = StdIn.readLine().trim.toUpperCase
        matchingCountries.find(_.code == selectedCode) match {
          case Some(country) => countryDetails(country, airports, runways)
          case None => List("Invalid country code.")
        }
      } else {
        matchingCountries.take(1).flatMap(country => countryDetails(country, airports, runways))
      }
      outputLines.foreach(println)
    }
  }

  def countryDetails(country: Country, airports: List[Airport], runways: List[Runway]): List[String] = {
    val countryHeader = s"\nCountry: ${country.name} (${country.code})"
    val countryAirports = airports.filter(_.isoCountry == country.code)

    if (countryAirports.isEmpty) {
      List(countryHeader, "No airports found for this country.")
    } else {
      val airportLines = countryAirports.flatMap { airport =>
        val airportLine = s"  - ${airport.name} (${airport.ident})"
        val airportRunways = runways.filter(_.airportRef == airport.id)
        if (airportRunways.isEmpty) {
          List(airportLine, "    No runways found for this airport.")
        } else {
          val runwayLines = airportRunways.map { runway =>
            s"      - Surface: ${runway.surface}, Latitude: ${runway.leIdent}"
          }
          airportLine :: "    Runways:" :: runwayLines
        }
      }
      countryHeader :: "Airports:" :: airportLines
    }
  }

  def reports(countries: List[Country], airports: List[Airport], runways: List[Runway]): Unit = {
    val top10Countries = Queries.getTopCountries(countries, airports, top = true)
    val bottom10Countries = Queries.getTopCountries(countries, airports, top = false)

    println("\nTop 10 countries with the highest number of airports:")
    top10Countries.foreach { case (country, count) =>
      println(s"  - ${country.name}: $count airports")
    }

    println("\nTop 10 countries with the lowest number of airports:")
    bottom10Countries.foreach { case (country, count) =>
      println(s"  - ${country.name}: $count airports")
    }

    val runwayTypes = Queries.getRunwayTypesByCountry(countries, airports, runways)
    println("\nTypes of runways per country:")
    runwayTypes.foreach { case (country, surfaces) =>
      println(s"  - ${country.name}: ${surfaces.mkString(", ")}")
    }

    val top10Latitudes = Queries.getMostCommonRunwayIdentifications(runways, top = true)
    println("\nTop 10 most common runway latitudes:")
    top10Latitudes.foreach { case (leIdent, count) =>
      println(s"  - $leIdent: $count runways")
    }
  }
}