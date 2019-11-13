package com.kaoruk.actors.blocking

import akka.actor.Actor
import scala.concurrent.Future

import org.slf4j.LoggerFactory

private[blocking] class BlockingEchoActor extends Actor {
  val logger = LoggerFactory.getLogger(getClass.getCanonicalName)

  override def preStart(): Unit = {
    super.preStart()
    logger.info("Starting up")
  }

  override def receive: Receive = {
    case "boom" =>
      logger.info("BOOOOM")
      throw new Exception("BOOM!")
    case message: String =>
      logger.info("Received {}", message)
      val ogSender = sender
      Future {
//        Thread.sleep(500)
        ogSender ! message
      }(context.dispatcher)
    case message =>
      logger.info("Non-String message: {}", message)
      sender ! ()
  }

  override def finalize(): Unit = {
    super.finalize()
    logger.info("successfully garbage collected")
  }
}
