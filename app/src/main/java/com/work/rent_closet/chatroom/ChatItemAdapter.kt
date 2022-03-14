package com.work.rent_closet.chatroom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.work.rent_closet.chat.ChatListItem
import com.work.rent_closet.databinding.ItemChatBinding

class ChatItemAdapter(): ListAdapter<ChatItem, ChatItemAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatItem: ChatItem) {

            binding.senderTextView.text = chatItem.senderId
            binding.messageTextView.text = chatItem.message

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        //inflate를 시켜주는데 context를 가져와야하는데 Parent.context로 가져오고 parent에 붙여주고
        return ViewHolder(
            ItemChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatItem>() {
            //현재 리스트에 노출하고 있는 아이템과 새로운 아이템이 같은지 비교
            //즉, 새로운 값이 들어왔을 때 실행
            override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                return oldItem == newItem
            }

            //현재 노출하고 있는 아이템과 새로운 아이템이 같은지 비교하는
            override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                TODO("Not yet implemented")
            }

        }
    }


}
