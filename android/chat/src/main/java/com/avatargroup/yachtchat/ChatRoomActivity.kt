package com.avatargroup.yachtchat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.avatargroup.yachtchat.model.MessageType
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.Polling
import io.socket.engineio.client.transports.WebSocket
import kotlinx.android.synthetic.main.activity_chatroom.editText
import kotlinx.android.synthetic.main.activity_chatroom.leave
import kotlinx.android.synthetic.main.activity_chatroom.recyclerView
import kotlinx.android.synthetic.main.activity_chatroom.send

class ChatRoomActivity : AppCompatActivity(), View.OnClickListener {

    val TAG = ChatRoomActivity::class.java.simpleName

    lateinit var mSocket: Socket
    lateinit var userName: String;
    lateinit var roomName: String;

    val gson: Gson = Gson()

    //For setting the recyclerView.
    val chatList: ArrayList<Message> = arrayListOf();
    lateinit var chatRoomAdapter: ChatRoomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)


        send.setOnClickListener(this)
        leave.setOnClickListener(this)

        //Get the nickname and roomname from entrance activity.
        try {
            userName = intent.getStringExtra("userName")!!
            roomName = intent.getStringExtra("roomName")!!
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Set Chatroom adapter

        chatRoomAdapter = ChatRoomAdapter(this, chatList);
        recyclerView.adapter = chatRoomAdapter;

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        try {
            val opts: IO.Options = IO.Options()
            opts.transports = arrayOf(WebSocket.NAME, Polling.NAME)
            mSocket = IO.socket(SOCKET_URL, opts)
            mSocket.on(Socket.EVENT_CONNECT, onConnectClient)
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
            mSocket.connect()

            Log.d("success", mSocket.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("fail", "Failed to connect")
        }
    }

    private val onConnectError =
        Emitter.Listener {
            Log.d(TAG, "Error connecting...")
        }

    private val onConnectCaptain  = Emitter.Listener {
        mSocket.emit(NEW_USER_KEY, ROOM_ID, USER_ID)
    }

    private val onSubscribedCaptain = Emitter.Listener {
        Toast.makeText(this, getString(R.string.yacht_captain_incoming_order), Toast.LENGTH_SHORT).show()
    }

    private val onConnectClient = Emitter.Listener {
        mSocket.emit(SEND_MESSAGE_KEY, ROOM_ID, USER_ID)
    }

    var onNewUser = Emitter.Listener {
        val name = it[0] as String //This pass the userName!
        val chat = Message(name, "", roomName, MessageType.USER_JOIN.index)
        addItemToRecyclerView(chat)
        Log.d(TAG, "on New User triggered.")
    }

    private fun addItemToRecyclerView(message: Message) {

        //Since this function is inside of the listener,
        // You need to do it on UIThread!
        runOnUiThread {
            chatList.add(message)
            chatRoomAdapter.notifyItemInserted(chatList.size)
            editText.setText("")
            recyclerView.scrollToPosition(chatList.size - 1) //move focus on last message
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.send -> onDestroy()
            R.id.leave -> onDestroy()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val data = initialData(userName, roomName)
        val jsonData = gson.toJson(data)
        mSocket.emit("unsubscribe", jsonData)
        mSocket.disconnect()
    }

    companion object {
        private const val ROOM_ID = 1

        private const val USER_ID = 1

        private const val SEND_MESSAGE_KEY = "room-send-message"

        private const val NEW_USER_KEY = "room-new-user"

        private const val SOCKET_URL = "http://chat.qualitylive.su/"
    }
}
