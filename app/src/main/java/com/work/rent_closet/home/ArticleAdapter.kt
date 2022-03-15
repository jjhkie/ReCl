package com.work.rent_closet.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.work.rent_closet.databinding.ItemArticleBinding


import java.text.SimpleDateFormat
import java.util.*



class ArticleAdapter (val onItemClicked: (ArticleModel) -> Unit): ListAdapter<ArticleModel, ArticleAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //articleModel 과 연결
        fun bind(articleModel: ArticleModel) {
            //createdAt 는 Long 타입으로 선언했으므로 date 타입으로 바꿔줄 필요가 있다.
            val format = SimpleDateFormat("MM월 dd일")
            val date = Date(articleModel.createdAt)

            binding.titleTextView.text = articleModel.title
            binding.dateTextView.text = format.format(date).toString()
            binding.priceTextView.text = articleModel.price
            //binding.contentTextView.text = articleModel.content

            //예외처리로 imageUrl이 비어있지않다면 실행
            if (articleModel.imageUrl.isNotEmpty()) {
                //Glide를 사용하여 추가(의존성 추가)
                Glide.with(binding.thumbnailImageView)
                    .load(articleModel.imageUrl)
                    .into(binding.thumbnailImageView)
            }

            //root은 하나의 리스트 전체를 의미하며 클릭하면
            //onItemClicked가 실행되고 위에 fun bind(articleModel: ArticleModel)을 인자로 넘겨준다.
            //그러면 ArticleAdapter를 처음 초기화해준 부분에서 람다가 실행된다.
            binding.root.setOnClickListener {
                onItemClicked(articleModel)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        //inflate를 시켜주는데 context를 가져와야하는데 Parent.context로 가져오고 parent에 붙여주고
        return ViewHolder(
            ItemArticleBinding.inflate(
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
        val diffUtil = object : DiffUtil.ItemCallback<ArticleModel>() {
            //현재 리스트에 노출하고 있는 아이템과 새로운 아이템이 같은지 비교
            //즉, 새로운 값이 들어왔을 때 실행
            override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {

                return oldItem.createdAt == newItem.createdAt
            }

            //현재 노출하고 있는 아이템과 새로운 아이템이 같은지 비교하는
            override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}
