import AssemblyKeys._

name := "iskra"

version := "1.0"

scalaVersion := "2.10.4"

organization := "com.datastax.examples"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "org.apache.spark" % "spark-core_2.10" % "0.9.1" % "provided"

libraryDependencies += "org.apache.spark" % "spark-streaming_2.10" % "0.9.1" % "provided"

libraryDependencies += "org.apache.spark" % "spark-streaming-twitter_2.10" % "0.9.1" % "provided"

libraryDependencies += "com.datastax.spark" %% "spark-cassandra-connector" % "1.0.0-rc4" % "provided" withSources() withJavadoc()

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "1.4.0"

//We do this so that Spark Dependencies will not be bundled with our fat jar but will still be included on the classpath
//When we do a sbt/run
run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))

assemblySettings