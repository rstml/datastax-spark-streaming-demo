package com.datastax.examples.iskra

import twitter4j.auth.{Authorization, OAuthAuthorization}
import twitter4j.conf.ConfigurationBuilder

/**
 * Created by rustam on 01/09/2014.
 */
object TwitterCredentials
{
  val consumerKey = "*****"
  val consumerSecret = "*****"
  val accessToken = "*****-*****"
  val accessTokenSecret = "*****"

  def setCredentials()
  {
    // Set the system properties so that Twitter4j library used by twitter stream
    // can use them to generat OAuth credentials
    System.setProperty("twitter4j.oauth.consumerKey", consumerKey)
    System.setProperty("twitter4j.oauth.consumerSecret", consumerSecret)
    System.setProperty("twitter4j.oauth.accessToken", accessToken)
    System.setProperty("twitter4j.oauth.accessTokenSecret", accessTokenSecret)
  }

  def getCredentials(): Option[Authorization] =
  {
    val builder = new ConfigurationBuilder()

    builder.setOAuthConsumerKey(consumerKey).
            setOAuthConsumerSecret(consumerSecret).
            setOAuthAccessToken(accessToken).
            setOAuthAccessTokenSecret(accessTokenSecret)

    val auth = new OAuthAuthorization(builder.build())

    return Option(auth)
  }
}