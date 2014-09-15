package com.datastax.examples.iskra

import scala.sys.process._

object TwitterMonitor
{

  def main(args: Array[String]): Unit =
  {
    // Detect Spark Master and Cassandra entry point using dsetool
    val ipReg = """\d{1,3}.\d{1,3}.\d{1,3}.\d{1,3}""".r
    val master = try {"dsetool sparkmaster".!!.trim } catch { case x:Exception => "localhost"}
    val cassandraIp = ipReg findFirstIn (master) match {
      case Some(ipReg) => ipReg
      case None => "127.0.0.1"
    }

    // Setup Twitter access
    TwitterCredentials.setCredentials()
    // Start stream processing
    TwitterStreamProcessor.start(master, cassandraIp)
  }

}