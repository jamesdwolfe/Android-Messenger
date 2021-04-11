package com.wolfe.kotlinmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.wolfe.kotlinmessenger.objects.ChatMessage
import com.wolfe.kotlinmessenger.objects.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.chat_from.view.*
import kotlinx.android.synthetic.main.chat_to.view.*

private val TAG = ChatLogActivity::class.qualifiedName

class ChatItemFrom(private val text: String, private val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.fromMessage.text = text
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.fromImage)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from
    }
}

class ChatItemTo(private val text: String, private val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.toMessage.text = text
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.toImage)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to
    }
}

class ChatLogActivity : AppCompatActivity() {
    private val adapter = GroupAdapter<GroupieViewHolder>()
    var fromUser: User? = null
    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        recyclerViewChatLog.adapter = adapter

        fromUser = LatestMessagesActivity.currentUser
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username

        listenForMessages()

        sendChatLog.setOnClickListener {
            Log.d(TAG, "Sending Message Attempt")
            performSendMessage()
        }

    }

    private fun listenForMessages() {
        val fromId = fromUser?.uid
        val toId = toUser?.uid
        if(fromUser == null || toUser == null || fromId == null || toId == null) return
        val ref = FirebaseDatabase.getInstance().getReference("/messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if(chatMessage != null) {
                    if(fromUser!!.uid == chatMessage.fromId){
                        adapter.add(ChatItemFrom(chatMessage.text, fromUser!!))
                    } else {
                        adapter.add(ChatItemTo(chatMessage.text, toUser!!))
                    }
                    recyclerViewChatLog.scrollToPosition(adapter.itemCount-1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

    }

    private fun performSendMessage() {
        val fromId = fromUser?.uid
        val toId = toUser?.uid
        if(fromUser == null || toUser == null || fromId == null || toId == null) return
        val ref = FirebaseDatabase.getInstance().getReference("/messages/$fromId/$toId").push()
        val refTo = FirebaseDatabase.getInstance().getReference("/messages/$toId/$fromId").push()

        val text = messageChatLog.text.toString()
        val chatMessage = ChatMessage(ref.key!!, fromId, toId, text, System.currentTimeMillis())

        ref.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG,"Upload Message Success 1/2: ${ref.key}")
                    refTo.setValue(chatMessage)
                            .addOnSuccessListener {
                                Log.d(TAG,"Upload Message Success 2/2: ${ref.key}")
                                messageChatLog.text.clear()
                            }
                            .addOnFailureListener {
                                Log.d(TAG,"Upload Message Failure 2/2: ${it.message}")
                            }
                }
                .addOnFailureListener {
                    Log.d(TAG,"Upload Message Failure 1/2: ${it.message}")
                }

        val refLatest = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        val refLatestTo = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")

        refLatest.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG,"Upload Latest Message Success 1/2: ${ref.key}")
                    refLatestTo.setValue(chatMessage)
                            .addOnSuccessListener {
                                Log.d(TAG,"Upload Latest Message Success 2/2: ${ref.key}")
                            }
                            .addOnFailureListener {
                                Log.d(TAG,"Upload Latest Message Failure 2/2: ${it.message}")
                            }
                }
                .addOnFailureListener {
                    Log.d(TAG,"Upload Latest Message Failure 1/2: ${it.message}")
                }
    }
}

