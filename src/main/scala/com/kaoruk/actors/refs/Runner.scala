package com.kaoruk.actors.refs

import akka.actor.{ActorSystem, PoisonPill, Props}
import org.slf4j.LoggerFactory

private[refs] object Runner extends App {
  val logger = LoggerFactory.getLogger(getClass.getCanonicalName)
  val system = ActorSystem("Actor-Refs")
  val actor = system.actorOf(Props(new OneTimeActor))
  val actorPath = actor.path

  for (n <- 1 to 18) {
//    val targetActor = actor
    val targetActor = system.actorSelection(actorPath)
    if (n % 9 == 0) {
      targetActor ! "Boom"
    } else {
      targetActor ! ("Ping", n)
    }
  }

  Thread.sleep(1000)
  logger.info("Starting to terminate system")
  system.terminate().onComplete(_ => logger.info("Terminated"))(system.dispatcher)
}
