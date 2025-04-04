package ui

import scala.io.StdIn
import parser.CSVParser
import model.{Country, Airport, Runway}
import java.text.Normalizer

object UserInterface {
  // Helper function to normalize strings (remove accents and special characters)
  def normalizeString(str: String): String = {
    Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
  }

  def start(): Unit = {
    // Parse CSV files
    val countries = CSVParser.parseCountries("data/countries.csv")
    val airports = CSVParser.parseAirports("data/airports.csv")
    val runways = CSVParser.parseRunways("data/runways.csv")

    // Debug statements to verify parsed data
    println(s"Debug: Parsed countries = ${countries.length}")
    println(s"Debug: Parsed airports = ${airports.length}")
    println(s"Debug: Parsed runways = ${runways.length}")

    println("Choose an option: Query (1) or Reports (2)")
    try {
      StdIn.readInt() match {
        case 1 => query(countries, airports, runways)
        case 2 => reports(countries, airports, runways)
        case _ => println("Invalid option")
      }
    } catch {
      case _: NumberFormatException => println("Invalid input. Please enter a number.")
    }
  }

  def query(countries: List[Country], airports: List[Airport], runways: List[Runway]): Unit = {
  println("Enter country name or code:")
  val input = StdIn.readLine().trim.toLowerCase

  // Normalize the input string
  val normalizedInput = normalizeString(input)

  // Find the matching country (partial matching included)
  val matchingCountries = countries.filter { country =>
    val normalizedCountryName = normalizeString(country.name.toLowerCase.trim)
    val normalizedCountryCode = normalizeString(country.code.toLowerCase.trim)

    normalizedCountryName.contains(normalizedInput) || normalizedCountryCode == normalizedInput
  }

  if (matchingCountries.isEmpty) {
    println("No matching country found.")
  } else {
    println(s"\nFound ${matchingCountries.length} matching country(ies):")
    matchingCountries.foreach(country => println(s"- ${country.name} (${country.code})"))

    // If multiple countries are found, ask the user to refine the selection
    if (matchingCountries.length > 1) {
      println("Please enter the exact country code for more details:")
      val selectedCode = StdIn.readLine().trim.toUpperCase
      matchingCountries.find(_.code == selectedCode) match {
        case Some(country) => displayCountryInfo(country, airports, runways)
        case None => println("Invalid country code.")
      }
    } else {
      displayCountryInfo(matchingCountries.head, airports, runways)
    }
  }
}

// Helper function to display country details
def displayCountryInfo(country: Country, airports: List[Airport], runways: List[Runway]): Unit = {
  println(s"\nCountry: ${country.name} (${country.code})")

  // Find airports in this country
  val countryAirports = airports.filter(_.isoCountry == country.code)
  if (countryAirports.isEmpty) {
    println("No airports found for this country.")
  } else {
    println("Airports:")
    countryAirports.foreach { airport =>
      println(s"  - ${airport.name} (${airport.ident})")

      // Find runways for this airport
      val airportRunways = runways.filter(_.airportRef == airport.id)
      if (airportRunways.isEmpty) {
        println("    No runways found for this airport.")
      } else {
        println("    Runways:")
        airportRunways.foreach { runway =>
          println(s"      - Surface: ${runway.surface}, Latitude: ${runway.leIdent}")
        }
      }
    }
  }
}



  def reports(countries: List[Country], airports: List[Airport], runways: List[Runway]): Unit = {
    // Report 1: Top 10 countries with highest and lowest number of airports
    val airportCounts = countries.map { country =>
      val count = airports.count(_.isoCountry == country.code)
      (country.name, count)
    }

    val sortedAirportCounts = airportCounts.sortBy { case (_, count) => -count } // Sort by count in descending order
    val top10Countries = sortedAirportCounts.take(10)
    val bottom10Countries = sortedAirportCounts.reverse.take(10)

    println("\nTop 10 countries with the highest number of airports:")
    top10Countries.foreach { case (name, count) =>
      println(s"  - $name: $count airports")
    }

    println("\nTop 10 countries with the lowest number of airports:")
    bottom10Countries.foreach { case (name, count) =>
      println(s"  - $name: $count airports")
    }

    // Report 2: Types of runways per country
    val runwaySurfaces = runways.groupBy(_.surface).map { case (surface, runways) =>
      (surface, runways.length)
    }

    println("\nTypes of runways per country:")
    runwaySurfaces.foreach { case (surface, count) =>
      println(s"  - $surface: $count runways")
    }

    // Report 3: Top 10 most common runway latitudes
    val runwayLatitudes = runways.groupBy(_.leIdent).map { case (leIdent, runways) =>
      (leIdent, runways.length)
    }

    val top10Latitudes = runwayLatitudes.toList.sortBy { case (_, count) => -count }.take(10)
    println("\nTop 10 most common runway latitudes:")
    top10Latitudes.foreach { case (leIdent, count) =>
      println(s"  - $leIdent: $count runways")
    }
  }
}