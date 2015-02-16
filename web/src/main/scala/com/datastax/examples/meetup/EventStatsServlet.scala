package com.datastax.examples.meetup

import org.joda.time.{DateTimeZone, DateTime, Duration}
import org.scalatra.scalate.ScalateSupport
import org.scalatra.{CorsSupport, ScalatraServlet}

import scala.concurrent.Await
import scala.concurrent.duration._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

class EventStatsServlet() extends ScalatraServlet with CorsSupport with JacksonJsonSupport with ScalateSupport
{
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  options("/*"){
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
  }

  get("/trending") {
    val time = new DateTime(DateTimeZone.UTC)

    // Scan 5 second intervals within the past 1 minute.
    // Stop as soon as first successful found.
    val result = (for (i <- Stream range (0,12); v = getTrendingTopics(i, time); if v.nonEmpty) yield v).headOption

    // Order topics by count in desc order and take top 20
    result.map(r => r.toIndexedSeq.sortBy(_._2).reverse.take(20))
  }

  get("/countries") {
    val attendeesByCountry = Event.dimensions("attending", "ALL")

    Await.result(attendeesByCountry, 5 seconds)
      .map{ case (a,b) => Map("code" -> a.toUpperCase, "value" -> b)}
  }

  get("/") {
    contentType="text/html"
    layoutTemplate("dashboard.ssp")
  }

  def roundDateTime(t: DateTime, d: Duration) = {
    t minus (t.getMillis - (t.getMillis.toDouble / d.getMillis).round * d.getMillis)
  }

  def getTrendingTopics(i:Int, time:DateTime) = {
    val t = roundDateTime(time minusSeconds 5*i, Duration.standardSeconds(5))
    val trendingTopics = Event.dimensions("trending", "S" + t.toString("yyyyMMddHHmmss"))
    Await.result(trendingTopics, 5 seconds)
  }
}