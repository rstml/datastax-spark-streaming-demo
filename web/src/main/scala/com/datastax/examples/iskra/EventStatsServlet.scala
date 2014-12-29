package com.datastax.examples.iskra

import org.scalatra.scalate.ScalateSupport
import org.scalatra.{CorsSupport, ScalatraServlet}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
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

  get("/events") {
    val hashtagStats = for {
      iphone <- Event.hourly("iphone")
      android <- Event.hourly("android")
    } yield (iphone, android)

    val stats = Await.result(hashtagStats, 5 seconds)

    Map(
      "iphone" -> stats._1.map{ case (i,m) => (i.stripPrefix("M"), m)},
      "android" -> stats._2.map{ case (i,m) => (i.stripPrefix("M"), m)}
    )
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

}