package com.work.rent_closet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.work.rent_closet.chat.ChatFragment
import com.work.rent_closet.databinding.ActivityMainBinding
import com.work.rent_closet.home.HomeFragment


import com.work.rent_clothes.mypage.MyPageFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
                R.id.chatList -> replaceFragment(chatFragment)
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