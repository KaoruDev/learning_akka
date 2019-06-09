package com.kaoruk

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorSystem, PoisonPill, Props}
import akka.pattern.ask
import akka.util.Timeout
import org.slf4j.LoggerFactory

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}


object Hook extends App {
  type Callback = String => Unit
  val logger = LoggerFactory.getLogger(getClass.getCanonicalName)

  val system = ActorSystem("Hook-it")
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val timeout = Timeout(1, TimeUnit.SECONDS)

  var hooks: List[Callback] = Nil

  val registerCallback = (subscriber: Callback) => {
    logger.warn("subscribing hook")
    hooks = subscriber :: hooks
  }

  val testActor = system.actorOf(ActorWithCallback.props(registerCallback))

  (for {
    _ <- ask(testActor, "Ping")
    _ = hooks.foreach(_.apply("I AM CALLBACK"))
    _ <- ask(testActor, "boom")
  } yield ()).onComplete(
    result => {
      hooks.foreach(_.apply("Still alive?"))
    }
  )

  val echoActor = system.actorOf(Props(new EchoActor))

  for {
    _ <- ask(echoActor, "Ping")
    _ <- ask(echoActor, "boom")
  } yield ()

  Thread.sleep(1000)

  Await.result(system.terminate(), Duration.apply(1, TimeUnit.MINUTES))
}

