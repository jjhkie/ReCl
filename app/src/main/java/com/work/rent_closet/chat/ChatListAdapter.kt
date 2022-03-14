package com.work.rent_closet.chat


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.work.rent_closet.databinding.ItemChatListBinding




class ChatListAdapter (val onItemClicked: (ChatListItem) -> Unit): ListAdapter<ChatListItem, ChatListAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //articleModel 과 연결
        fun bind(chatListItem: ChatListItem) {

            binding.chatRoomTitleTextView.text = chatListItem.itemTitle

            //root은 하나의 리스트 전체를 의미하며 클릭하면
            //onItemClicked가 실행되고 위에 fun bind(articleModel: ArticleModel)을 인자로 넘겨준다.
            //그러면 ArticleAdapter를 처음 초기화해준 부분에서 람다가 실행된다.
            binding.root.setOnClickListener {
                onItemClicked(chatListItem)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        //inflate를 시켜주는데 context를 가져와야하는데 Parent.context로 가져오고 parent에 붙여주고
        return ViewHolder(
            ItemChatListBinding.inflate(
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
        val diffUtil = object : DiffUtil.ItemCallback<ChatListItem>() {
            //현재 리스트에 노출하고 있는 아이템과 새로운 아이템이 같은지 비교
            //즉, 새로운 값이 들어왔을 때 실행
            override fun areItemsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {

                return oldItem.key == newItem.key
            }

            //현재 노출하고 있는 아이템과 새로운 아이템이 같은지 비교하는
            override fun areContentsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}
