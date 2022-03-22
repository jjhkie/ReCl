package com.work.rent_closet.page


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.work.rent_closet.DBKey.Companion.DB_ARTICLES
import com.work.rent_closet.DBKey.Companion.DB_CHAT
import com.work.rent_closet.DBKey.Companion.DB_Suggest
import com.work.rent_closet.DBKey.Companion.DB_USER
import com.work.rent_closet.chat.ChatListItem
import com.work.rent_closet.databinding.ActivityDetailarticleBinding
import com.work.rent_closet.suggest.SuggestActivity
import com.work.rent_closet.suggest.SuggestAdapter
import com.work.rent_closet.suggest.SuggestModel

class DetailArticle : AppCompatActivity() {

    private lateinit var binding: ActivityDetailarticleBinding
    private lateinit var suggestAdapter: SuggestAdapter
    private lateinit var articleDB: DatabaseReference
    private lateinit var userDB: DatabaseReference
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


        //해당 게시물의 정보를 찾기 위한 값
        val key = intent.getStringExtra("key")
        val writer = intent.getStringExtra("writer_Id")
        articleDB =
            Firebase.database.reference.child(DB_ARTICLES).child("$key")

        articleDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.detailTitle.text =
                    snapshot.child("title").getValue(String::class.java).toString()
                binding.detailCategory.text =
                    snapshot.child("category").getValue(String::class.java).toString()
                binding.detailContent.text =
                    snapshot.child("content").getValue(String::class.java).toString()
                binding.detailWriterName.text =
                    snapshot.child("writer_Name").getValue(String::class.java).toString()
                binding.detailHeight.text =
                    snapshot.child("height").getValue(String::class.java).toString()
                binding.detailWeight.text =
                    snapshot.child("weight").getValue(String::class.java).toString()
                val thumbnailImage =
                    snapshot.child("imageUrl").getValue(String::class.java).toString()
                val writerprofileimage =
                    snapshot.child("profile_uri").getValue(String::class.java).toString()
                if(thumbnailImage.isNotEmpty()){
                    Glide.with(binding.detailUri)
                        .load(thumbnailImage)
                        .into(binding.detailUri)
                }
                if(writerprofileimage.isNotEmpty()){
                    Glide.with(binding.writerProfileImage)
                        .load(writerprofileimage)
                        .into(binding.writerProfileImage)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        if(writer == auth.currentUser?.uid){
            binding.detailBt.text = "수정하기"
        }else{
            binding.detailBt.text="제안하기"
            binding.detailBt.setOnClickListener {

                val intent = Intent(this,SuggestActivity::class.java)
                intent.putExtra("key",key)
                intent.putExtra("offer_id",writer)
                startActivity(intent)
            }
        }

        binding.closeBt.setOnClickListener {
            finish()
        }

        articleDB =
            Firebase.database.reference.child(DB_ARTICLES).child(key.toString())
                .child(DB_Suggest)

        userDB = Firebase.database.reference.child(DB_USER)
        Log.d("databadddddddddddddse", articleDB.toString())

        //제안한 목록을 클릭했을 때
        suggestAdapter = SuggestAdapter(onItemClicked = { suggestModel ->
            val chatRoom = ChatListItem(
                buyerId = auth.currentUser!!.uid,
                sellerId = suggestModel.suggestId,
                itemTitle = suggestModel.title,
                itemNo = suggestModel.key,
                key = System.currentTimeMillis()
            )
            Log.d("databadddddddddddddse", "이거야 $chatRoom")
            Log.d("databadddddddddddddse", "이거야 $chatRoom")
            Log.d("databadddddddddddddse", "이거야 $suggestModel")
            userDB.child(auth.currentUser!!.uid)
                .child(DB_CHAT)
                .child(suggestModel.key)
                .setValue(chatRoom)

            userDB.child(suggestModel.suggestId)
                .child(DB_CHAT)
                .child(suggestModel.key)
                .setValue(chatRoom)
            
            Toast.makeText(this, "채팅방이 생성되었씁니ㅏㄷ..", Toast.LENGTH_LONG).show()

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