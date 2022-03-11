package com.work.rent_closet.page

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.work.rent_closet.DBKey.Companion.DB_ARTICLES
import com.work.rent_closet.DBKey.Companion.DB_Suggest
import com.work.rent_closet.databinding.ActivityDetailarticleBinding
import com.work.rent_closet.home.ArticleAdapter
import com.work.rent_closet.home.ArticleModel
import com.work.rent_closet.suggest.SuggestActivity
import com.work.rent_closet.suggest.SuggestAdapter
import com.work.rent_closet.suggest.SuggestModel

class DetailArticle : AppCompatActivity() {

    private lateinit var binding: ActivityDetailarticleBinding
    private lateinit var suggestAdapter: SuggestAdapter
    private lateinit var articleDB: DatabaseReference
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val key by lazy{
        intent.getStringExtra("key")
    }
    private val suggestList = mutableListOf<SuggestModel>()
    private val listener = object : ChildEventListener {
        //데이터가 추가 되었을 때
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val suggestModel = snapshot.getValue(SuggestModel::class.java)
            suggestModel ?: return

            suggestList.add(suggestModel)
            suggestAdapter.submitList(suggestList)

        }

        //데이터가 변화되었을 때
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        //데이터가 제거되었을 때
        override fun onChildRemoved(snapshot: DataSnapshot) {}

        //데이터가 db 리스트 위치가 변경되었을 때
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        //db처리 중에 오류가 발생했을 때
        override fun onCancelled(error: DatabaseError) {}

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailarticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sellerName = intent.getStringExtra("sellerName")
        val creatdAt = intent.getStringExtra("creatdAt")
        val height = intent.getStringExtra("height")
        val weight = intent.getStringExtra("weight")
        val title = intent.getStringExtra("title")
        val price = intent.getStringExtra("price")
        val content = intent.getStringExtra("content")
        val imageUri = intent.getStringExtra("image")

        val sellerId = intent.getStringExtra("sellerId")

        binding.detailTitle.text = title
        binding.detailPrice.text = price
        binding.detailSellerName.text = sellerName
        binding.detailCreateAt.text = creatdAt
        binding.detailHeight.text = height
        binding.detailWeight.text = weight
        binding.detailContent.text = content

        if (auth.currentUser!!.uid == sellerId) {
            binding.detailBt.text = "수정하기"
            binding.detailBt.setOnClickListener {

            }
        } else {
            binding.detailBt.text = "제안하기"
            binding.detailBt.setOnClickListener {
                val intent = Intent(this, SuggestActivity::class.java)
                intent.putExtra("sellerId", sellerId)
                intent.putExtra("key",key)
                startActivity(intent)
            }
        }

        if (imageUri != null) {
            if (imageUri.isNotEmpty()) {
                Glide.with(binding.detailUri)
                    .load(imageUri)
                    .into(binding.detailUri)
            }
        }

        articleDB =Firebase.database.reference.child(DB_ARTICLES).child(key.toString()).child(DB_Suggest)
        suggestAdapter = SuggestAdapter()
        //recycler setting
        binding.detailRecycler.layoutManager = GridLayoutManager(this, 2)
        binding.detailRecycler.adapter = suggestAdapter

        articleDB.addChildEventListener(listener)

    }
}