package com.wolfe.kotlinmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.wolfe.kotlinmessenger.objects.ChatMessage
import com.wolfe.kotlinmessenger.objects.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.chat_from.view.*
import kotlinx.android.synthetic.main.chat_to.view.*

private val TAG = ChatLogActivity::class.qualifiedName

class ChatItemFrom(val text: String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.fromMessage.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_from
    }

}

class ChatItemTo(val text: String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.toMessage.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to
    }

}

class ChatLogActivity : AppCompatActivity() {
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        recyclerViewChatLog.adapter = adapter

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user?.username

        listenForMessages()

        sendChatLog.setOnClickListener {
            Log.d(TAG, "Sending Message Attempt")
            performSendMessage()
        }

    }

    private fun listenForMessages() {
        val ref = FirebaseDatabase.getInstance().getReference("/messages")
        val fromId = FirebaseAuth.getInstance().uid
        val toId = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)?.uid
        if(fromId == null || toId == null) return

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if(chatMessage != null
                        && (fromId == chatMessage.fromId || fromId == chatMessage.toId)
                        && (toId == chatMessage.fromId || toId == chatMessage.toId)){
                    if(fromId == chatMessage.fromId){
                        adapter.add(ChatItemTo(chatMessage.text))
                    } else {
                        adapter.add(ChatItemFrom(chatMessage.text))
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        })

    }

    private fun performSendMessage() {
        val ref = FirebaseDatabase.getInstance().getReference("/messages").push()
        val fromId = FirebaseAuth.getInstance().uid
        val toId = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)?.uid
        if(fromId == null || toId == null) return

        val text = messageChatLog.text.toString()
        val chatMessage = ChatMessage(ref.key!!, fromId, toId, text, System.currentTimeMillis())

        ref.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG,"Upload Message Success: ${ref.key}")
                    messageChatLog.setText("")
                }
                .addOnFailureListener {
                    Log.d(TAG,"Upload Message Failure: ${it.message}")
                }
    }
}

