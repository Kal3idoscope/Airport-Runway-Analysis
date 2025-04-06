package api

import model.{Country, Airport, Runway}

object Queries {
  def query(
    countries: List[Country],
    airports: List[Airport],
    runways: List[Runway],
    countryParam: String
  ): List[(Airport, List[Runway])] = {
    // 1. find country 
    val country = countries.find(c => 
      c.code.equalsIgnoreCase(countryParam) || 
      c.name.equalsIgnoreCase(countryParam)
    ) 

    // 2. find airport
    country match {
      case Some(c) =>
        val countryAirports = airports.filter(_.isoCountry.equalsIgnoreCase(c.code))
        // 3. find runways
        countryAirports.map { airport =>
          val airportRunways = runways.filter(_.airportRef == airport.id)
          (airport, airportRunways)
        }
      case None => List.empty
    }
  }

// REPORT 1
  def getTopCountries(
    countries: List[Country],
    airports: List[Airport],
    top: Boolean
  ): List[(Country, Int)] = {
    // 1. count airports by country
    val countryAirportCount = airports
      .groupBy(_.isoCountry)
      .map { case (countryCode, aps) => (countryCode, aps.size) }
    
    // 2. sort countries
    val sorted = countries
      .flatMap(c => countryAirportCount.get(c.code).map(count => (c, count)))
      .sortBy { case (_, count) => if (top) -count else count }
    
    // 3. take top/bottom 10 
    sorted.take(10)
  }

// REPORT 2
  def getRunwayTypesByCountry(
    countries: List[Country],
    airports: List[Airport],
    runways: List[Runway]
  ): List[(Country, Set[String])] = {
    countries.flatMap { country =>
      val airportIds = airports.filter(_.isoCountry == country.code).map(_.id).toSet
      val surfaces = runways
        .filter(r => airportIds.contains(r.airportRef))
        .flatMap(_.surface)
        .toSet

      if (surfaces.nonEmpty)
        Some((country, surfaces))
      else
        None
    }
  }

// REPORT 3
  def getMostCommonRunwayIdentifications(
    runways: List[Runway],
    top: Boolean
  ): List[(String, Int)] = {
    val identCounts = runways
      .flatMap(_.leIdent)
      .groupBy(identity)
      .mapValues(_.size)
      .toList

    val sorted = if (top)
      identCounts.sortBy(-_._2)
    else
      identCounts.sortBy(_._2)

    sorted.take(10)
  }


}



