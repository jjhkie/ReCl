package com.work.rent_closet.chat

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.work.rent_closet.DBKey.Companion.DB_CHAT
import com.work.rent_closet.DBKey.Companion.DB_ROOM
import com.work.rent_closet.DBKey.Companion.DB_USER
import com.work.rent_closet.R
import com.work.rent_closet.chatroom.ChatRoomActivity
import com.work.rent_closet.databinding.FragmentChatBinding


class ChatFragment : Fragment(R.layout.fragment_chat) {

    private val auth: FirebaseAuth by lazy{
        Firebase.auth
    }
    private var itemkey: String? = null
    private val chatRoomList = mutableListOf<ChatListItem>()
    private lateinit var chatDB:DatabaseReference
    private lateinit var binding: FragmentChatBinding
    private lateinit var chatListAdapter: ChatListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentChatListBinding = FragmentChatBinding.bind(view)
        binding = fragmentChatListBinding

        chatListAdapter = ChatListAdapter(onItemClicked = {charListItem->
            //채팅방으로 이동하는 코드
            context?.let{
                itemkey = charListItem.itemNo.toString()
                val intent = Intent(it, ChatRoomActivity::class.java)
                intent.putExtra("itemId",charListItem.itemNo)
                startActivity(intent)
            }
        })

        chatRoomList.clear()

        fragmentChatListBinding.chatListRecyclerView.adapter = chatListAdapter
        fragmentChatListBinding.chatListRecyclerView.layoutManager = LinearLayoutManager(context)

        if(auth.currentUser ==null){
            return
        }
        chatDB = Firebase.database.reference.child(DB_USER)
            .child(auth.currentUser!!.uid)
            .child(DB_CHAT)
            .child(itemkey.toString())


        chatDB.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val model = it.getValue(ChatListItem::class.java)
                    model ?: return

                    chatRoomList.add(model)
                }
                chatListAdapter.submitList(chatRoomList)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun onResume() {
        super.onResume()
        chatListAdapter.notifyDataSetChanged()
    }
}