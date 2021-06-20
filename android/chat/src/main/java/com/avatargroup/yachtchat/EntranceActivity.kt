package com.avatargroup.yachtchat

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_entrance.button
import org.jetbrains.anko.startActivity

class EntranceActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrance)

        button.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.button -> enterChatroom()
        }
    }

    private fun enterChatroom() {
        val roomName = "1"
        val userName = "anon"
        if (!roomName.isNullOrBlank() && !userName.isNullOrBlank()) {
            startActivity<ChatRoomActivity>(
                "userName" to "userName",
                "roomName" to "1"
            )
        } else {
            Toast.makeText(this, "Nickname and Roomname should be filled!", Toast.LENGTH_SHORT)
        }
    }
}
