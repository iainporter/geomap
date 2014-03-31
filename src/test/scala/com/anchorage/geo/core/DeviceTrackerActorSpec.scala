package com.anchorage.geo.core

import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import akka.actor.ActorSystem
import org.specs2.mutable.SpecificationLike
import com.anchorage.geo.model.Device
import com.anchorage.geo.core.DeviceTrackerActor.{AddLocation, AddDevice}
import com.anchorage.geo.{Location, MappingDetail}
import org.joda.time.DateTime
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.Success


/**
 * Created by iainporter on 26/02/2014.
 */
class DeviceTrackerActorSpec  extends TestKit(ActorSystem()) with SpecificationLike with ImplicitSender {

  val actorRef = TestActorRef(new DeviceTrackerActor)
  implicit val timeout: Timeout = Timeout(5000.milli)


  val device = Device(deviceId="123456789", name="myIPhone", model="iPhone", os="IOS 4.1", manufacturer = "Apple", mappingDetails = Nil)
  val latitude = 51.8409093
  val longitude = -2.5812894
  val loc = Location(longitude = longitude, latitude = latitude)

  var mappingDetail = new MappingDetail(loc)

  "Add Device should" >> {

    "accept valid Device" in {
      val future = actorRef ? AddDevice(device)
      val Success(result: Device) = future.value.get
      assert(result.mappingDetails.length == 0)
      success
    }
  }

  "Add Location to device should" >> {

    "add valid Location" in {
        actorRef ! AddLocation(device.deviceId, loc)
      //TODO: Add some assertions here on the received message
        success
    }
  }


}