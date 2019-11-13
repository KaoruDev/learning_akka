package com.kaoruk.actors.blocking

import akka.actor.Actor
import scala.concurrent.Future
import scala.util.Random

import org.slf4j.LoggerFactory

import java.util.concurrent.ThreadLocalRandom

private[blocking] class BlockingEchoActor extends Actor {
  val logger = LoggerFactory.getLogger(getClass.getCanonicalName)
  var currentNumber = 0

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
      // this future will allow us to pretend like we're doing something async and thus corrupt the state of the actor
      Future {
        val number = ThreadLocalRandom.current().nextInt(50, 500)
        logger.info(s"$message assign random number: $number, currentNumber: $currentNumber")
        currentNumber = number
        // add this increases the likelihood of executions appear non-serial and therefore
        // we are more consistently able to corrupt the internal state of the actor.
        Thread.sleep(number)
        ogSender ! s"reply to $message with currentNumber: $currentNumber, number: $number"
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
