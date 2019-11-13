package com.kaoruk.actors.blocking

import akka.actor.{ActorSystem, Props}
import akka.pattern._
import akka.util.Timeout
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import org.slf4j.LoggerFactory

import java.util.concurrent.Executors

object Runner extends App {
  val logger = LoggerFactory.getLogger(getClass.getCanonicalName)
  val pool = Executors.newSingleThreadExecutor()
  val ec = ExecutionContext.fromExecutor(pool)
  val system = ActorSystem("Actor-Refs", None, None, Some(ec))
  val actor = system.actorOf(Props(new BlockingEchoActor))
  implicit val timeout = Timeout(1.second)
  val actorPath = actor.path

  pool.submit(new Runnable {
    override def run(): Unit = {
      for (i <- 1 to 10) {
        logger.info(s"sending hello $i...")
        (actor ? s"hello $i").mapTo[String].foreach(response => {
          logger.info(s"got response: $response")
        })(system.dispatcher)
      }
    }
  })

  Thread.sleep(10000)
  logger.info("Starting to terminate system")
  system.terminate().onComplete(_ => {
    logger.info("Terminated")
    pool.shutdown()
  })(system.dispatcher)
}
