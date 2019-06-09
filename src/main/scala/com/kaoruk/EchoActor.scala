package com.kaoruk

import akka.actor.Actor
import org.slf4j.LoggerFactory

class EchoActor extends Actor {
  val logger = LoggerFactory.getLogger(getClass.getCanonicalName)


  override def preStart(): Unit = {
    super.preStart()
    logger.info("Starting up")
  }

  override def receive: Receive = {
    case "boom" =>
      logger.info("BOOOOM")
      throw new Exception("BOOM!")
    case message =>
      logger.info("{}", message)
      sender ! ()
  }

  override def finalize(): Unit = {
    super.finalize()
    logger.info("successfully garbage collected")
  }
}
