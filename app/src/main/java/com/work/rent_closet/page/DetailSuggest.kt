package com.work.rent_closet.page

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.work.rent_closet.databinding.ActivityDetailSuggestBinding

class DetailSuggest: AppCompatActivity() {

    private lateinit var binding:ActivityDetailSuggestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailSuggestBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}