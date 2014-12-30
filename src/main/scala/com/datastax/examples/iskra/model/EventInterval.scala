package com.datastax.examples.iskra.model

import org.joda.time.{DateTimeZone, DateTime}
import org.apache.spark.streaming.Time

/**
 * Created by rustam on 29/12/2014.
 */
object EventInterval {
  val All = "ALL"

  def Seconds(time: Time): String =
    "S" + new DateTime(time.milliseconds, DateTimeZone.UTC).toString("yyyyMMddHHmmss")
}