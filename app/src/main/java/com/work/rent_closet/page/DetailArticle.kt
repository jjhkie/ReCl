package com.work.rent_closet.page

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.work.rent_closet.DBKey.Companion.DB_ARTICLES
import com.work.rent_closet.DBKey.Companion.DB_CHAT
import com.work.rent_closet.DBKey.Companion.DB_Suggest
import com.work.rent_closet.DBKey.Companion.DB_USER
import com.work.rent_closet.chat.ChatListItem
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
    private lateinit var userDB:DatabaseReference
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val key by lazy {
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
        val category = intent.getStringExtra("category")
        val content = intent.getStringExtra("content")
        val imageUri = intent.getStringExtra("image")

        val sellerId = intent.getStringExtra("sellerId")
        Log.d("databadddddddddddddse","이거 꼭 봐 $sellerId")
        binding.detailTitle.text = title
        binding.detailCategory.text = category
        binding.detailSellerName.text = sellerName
        binding.detailCreateAt.text = creatdAt
        binding.detailHeight.text = height
        binding.detailWeight.text = weight
        binding.detailContent.text = content
        Log.d("databadddddddddddddse", key.toString())
        if (auth.currentUser!!.uid == sellerId) {
            binding.detailBt.text = "수정하기"
            binding.detailBt.setOnClickListener {

            }
        } else {
            binding.detailBt.text = "제안하기"
            binding.detailBt.setOnClickListener {
                Toast.makeText(this,"클릭되었습니다.",Toast.LENGTH_LONG).show()
                val intent = Intent(this, SuggestActivity::class.java)
                intent.putExtra("sellerId", sellerId.toString())
                intent.putExtra("key", key)
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


        binding.closeBt.setOnClickListener {
            finish()
        }

        articleDB =
            Firebase.database.reference.child(DB_ARTICLES).child(key.toString()).child(DB_Suggest)

        userDB = Firebase.database.reference.child(DB_USER)
        Log.d("databadddddddddddddse", articleDB.toString())

        //제안한 목록을 클릭했을 때
        suggestAdapter = SuggestAdapter(onItemClicked = { suggestModel ->
            val chatRoom = ChatListItem(
                buyerId =auth.currentUser!!.uid,
                sellerId=suggestModel.suggestId,
                itemTitle = suggestModel.title,
                itemNo = suggestModel.key,
                key = System.currentTimeMillis()
            )
            Log.d("databadddddddddddddse","이거야 $chatRoom")
            Log.d("databadddddddddddddse","이거야 $chatRoom")
            Log.d("databadddddddddddddse","이거야 $suggestModel")
            userDB.child(auth.currentUser!!.uid)
                .child(DB_CHAT)
                .child(suggestModel.key)
                .setValue(chatRoom)

            userDB.child(suggestModel.suggestId)
                .child(DB_CHAT)
                .child(suggestModel.key)
                .setValue(chatRoom)

        Toast.makeText(this,"채팅방이 생성되었씁니ㅏㄷ..",Toast.LENGTH_LONG).show()

        })
        //recycler setting
        binding.detailRecycler.layoutManager = LinearLayoutManager(this)
        binding.detailRecycler.adapter = suggestAdapter

        articleDB.addChildEventListener(listener)

    }

    override fun onResume() {
        super.onResume()
        suggestAdapter.notifyDataSetChanged()
    }

}