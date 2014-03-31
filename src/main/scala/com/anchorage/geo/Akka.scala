package com.anchorage.geo

import akka.actor.ActorSystem

object Akka {
  implicit val actorSystem = ActorSystem("actorsystem")
}
