package ui

import scala.io.StdIn
import parser.CSVParser
import model.{Country, Airport, Runway}
import java.text.Normalizer
import scala.swing._
import javax.swing.{WindowConstants, JScrollPane}

object GUI {

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

    // Initialize GUI
    createAndShowGUI(countries, airports, runways)
  }

  def createAndShowGUI(countries: List[Country], airports: List[Airport], runways: List[Runway]): Unit = {
    // Create a new Frame
val frame = new MainFrame {
  title = "Airport and Runway Query"
  preferredSize = new Dimension(800, 600)
  background = new Color(253, 213, 224) // pink

  // Create a text area to display output
  val outputArea = new TextArea {
    background = new Color(255, 223, 211) // orange
    editable = false
    rows = 20
    columns = 60
  }

  // Wrap the output area in a ScrollPane
  val scrollPane = new ScrollPane(outputArea)

  // Create a text field for input
  val inputField = new TextField {
    preferredSize = new Dimension(300, 20)
    minimumSize = new Dimension(300, 20)
    maximumSize = new Dimension(300, 20)
  }

  // Panel for input label and field (horizontal alignment)
val inputPanel = new BoxPanel(Orientation.Horizontal) {
  background = new Color(253, 213, 224) // pink
  contents += new Label("Enter country name or code:")
  contents += Swing.HStrut(10) // espace entre le label et le champ texte
  contents += inputField
  contents += new Button("Query") {
    reactions += {
      case event.ButtonClicked(_) =>
        outputArea.text = query(countries, airports, runways, inputField.text.trim)
        outputArea.caret.position = 0
    }
  }
}

// Panel for buttons (horizontal alignment for each button/label)
val buttonPanel = new BoxPanel(Orientation.Vertical) {
  background = new Color(253, 213, 224) // pink

  // Create labels and buttons with left alignment
  contents += new BoxPanel(Orientation.Horizontal) {
    background = new Color(253, 213, 224)
    contents += new Label("Reports")
  }

  contents += new BoxPanel(Orientation.Horizontal) {
    background = new Color(253, 213, 224)
    contents += new Label("Top 10 countries with the highest number of airports:")
  }
  contents += new BoxPanel(Orientation.Horizontal) {
    background = new Color(253, 213, 224)
    contents += new Button("Show highest") {
      background = new Color(253, 213, 224)
      reactions += {
        case event.ButtonClicked(_) =>
          outputArea.text = getTopCountries(countries, airports, top = true)
          outputArea.caret.position = 0
      }
    }
  }

  contents += new BoxPanel(Orientation.Horizontal) {
    background = new Color(253, 213, 224)
    contents += new Label("Top 10 countries with the lowest number of airports:")
  }
  contents += new BoxPanel(Orientation.Horizontal) {
    background = new Color(253, 213, 224)
    contents += new Button("Show lowest") {
      reactions += {
        case event.ButtonClicked(_) =>
          outputArea.text = getTopCountries(countries, airports, top = false)
          outputArea.caret.position = 0
      }
    }
  }

  contents += new BoxPanel(Orientation.Horizontal) {
    background = new Color(253, 213, 224)
    contents += new Label("Types of runways per country:")
  }
  contents += new BoxPanel(Orientation.Horizontal) {
    background = new Color(253, 213, 224)
    contents += new Button("Show runway types") {
      reactions += {
        case event.ButtonClicked(_) =>
          outputArea.text = getRunwayTypesPerCountry(runways)
          outputArea.caret.position = 0
      }
    }
  }

  contents += new BoxPanel(Orientation.Horizontal) {
    background = new Color(253, 213, 224)
    contents += new Label("Top 10 most common runway latitudes:")
  }
  contents += new BoxPanel(Orientation.Horizontal) {
    background = new Color(253, 213, 224)
    contents += new Button("Show latitudes") {
      reactions += {
        case event.ButtonClicked(_) =>
          outputArea.text = getTopRunwayLatitudes(runways)
          outputArea.caret.position = 0
      }
    }
  }
}

// Main layout (vertical)
contents = new BoxPanel(Orientation.Vertical) {
  background = new Color(253, 213, 224) // pink
  contents += inputPanel
  contents += Swing.VStrut(10) 
  contents += buttonPanel
  contents += Swing.VStrut(10) 
  contents += scrollPane
}



  // Close behavior
  peer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
}

    // Make sure to run the GUI on the EDT (Event Dispatch Thread)
    Swing.onEDT {
      println("Opening frame...")
      frame.visible = true
      println("Frame opened")
    }

    // Keep the application alive (this ensures it stays open)
    Thread.sleep(Long.MaxValue)
  }

  def query(countries: List[Country], airports: List[Airport], runways: List[Runway], input: String): String = {
    // Normalize the input string
    val normalizedInput = normalizeString(input.toLowerCase)

    // Find the matching country (partial matching included)
    val matchingCountries = countries.filter { country =>
      val normalizedCountryName = normalizeString(country.name.toLowerCase.trim)
      val normalizedCountryCode = normalizeString(country.code.toLowerCase.trim)

      normalizedCountryName.contains(normalizedInput) || normalizedCountryCode == normalizedInput
    }

    if (matchingCountries.isEmpty) {
      "No matching country found."
    } else {
      val result = new StringBuilder(s"\nFound ${matchingCountries.length} matching country(ies):\n")
      matchingCountries.foreach(country => result.append(s"- ${country.name} (${country.code})\n"))

      // If multiple countries are found, ask the user to refine the selection
      if (matchingCountries.length > 1) {
        result.append("\nPlease enter the exact country code for more details:")
        val selectedCode = input // Assume input field is used for country code
        matchingCountries.find(_.code == selectedCode) match {
          case Some(country) => result.append(displayCountryInfo(country, airports, runways))
          case None => result.append("Invalid country code.")
        }
      } else {
        result.append(displayCountryInfo(matchingCountries.head, airports, runways))
      }
      result.toString()
    }
  }

  // Helper function to display country details
  def displayCountryInfo(country: Country, airports: List[Airport], runways: List[Runway]): String = {
    val result = new StringBuilder(s"\nCountry: ${country.name} (${country.code})\n")

    // Find airports in this country
    val countryAirports = airports.filter(_.isoCountry == country.code)
    if (countryAirports.isEmpty) {
      result.append("No airports found for this country.\n")
    } else {
      result.append("Airports:\n")
      countryAirports.foreach { airport =>
        result.append(s"  - ${airport.name} (${airport.ident})\n")

        // Find runways for this airport
        val airportRunways = runways.filter(_.airportRef == airport.id)
        if (airportRunways.isEmpty) {
          result.append("    No runways found for this airport.\n")
        } else {
          result.append("    Runways:\n")
          airportRunways.foreach { runway =>
            result.append(s"      - Surface: ${runway.surface}, Latitude: ${runway.leIdent}\n")
          }
        }
      }
    }
    result.toString()
  }

  def reports(countries: List[Country], airports: List[Airport], runways: List[Runway]): String = {
    val result = new StringBuilder

    // Report 1: Top 10 countries with highest and lowest number of airports
    val airportCounts = countries.map { country =>
      val count = airports.count(_.isoCountry == country.code)
      (country.name, count)
    }

    val sortedAirportCounts = airportCounts.sortBy { case (_, count) => -count } // Sort by count in descending order
    val top10Countries = sortedAirportCounts.take(10)
    val bottom10Countries = sortedAirportCounts.reverse.take(10)

    result.append("\nTop 10 countries with the highest number of airports:\n")
    top10Countries.foreach { case (name, count) =>
      result.append(s"  - $name: $count airports\n")
    }

    result.append("\nTop 10 countries with the lowest number of airports:\n")
    bottom10Countries.foreach { case (name, count) =>
      result.append(s"  - $name: $count airports\n")
    }

    // Report 2: Types of runways per country
    val runwaySurfaces = runways.groupBy(_.surface).map { case (surface, runways) =>
      (surface, runways.length)
    }

    result.append("\nTypes of runways per country:\n")
    runwaySurfaces.foreach { case (surface, count) =>
      result.append(s"  - $surface: $count runways\n")
    }

    // Report 3: Top 10 most common runway latitudes
    val runwayLatitudes = runways.groupBy(_.leIdent).map { case (leIdent, runways) =>
      (leIdent, runways.length)
    }

    val top10Latitudes = runwayLatitudes.toList.sortBy { case (_, count) => -count }.take(10)
    result.append("\nTop 10 most common runway latitudes:\n")
    top10Latitudes.foreach { case (leIdent, count) =>
      result.append(s"  - $leIdent: $count runways\n")
    }

    result.toString()
  }

  def getTopCountries(countries: List[Country], airports: List[Airport], top: Boolean): String = {
    val airportCounts = countries.map { country =>
      val count = airports.count(_.isoCountry == country.code)
      (country.name, count)
    }

    val sortedAirportCounts = if (top) {
      airportCounts.sortBy { case (_, count) => -count }.take(10) // Top 10
    } else {
      airportCounts.sortBy { case (_, count) => count }.take(10) // Bottom 10
    }

    val title = if (top) "Top 10 countries with the highest number of airports:" 
                else "Top 10 countries with the lowest number of airports:"
    
    val result = new StringBuilder(s"\n$title\n")
    sortedAirportCounts.foreach { case (name, count) =>
      result.append(s"  - $name: $count airports\n")
    }
    result.toString()
  }

def getRunwayTypesPerCountry(runways: List[Runway]): String = {
  // Group runways by country and surface type
  val runwaySurfacesPerCountry = runways.groupBy(_.airportRef).flatMap { case (airportId, runwaysForAirport) =>
    val surfaceCount = runwaysForAirport.groupBy(_.surface).map { case (surface, surfaceRunways) =>
      (surface, surfaceRunways.length)
    }
    surfaceCount
  }

  // Format the result
  val result = new StringBuilder("\nTypes of runways per country:\n")
  
  // Display surface type counts for each country
  runwaySurfacesPerCountry.foreach { case (surface, count) =>
    result.append(s"  - $surface: $count runways\n")
  }

  result.toString()
}


def getTopRunwayLatitudes(runways: List[Runway]): String = {
  val runwayLatitudes = runways.groupBy(_.leIdent).map { case (leIdent, runways) =>
    (leIdent, runways.length)
  }

  val top10Latitudes = runwayLatitudes.toList.sortBy { case (_, count) => -count }.take(10)
  
  val result = new StringBuilder("\nTop 10 most common runway latitudes:\n")
  top10Latitudes.foreach { case (leIdent, count) =>
    result.append(s"  - $leIdent: $count runways\n")
  }
  result.toString()
}



}
