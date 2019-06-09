package com.kaoruk.actors.refs

import akka.actor.Actor
import org.slf4j.LoggerFactory

private[refs] class OneTimeActor extends Actor {
  private val logger = LoggerFactory.getLogger(getClass.getCanonicalName)


  override def preStart(): Unit = {
    super.preStart()
    logger.info("Started actor on path: {}", self.path)
  }

  override def receive: Receive = {
    case ("Ping", n: Int) =>
      logger.info("PONG {}", n)
    case "Boom" =>
      throw new Exception("BOOM")
  }

  override def finalize(): Unit = {
    super.finalize()
    logger.info("I'm being GCed!")
  }
}
