package com.anchorage.geo

import akka.actor.ActorRef
import scala.concurrent.ExecutionContext
import akka.pattern.ask
import spray.routing.Directives
import com.anchorage.geo.api.DefaultJsonFormats
import akka.util.Timeout
import spray.http.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import scala.concurrent.duration._
import com.anchorage.geo.model.Device
import com.anchorage.geo.core.DeviceTrackerActor._
import com.anchorage.geo.Mongo.DeviceDetails


class DeviceTrackingService(deviceActor: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with DefaultJsonFormats {

  implicit val timeout: Timeout = 5 seconds

  lazy val route =
    path("devices") {
      post {
        postRoute
      } ~
        get {
          getRoute
        } ~
        delete {
          deleteRoute
        }
    }~
  path("devices" / Segment / "locations") { deviceId =>
    post{
      entity(as[Location]) {
        location =>
          complete {
            (deviceActor ? AddLocation(deviceId, location)).mapTo[Device]
            StatusCodes.Created
          }
      }
    }

  }


  /**
   * Uses the entity directive in conjunction with and spray.httpx.unmarshalling to convert wire format into
   * a Device object
   */
  protected lazy val postRoute =
    entity(as[Device]) {
      device =>
        complete {
          StatusCodes.Created -> (deviceActor ? AddDevice(device)).mapTo[Device]
        }
    }


  protected lazy val getRoute =
//    path(JavaUUID) {
//      id ⇒
//        complete {
//          (deviceActor ? GetLocationById(id.toString())).mapTo[MappingDetail].map(md => md
//          match {
//            case MappingDetail(_, _, _, _, _) ⇒
//              HttpResponse(
//                StatusCodes.OK,
//                HttpEntity(ContentTypes.`application/json`, md.toJson.prettyPrint)
//              )
//            case _ ⇒
//              HttpResponse(StatusCodes.BadRequest)
//          })
//        }
//    } ~
      parameters('deviceId.as[String]) {
        name ⇒
          detach() {
            complete {
              DeviceDetails.findByDeviceId(name)
            }
          }
      } ~ {
      complete {
        DeviceDetails.findAll()
      }
    }

  protected lazy val deleteRoute =
    detach() {
      dynamic {
        DeviceDetails.removeAll()
        complete(StatusCodes.OK)
      }
    }
}
