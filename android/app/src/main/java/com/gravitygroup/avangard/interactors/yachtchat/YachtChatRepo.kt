package com.gravitygroup.avangard.interactors.yachtchat

import android.content.Context
import java.net.URISyntaxException

class YachtChatRepo(
    private val context: Context
) {

//    private var mSocket: java.net.Socket? = null

    init {
        try {
//            mSocket = IO.socket("http://chat.socket.io")
        } catch (e: URISyntaxException) {
        }
    }

//    suspend fun connect() {
//        mSocket?.on("new message", onNewMessage)
//        mSocket?.connect()
//    }
//
//    fun disconnect() {
//        mSocket?.disconnect();
//        mSocket?.off("new message", onNewMessage);
//    }

//    private val onNewMessage = Emitter.Listener { args ->
//       context.runOnUiThread(Runnable {
//            val data = args[0] as JSONObject
//            val username: String
//            val message: String
//            try {
//                username = data.getString("username")
//                message = data.getString("message")
//            } catch (e: JSONException) {
//                return@Runnable
//            }
//
//            // add the message to view
//            addMessage(username, message)
//        })
//    }
}