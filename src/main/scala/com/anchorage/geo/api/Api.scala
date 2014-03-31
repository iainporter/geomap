package com.anchorage.geo.api


import spray.routing.RouteConcatenation
import akka.actor.Props
import com.anchorage.geo.core.{Core, CoreActors}
import com.anchorage.geo.{DeviceTrackingService}

/**
 * The REST API layer. It exposes the REST services, but does not provide any
 * web server interface.<br/>
 * Notice that it requires to be mixed in with ``core.CoreActors``, which provides access
 * to the top-level actors that make up the system.
 */
trait Api extends RouteConcatenation {
  this: CoreActors with Core =>

  private implicit val _ = system.dispatcher

  val routes =
    new DeviceTrackingService(deviceActor).route

  val rootService = system.actorOf(Props(new RoutedHttpService(routes)))

}
