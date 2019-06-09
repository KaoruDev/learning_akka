name := "learning_akka"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies ++=Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.23",
  "org.slf4j" % "slf4j-api" % "1.7.26",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "ch.qos.logback" % "logback-core" % "1.2.3",
)


resolvers ++= Seq(
  DefaultMavenRepository,
  Resolver.sonatypeRepo("public") ,
  Resolver.typesafeRepo("releases"),
  Resolver.typesafeIvyRepo("releases"),
  Resolver.url("Akka Snapshot Repository", url("https://repo.akka.io/snapshots/"))
)
