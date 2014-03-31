package com.anchorage.geo

import org.scalatest.{Suite, BeforeAndAfter, FunSpec}
import org.scalatest.matchers.ShouldMatchers
import spray.testkit.ScalatestRouteTest
import com.anchorage.geo.core.{CoreActors, Core}
import com.anchorage.geo.api.{DefaultJsonFormats, Api}
import scala.concurrent.Await
import spray.http.StatusCodes
import com.anchorage.geo.model.Device
import com.anchorage.geo.Mongo.DeviceDetails
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import org.scalatest.{Suite, BeforeAndAfter, FunSpec}
import org.scalatest.matchers.ShouldMatchers

/**
 * Created by iainporter on 26/02/2014.
 */
class DeviceTrackingServiceIntegrationTest
  extends FunSpec
  with ShouldMatchers
  with BeforeAndAfter
  with ScalatestRouteTest
  with Core
  with CoreActors
  with Api
  with Suite
  with DefaultJsonFormats {

  def actorRefFactory = system

  val route = new DeviceTrackingService(deviceActor).route


  val device = Device(deviceId = "123456789", name = "myIPhone", model = "iPhone", os = "IOS 4.1", manufacturer = "Apple", mappingDetails = Nil)
  val device2 = Device(deviceId = "123456780", name = "myIPhone", model = "iPhone", os = "IOS 4.1", manufacturer = "Apple", mappingDetails = Nil)


  val latitude = 51.8409093
  val longitude = -2.5812894
  val loc = Location(longitude = longitude, latitude = latitude)

  implicit  val routeTestTimeout = RouteTestTimeout(5.second)

  describe("MyService - device Tracker Service") {

    describe("POST") {

      it("should return Created on a POST request and perform a database insert") {

        val resultsBefore = Await.result(DeviceDetails.count(), 5 seconds)

        Post("/devices", device) ~> route ~> check {
          response.status should be(StatusCodes.Created)
          val responseObj = responseAs[Device]
          println(responseObj)
          responseObj.deviceId should be(device.deviceId)
          responseObj.model should be(device.model)
          responseObj.name should be(device.name)
          responseObj.os should be(device.os)
          responseObj.manufacturer should be(device.manufacturer)
          responseObj.id should be('defined)
        }

        val resultsAfter = Await.result(DeviceDetails.count(), 5 seconds)
        resultsAfter should be(resultsBefore + 1)
      }
    }


    it("should be able to find the Device by the Id in the DB") {

      Post("/devices", device) ~> route ~> check {
        response.status should be(StatusCodes.Created)
        val responseObj = responseAs[Device]
        responseObj.deviceId should be(device.deviceId)
        responseObj.model should be(device.model)
        responseObj.name should be(device.name)
        responseObj.os should be(device.os)
        responseObj.manufacturer should be(device.manufacturer)
        val responseDeviceInDB = Await.result(DeviceDetails.findById(responseObj.id.get), 5 seconds)
        responseDeviceInDB should be(Some(responseObj))
      }
    }

    describe("Locations") {

      it("should return Created on a POST request and add a Location to a device") {
        val persistedDevice = Await.result(DeviceDetails.add(device2), 5 seconds)
        persistedDevice.id should be('defined)

        Post("/devices/" + device2.deviceId + "/locations", loc) ~> route ~> check {
          response.status should be(StatusCodes.Created)
//          val responseObj = responseAs[Device]
//          println(responseObj)
//          responseObj.deviceId should be(device2.deviceId)
//          responseObj.model should be(device2.model)
//          responseObj.name should be(device2.name)
//          responseObj.os should be(device2.os)
//          responseObj.manufacturer should be(device2.manufacturer)
//          responseObj.id should be('defined)
        }
      }
    }

  }

  //    describe("DELETE") {
  //
  //      it("should return OK and remove all entities from the collection") {
  //
  //        Await.result(MappingDetails.add(mappingDetail), 5 seconds)
  //
  //        Delete("/locations") ~> route ~> check {
  //          response.status should be(StatusCodes.OK)
  //          responseAs[String] should be("OK")
  //        }
  //
  //        val resultsAfter = Await.result(MappingDetails.count(), 5 seconds)
  //        resultsAfter should be(0)
  //      }
  //    }

  //  describe("GET") {
  //
  //    it("should be able to get all mappingDetails") {
  //
  //      val persistedMD = Await.result(MappingDetails.add(mappingDetail), 5 seconds)
  //      persistedMD.id should be('defined)
  //
  //      val persistedMD2 = Await.result(MappingDetails.add(mappingDetail2), 5 seconds)
  //      persistedMD2.id should be('defined)
  //
  //      val persistedMDs = Set(persistedMD, persistedMD2)
  //
  //      Get(s"/locations") ~> route ~> check {
  //        response.status should be(StatusCodes.OK)
  //        val responseMDs = responseAs[Set[MappingDetail]]
  //        responseMDs should be(persistedMDs)
  //      }
  //    }
  //  }
  //
  //  describe("find a subset of mappingDetails by the Id or deviceId") {
  //
  //    it("should be able to find a mappingDetail by the deviceId") {
  //      val persistedMappingDetail = Await.result(MappingDetails.add(mappingDetail), 5 seconds)
  //      persistedMappingDetail.id should be('defined)
  //
  //      val persistedMappingDetail2 = Await.result(MappingDetails.add(mappingDetail2), 5 seconds)
  //      persistedMappingDetail2.id should be('defined)
  //
  //      Get(s"/locations?deviceId=$deviceId") ~> route ~> check {
  //        response.status should be(StatusCodes.OK)
  //        val responseMappingDetails = responseAs[List[MappingDetail]]
  //        responseMappingDetails.size should be(1)
  //        responseMappingDetails(0) should be(persistedMappingDetail)
  //      }
  //    }
  //
  //    it("should return a empty list if no mappingDetail with that deviceId could be found") {
  //      Get(s"/locations?deviceId=NOTFOUND") ~> route ~> check {
  //        response.status should be(StatusCodes.OK)
  //        val responseMappingDetails = responseAs[List[MappingDetail]]
  //        responseMappingDetails.size should be(0)
  //      }
  //    }

  //    it("should be able to find a mappingDetail by the Id") {
  //
  //      val persistedMappingDetail = Await.result(MappingDetails.add(mappingDetail), 5 seconds)
  //      persistedMappingDetail.id should be('defined)
  //      val persistId = persistedMappingDetail.id.get
  //
  //      Get(s"/locations/$persistId") ~> route ~> check {
  //        response.status should be(StatusCodes.OK)
  //        val responseMappingDetail = responseAs[MappingDetail]
  //        responseMappingDetail should be(persistedMappingDetail)
  //      }
  //    }
  //
  //    it("should answer with a BadRequest if the mappingDetail could not be found") {
  //      val nonexistentId = "does_not_exist"
  //      Get(s"/locations/$nonexistentId") ~> route ~> check {
  //        response.status should be(StatusCodes.BadRequest)
  //        response.entity should be(Empty)
  //      }
  //    }
  //  }



  before {
    Await.result(DeviceDetails.removeAll(), 5 seconds)
  }
}
