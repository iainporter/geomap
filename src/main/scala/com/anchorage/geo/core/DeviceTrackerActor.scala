package com.anchorage.geo.core

import akka.actor.Actor
import scala.concurrent.{Future, ExecutionContext}
import com.anchorage.geo.Mongo.DeviceDetails
import com.anchorage.geo.model.Device
import com.anchorage.geo.{Location, MappingDetail}
import com.anchorage.geo.core.DeviceTrackerActor.{GetAllDevices, AddLocation, AddDevice}


object DeviceTrackerActor {
  case class AddDevice(device: Device)
  case class AddLocation(deviceId: String, location: Location)
  case class GetAllDevices()
}

class DeviceTrackerActor extends Actor {
  import ExecutionContext.Implicits.global
  def actorRefFactory = context

  def receive: Receive = {
    case AddDevice(device) => DeviceDetails.add(device)
      sender ! device
    case AddLocation(deviceId, location) =>
      val device: Future[List[Device]] = DeviceDetails.findByDeviceId(deviceId)
      device onSuccess {
        case device => {
          val head = device.head
          head.addLocation(new MappingDetail(location))
          DeviceDetails.update(head)
          sender ! head
        }

      }
    case GetAllDevices => {
      sender !  DeviceDetails.findAll().mapTo[List[Device]]
    } //TODO: Implement Paging

  }

}


