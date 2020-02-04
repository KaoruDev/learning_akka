package com.kaoruk.actors.serialization

import akka.actor.ActorSystem

import org.slf4j.LoggerFactory

object Runner extends App {
  val logger = LoggerFactory.getLogger(getClass.getCanonicalName)
  val system = ActorSystem("Actor-Refs")

  val actor = system.actorOf(OrderedActor.props(system.dispatcher))
//  val actor = system.actorOf(UnorderedActor.props(system.dispatcher))
  for (n <- 1 to 20) {
    actor ! n
  }


  logger.info("Queued up messages, waiting now")
  Thread.sleep(1000)
  logger.info("Starting to terminate system")
  system.terminate().onComplete(_ => logger.info("Terminated"))(system.dispatcher)
}
