package com.work.rent_closet.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.work.rent_closet.R
import com.work.rent_closet.databinding.FragmentHomeBinding

class HomeFragment: Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var articleAdapter: ArticleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding = fragmentHomeBinding

        //adapter 초기화
        articleAdapter = ArticleAdapter()
        //adapter 체크
        articleAdapter.submitList(mutableListOf<ArticleModel>().apply{
            add(ArticleModel("0","abcd",1000000,"5000원",""))
            add(ArticleModel("0","abddd",2000000,"2000원",""))
        })

        //fragment 는 context가 아니므로 getcontext로 가져와야 한다.
        //그런데 kotlin 에서는 get 을 생략할 수 있으므로 context로 작성
        binding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.articleRecyclerView.adapter = articleAdapter
    }
}