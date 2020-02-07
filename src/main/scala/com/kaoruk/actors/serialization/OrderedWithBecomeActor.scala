package com.kaoruk.actors.serialization

import akka.actor.{Actor, Props, Stash}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Random


object OrderedWithBecomeActor {
  case object Poll

  def props(implicit ec: ExecutionContext): Props = Props(new OrderedWithBecomeActor())
}


/**
 * Become flow:
 *
 * 1. start as `firstStage`
 *    - stash received `Int`
 *    - as soon as the first number arrives:
 *       - send a `Poll` command
 *       - become `stashWhileHandlingInt`
 * 2. as `stashWhileHandlingInt`
 *    - stash received `Int`s
 *    - on `Poll` command
 *       - unstash
 *       - become `handlingInt`
 * 3. as `handlingInt`
 *    - schedule `Int` processing to some time in the near future
 *       - when finished send a new `Poll` to start processing he next message
 *    - become `stashWhileHandlingInt`
 * @param ec
 */
class OrderedWithBecomeActor(implicit ec: ExecutionContext) extends Actor with Stash {
  val rand = new Random()
  val logger = LoggerFactory.getLogger(OrderedWithBecomeActor.getClass)

  var messages: Seq[String] = Seq.empty

  import OrderedWithBecomeActor._

  def receive: Receive = firstStage

  // first stage is needed to we trigger an initial Poll() command
  // once it's fired, there's no need to get back here since every time
  //    we complete handling a number, that will trigger the next Poll()
  def firstStage: Receive = {
    case number: Int =>
      this.stash()
      logger.info(s"First number $number stashed")
      context.become(stashWhileHandlingInt)
      self ! Poll
  }

  def stashWhileHandlingInt: Receive = {
    case number: Int =>
      logger.info(s"Stashed $number. Got it either from a brand new message or from the unstash call")
      this.stash()
    case Poll =>
      this.unstashAll()
      context.become(handlingInt)

  }

  def handlingInt: Receive = {
    case number: Int =>
      val waitTime = rand.nextInt(50) + 10
      val me = self
      logger.info(s"Processing $number, waiting for $waitTime milliseconds")
      context.become(stashWhileHandlingInt)
      context.system.scheduler.scheduleOnce(waitTime.millisecond)({
        logger.info(s"Finished processing $number, polling the next one")
        me ! Poll
      })
  }

  override def postStop(): Unit = {
    super.postStop()
    messages.foreach(println)
  }
}




