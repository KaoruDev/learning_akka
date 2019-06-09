package com.kaoruk.actors

import akka.actor.{Actor, Props}
import com.kaoruk.actors.ActorHooks.Callback
import org.slf4j.LoggerFactory

class ActorWithCallback(registerCallback: Callback => Unit) extends Actor {
  private val logger = LoggerFactory.getLogger(getClass.getCanonicalName)

  registerCallback(handleCallback)

  override def preStart(): Unit = {
    super.preStart()
    logger.info("Starting up")
  }

  override def receive: Receive = {
    case "Ping" =>
      logger.info("Pong")
      sender ! Unit
    case "boom" =>
      logger.info("BOOOOM")
      throw new Exception("BOOM!")
  }

  private def handleCallback(message: String): Unit = {
    logger.info(s"Handling call back $message")
  }

  override def finalize(): Unit = {
    super.finalize()
    logger.info(s"${getClass.getCanonicalName} successfully garbage collected")
  }
}

object ActorWithCallback {
  def props(registerCallback: Callback => Unit): Props = Props(new ActorWithCallback(registerCallback))
}

