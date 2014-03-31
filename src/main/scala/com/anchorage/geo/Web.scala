package com.anchorage.geo


import akka.io.IO
import spray.can.Http
import com.anchorage.geo.core.{CoreActors, Core}
import com.anchorage.geo.api.Api
import com.typesafe.config.ConfigFactory
import akka.util.Timeout
import scala.concurrent.duration._

/**
 * Provides the web server (spray-can) for the REST api in ``Api``, using the actor system
 * defined in ``Core``.
 *
 * You may sometimes wish to construct separate ``ActorSystem`` for the web server machinery.
 * However, for this simple application, we shall use the same ``ActorSystem`` for the
 * entire application.
 *
 * Benefits of separate ``ActorSystem`` include the ability to use completely different
 * configuration, especially when it comes to the threading model.
 */
trait Web {
  this: Api with CoreActors with Core =>


  private lazy val config = ConfigFactory.load()
  private lazy val serverAddress = config.getString("com.anchorage.address")
  private lazy val serverPort = config.getInt("com.anchorage.port")


  implicit val timeout: Timeout = Timeout(5 seconds)


  IO(Http)(system) ! Http.Bind(rootService, interface = serverAddress, port = serverPort)

}
