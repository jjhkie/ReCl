package com.work.rent_closet.suggest

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.work.rent_closet.DBKey
import com.work.rent_closet.DBKey.Companion.DB_Suggest
import com.work.rent_closet.databinding.ActivitySuggestarticleBinding

class SuggestActivity : AppCompatActivity() {
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }
    private lateinit var binding: ActivitySuggestarticleBinding

    private val articleDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DBKey.DB_ARTICLES)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuggestarticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Toast.makeText(this,"여기까지는 들어오나?",Toast.LENGTH_LONG).show()
        val seller = intent.getStringExtra("sellerId")
        val suggestId = auth.currentUser!!.uid
        Log.d("databadddddddddddddse", "마지막 테스트 $seller")
        binding.completionBt.setOnClickListener {
            val title = binding.suggestTitle.text.toString()
            val price = binding.suggestPrice.text.toString()
            val content = binding.suggestContent.text.toString()
            val key = intent.getStringExtra("key")

            uploadSuggest(title, price, content, seller.toString(), suggestId, key.toString())
        }

        binding.closeBt.setOnClickListener {
            finish()
        }


    }

    private fun uploadSuggest(
        title: String,
        price: String,
        content: String,
        seller: String,
        suggestId: String,
        key: String
    ) {
        val model = SuggestModel(
            title,
            "$price 원",
            0,
            content,
            "",
            seller,
            suggestId,
            key
        )
        articleDB.child(key).child(DB_Suggest).push().setValue(model)
        finish()
    }
}