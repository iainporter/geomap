package com.anchorage.geo

import org.joda.time.DateTime
import scala.Some
import sprest.models.ModelCompanion

case class MappingDetail(
                          loc: Location,
                          timeCreated: Option[DateTime]) {
  def this(loc: Location) = this(loc, Some(DateTime.now))
}


case class Location(longitude: Double, latitude: Double)

