package com.kaoruk.actors.serialization

import akka.actor.{Actor, Props}
import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Random

import com.kaoruk.actors.serialization.OrderedActor.Poll
import org.slf4j.LoggerFactory

class OrderedActor(implicit ec: ExecutionContext) extends Actor {
  val rand = new Random()
  val logger = LoggerFactory.getLogger(UnorderedActor.getClass)

  var busy = false
  val buffer = new mutable.Queue[Int]

  def receive: Receive = {
    case number:Int =>
      buffer.enqueue(number)
      logger.info(s"Received $number, enqueing")
      self ! Poll
    case Poll =>
      if (!busy && buffer.nonEmpty) {
        val number = buffer.dequeue()
        val waitTime = rand.nextInt(50) + 10
        val me = self
        busy = true

        logger.info(s"Processing $number, waiting for $waitTime milliseconds")
        context.system.scheduler.scheduleOnce(waitTime.millisecond)({
          logger.info(s"Finished processing $number")
          busy = false
          me ! Poll
        })
      }

  }
}

object OrderedActor {
  case object Poll

  def props(implicit ec: ExecutionContext): Props = Props(new OrderedActor())
}


