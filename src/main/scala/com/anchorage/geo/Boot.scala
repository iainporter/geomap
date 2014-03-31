package com.anchorage.geo


import com.anchorage.geo.core.{CoreActors, BootedCore}
import com.anchorage.geo.api.Api

object Boot extends App with BootedCore with CoreActors with Api with Web