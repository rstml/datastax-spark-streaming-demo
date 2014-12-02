package com.datastax.examples.iskra.websocket

import org.apache.spark.storage.StorageLevel
import scalawebsocket.WebSocket
import org.apache.spark.streaming.receiver.Receiver
import org.apache.spark.Logging

class WebSocketReceiver(
            url: String,
            storageLevel: StorageLevel
      ) extends Receiver[String](storageLevel) with Logging {

  @volatile private var webSocket: WebSocket = _

  def onStart() {
    try{
      logInfo("Connecting to WebSocket: " + url)
      val newWebSocket = WebSocket().open(url).onTextMessage({ msg: String => store(msg) })
      setWebSocket(newWebSocket)
      logInfo("Connected to: WebSocket" + url)
    } catch {
      case e: Exception => restart("Error starting WebSocket stream", e)
    }
  }

  def onStop() {
    setWebSocket(null)
    logInfo("WebSocket receiver stopped")
  }

  private def setWebSocket(newWebSocket: WebSocket) = synchronized {
    if (webSocket != null) {
      webSocket.shutdown()
    }
    webSocket = newWebSocket
  }
}