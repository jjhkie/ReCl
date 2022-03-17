package com.work.rent_closet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.work.rent_closet.chat.ChatFragment
import com.work.rent_closet.databinding.ActivityMainBinding
import com.work.rent_closet.home.HomeFragment


import com.work.rent_clothes.mypage.MyPageFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val chatFragment = ChatFragment()
        val myPageFragment = MyPageFragment()


        replaceFragment(homeFragment)//기본으로 home 프레그먼트를 설정한다.

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(homeFragment)
                R.id.chatList ->
                    if(auth.currentUser == null){
                        Toast.makeText(this,"로그인을 먼저 해주세요.",Toast.LENGTH_LONG).show()
                    }else{
                        replaceFragment(chatFragment)
                    }

                R.id.myPage -> replaceFragment(myPageFragment)

            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.main_fragment, fragment)
                commit()
            }
    }
}