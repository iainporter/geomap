package com.anchorage.geo.model

import sprest.models.{Model, ModelCompanion}
import com.anchorage.geo.MappingDetail

/**
 * Created by iainporter on 25/02/2014.
 */
case class Device(deviceId: String,
                   name: String,
                   model: String,
                   os: String,
                   manufacturer: String,
                   var mappingDetails: List[MappingDetail],
                   var id: Option[String] = None) extends Model[String] {

  def addLocation(x: MappingDetail): Unit = mappingDetails = x :: mappingDetails

}

object Device extends ModelCompanion[Device, String]


