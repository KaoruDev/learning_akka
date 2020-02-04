package com.kaoruk.actors.serialization

import akka.actor.{Actor, Props}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Random

import org.slf4j.LoggerFactory

class UnorderedActor(implicit ec: ExecutionContext) extends Actor {
  val rand = new Random()
  val logger = LoggerFactory.getLogger(UnorderedActor.getClass)

  def receive: Receive = {
    case number:Int =>
      val waitTime = rand.nextInt(50) + 10
      logger.info(s"Received $number, waiting for $waitTime milliseconds")
      context.system.scheduler.scheduleOnce(waitTime.millisecond)({
        logger.info(s"Finished processing $number")
      })
  }
}

object UnorderedActor {
  def props(implicit ec: ExecutionContext): Props = Props(new UnorderedActor())
}
