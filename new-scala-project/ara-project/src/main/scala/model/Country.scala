package model

case class Country(
  id: Int,
  code: String,
  name: String,
  continent: String,
  wikipediaLink: String,
  keywords: Option[String]
)

object Country {
  def from(line: String): Option[Country] = {
    // Split the line by commas, but handle quoted fields correctly
    val parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1).map(_.trim.replace("\"", ""))
    if (parts.length >= 5) { // At least 5 fields are required (id, code, name, continent, wikipediaLink)
      val id = parts(0).toInt
      val code = parts(1)
      val name = parts(2)
      val continent = parts(3)
      val wikipediaLink = parts(4)
      val keywords = if (parts.length > 5 && parts(5).nonEmpty) Some(parts(5)) else None

      val country = Country(id, code, name, continent, wikipediaLink, keywords)
      println(s"Debug: Parsed country = ${country.name} (${country.code})") // Debug statement
      Some(country)
    } else {
      println(s"Debug: Failed to parse line: $line") // Debug statement
      None
    }
  }
}