package com.linkshare.lib

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 11.04.12
 * Time: 22:26
 * To change this template use File | Settings | File Templates.
 */

import net.liftweb.util.Schedule
import net.liftweb.actor.LiftActor
import net.liftweb.util.Helpers._
import net.liftweb.common.Logger

object FetchRssTask extends LiftActor with Logger {

  case class DoIt()
  case class Stop()

  private var stopped = false

  def messageHandler = {
    case DoIt =>
      if (!stopped)
        Schedule.schedule(this, DoIt, 2 minutes)
        HomepageActor ! FetchIt("http://www.google.com/news?pz=1&cf=all&ned=us&hl=en&output=rss")

    case Stop =>
      stopped = true
  }
}
