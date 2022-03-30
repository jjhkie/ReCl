package com.work.rent_closet.chatroom

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.work.rent_closet.DBKey.Companion.DB_CHAT
import com.work.rent_closet.DBKey.Companion.DB_ROOM
import com.work.rent_closet.databinding.ActivityChatroomBinding

class ChatRoomActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val chatList = mutableListOf<ChatItem>()
    private val adapter = ChatItemAdapter()
    private lateinit var chatRoomDB: DatabaseReference
    private val keyId by lazy {
        intent.getStringExtra("itemId")
    }
    private lateinit var binding: ActivityChatroomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatroomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val chatKey = intent.getLongExtra("chatKey", -1)
        chatRoomDB = Firebase.database.reference
            .child(DB_ROOM).child("$chatKey")

        Log.d("databadddddddddddddse", "room 생성 $keyId")
        chatRoomDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatItem = snapshot.getValue(ChatItem::class.java)
                Log.d("databadddddddddddddse", "room addchildEventListnenr $chatItem")
                chatItem ?: return

                chatList.add(chatItem)
                adapter.submitList(chatList)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        binding.chatRecyclerView.adapter = adapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.sendButton.setOnClickListener {
            val chatItem = ChatItem(
                senderId = auth.currentUser!!.uid,
                message = binding.messageEditText.text.toString()
            )
            chatRoomDB.push().setValue(chatItem)
        }

        binding.closeBt.setOnClickListener {
            finish()
        }
    }
}