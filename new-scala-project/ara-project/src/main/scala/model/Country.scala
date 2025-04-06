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
    val parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1).map(_.trim.replace("\"", ""))
    if (parts.length >= 5) {
      val idOption = Utils.toIntOption(parts(0))
      idOption.map { id =>
        val keywords = if (parts.length > 5 && parts(5).nonEmpty) Some(parts(5)) else None
        val country = Country(id, parts(1), parts(2), parts(3), parts(4), keywords)
        println(s"Debug: Parsed country = ${country.name} (${country.code})")
        country
      }
    } else {
      println(s"Debug: Failed to parse line: $line")
      None
    }
  }
}