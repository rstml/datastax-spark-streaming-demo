import AssemblyKeys._
import sbt._

name := "iskra"

version := "1.0"

scalaVersion := "2.10.4"

organization := "com.datastax.examples"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "org.apache.spark" % "spark-core_2.10" % "1.1.0" % "provided"

libraryDependencies += "org.apache.spark" % "spark-streaming_2.10" % "1.1.0" % "provided"

libraryDependencies += "org.apache.spark" % "spark-sql_2.10" % "1.1.0" % "provided"

libraryDependencies += "com.datastax.spark" %% "spark-cassandra-connector" % "1.1.0" % "provided" withSources() withJavadoc()

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "1.4.0"

libraryDependencies += "eu.piotrbuda" %% "scalawebsocket" % "0.1.1"

//We do this so that Spark Dependencies will not be bundled with our fat jar but will still be included on the classpath
//When we do a sbt/run
run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))

assemblySettings

outputPath in assembly := file("target/streaming-demo.jar")