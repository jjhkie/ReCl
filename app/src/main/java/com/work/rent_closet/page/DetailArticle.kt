package com.work.rent_closet.page

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.work.rent_closet.databinding.ActivityDetailarticleBinding

class DetailArticle: AppCompatActivity() {

    private lateinit var binding:ActivityDetailarticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailarticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        binding.detailTitle.text = title
        val price = intent.getStringExtra("price")
        binding.detailPrice.text = price
        val sellerId = intent.getStringExtra("sellerId")
        binding.detailSeller.text = sellerId
        val imageUri = intent.getStringExtra("image")
        if (imageUri != null) {
            if(imageUri.isNotEmpty()){
                Glide.with(binding.detailUri)
                    .load(imageUri)
                    .into(binding.detailUri)
            }
        }

    }
}