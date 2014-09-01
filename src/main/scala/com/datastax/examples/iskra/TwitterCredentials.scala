package com.datastax.examples.iskra

/**
 * Created by rustam on 01/09/2014.
 */
object TwitterCredentials
{
  val consumerKey = "keykeykey"
  val consumerSecret = "keykeykey"
  val accessToken = "keykeykey-keykeykey"
  val accessTokenSecret = "keykeykey"

  def setCredentials(): Unit =
  {
    // Set the system properties so that Twitter4j library used by twitter stream
    // can use them to generat OAuth credentials
    System.setProperty("twitter4j.oauth.consumerKey", consumerKey)
    System.setProperty("twitter4j.oauth.consumerSecret", consumerSecret)
    System.setProperty("twitter4j.oauth.accessToken", accessToken)
    System.setProperty("twitter4j.oauth.accessTokenSecret", accessTokenSecret)
  }
}