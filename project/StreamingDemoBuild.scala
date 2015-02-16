/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import sbt._
import sbt.Keys._

object StreamingDemoBuild extends Build {
  import Settings._

  val streamingDemo = (project in file(".")).settings(defaultSettings: _*).settings(libraryDependencies ++= Dependencies.streamingDemo)
}

object Dependencies {
  import Versions._

  object Compile {
    val cassandraThrift  = "org.apache.cassandra"   % "cassandra-thrift"           % Cassandra excludeAll(ExclusionRule("commons-logging"))
    val cassandraClient  = "org.apache.cassandra"   % "cassandra-clientutil"       % Cassandra excludeAll(ExclusionRule("commons-logging"))
    val cassandraDriver  = "com.datastax.cassandra" % "cassandra-driver-core"      % CassandraDriver exclude("com.google.guava", "guava")
    val slf4jApi         = "org.slf4j"              % "slf4j-api"                  % Slf4j % "provided"
    val sparkCore        = "org.apache.spark"       %% "spark-core"                % Spark % "provided"
    val sparkStreaming   = "org.apache.spark"       %% "spark-streaming"           % Spark % "provided"
    val sparkCassandra   = "com.datastax.spark"     %% "spark-cassandra-connector" % SparkCassandra exclude("com.esotericsoftware.minlog", "minlog") excludeAll(ExclusionRule("commons-beanutils")) excludeAll(ExclusionRule("commons-logging")) excludeAll(ExclusionRule("org.apache.spark"))
    val websockets       = "eu.piotrbuda"           %% "scalawebsocket"            % "0.1.1"
  }

  import Compile._

  val streamingDemo = Seq(cassandraThrift, cassandraClient, cassandraDriver, sparkCassandra, slf4jApi, sparkCore, sparkStreaming, websockets)
}

object Settings extends Build {
  import sbtassembly._
  import AssemblyKeys._

  val buildSettings = Seq(
    version in ThisBuild := "1.1.1-SNAPSHOT",
    scalaVersion := Versions.Scala
  )

  override lazy val settings = super.settings ++ buildSettings

  val defaultSettings = Seq(
    updateOptions := updateOptions.value.withCachedResolution(true),
    assemblyJarName in assembly := "streaming-demo.jar",
    assemblyExcludedJars in assembly := {
      val cp = (fullClasspath in assembly).value
      cp filter {_.data.getName.endsWith("sources.jar")}
    },
    run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run)),
    parallelExecution in assembly := false,
    assemblyMergeStrategy in assembly := {
      case x if Assembly.isConfigFile(x) =>
        MergeStrategy.concat
      case PathList(ps @ _*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) =>
        MergeStrategy.rename
      case PathList("META-INF", xs @ _*) =>
        (xs map {_.toLowerCase}) match {
          case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
            MergeStrategy.discard
          case ps @ (x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".rsa") || ps.last.endsWith("mailcap") =>
            MergeStrategy.discard
          case _ => MergeStrategy.deduplicate
        }
      case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
      case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
      case PathList(ps @ _*) if ps.last endsWith "plugin.properties" => MergeStrategy.concat
      case "application.conf"                            => MergeStrategy.concat
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )
}

object Versions {
  val Cassandra      = "2.1.2"
  val CassandraDriver = "2.1.3"
  val CommonsLang3    = "3.3.2"
  val JDK            = "1.7"
  val Scala          = "2.10.4"
  val Slf4j          = "1.7.5"
  val Spark          = "1.1.0"
  val SparkCassandra = "1.1.0"
}

object ShellPromptPlugin extends AutoPlugin {
  override def trigger = allRequirements
  override lazy val projectSettings = Seq(
    shellPrompt := buildShellPrompt
  )
  val devnull: ProcessLogger = new ProcessLogger {
    def info (s: => String) {}
    def error (s: => String) { }
    def buffer[T] (f: => T): T = f
  }
  def currBranch =
    ("git status -sb" lines_! devnull headOption).
      getOrElse("-").stripPrefix("## ")
  val buildShellPrompt: State => String = {
    case (state: State) =>
      val currProject = Project.extract (state).currentProject.id
      s"""$currProject:$currBranch> """
  }
}