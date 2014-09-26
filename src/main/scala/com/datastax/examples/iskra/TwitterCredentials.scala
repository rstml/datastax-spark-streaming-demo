package com.datastax.examples.iskra

/**
 * Created by rustam on 01/09/2014.
 */
object TwitterCredentials
{
  val consumerKey = "GSaQfKdRp6ptOnUfhiehmk8dF"
  val consumerSecret = "aUQSmntJ77kZBh36kLB1mMR0VvkZqsJamkndPV0NdB6zA1blZF"
  val accessToken = "46770301-LwFIC8RNtWf10Z1s4pnyFev4qnIU21UKlvP2RzJ6Y"
  val accessTokenSecret = "pXponW9TWnh6STyUCRVoCnzhhjJwM1QtuDhjdF6L1NLnh"

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