package com.work.rent_closet.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.work.rent_closet.DBKey.Companion.DB_USER
import com.work.rent_closet.R
import com.work.rent_closet.databinding.FragmentHomeBinding
import com.work.rent_closet.page.DetailArticle

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var articleDB: DatabaseReference
    private lateinit var userDB:DatabaseReference
    private lateinit var binding: FragmentHomeBinding
    private lateinit var articleAdapter: ArticleAdapter


    private val articleList = mutableListOf<ArticleModel>()

    private val listener = object : ChildEventListener {
        //데이터가 추가 되었을 때
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            //model class 자체를 업로드를 하고 자체를  다운받을거다.
            val articleModel = snapshot.getValue(ArticleModel::class.java)
            articleModel ?: return

            articleList.add(articleModel)
            articleAdapter.submitList(articleList)
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

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding = fragmentHomeBinding

        //화면 전환 후 같은 뷰가 또 추가되는 현상을 막기 위해
        articleList.clear()
        //db 설정
        articleDB = Firebase.database.reference.child(DB_ARTICLES)//

        userDB = Firebase.database.reference.child(DB_USER)

        //adapter 초기화
        articleAdapter = ArticleAdapter(onItemClicked = { articleModel ->  
            if(auth.currentUser != null){
                //로그인이 되어있는 상황
                val detail_article= mutableListOf<ArticleModel>(articleModel)
                Log.d("databadddddddddddddse",detail_article.toString())
                val intent = Intent(requireContext(), DetailArticle::class.java)
                intent.putExtra("dfdfd",detail_article)
                intent.putExtra("createdAt",articleModel.createdAt)
                intent.putExtra("price",articleModel.price)
                intent.putExtra("sellerId",articleModel.sellerId)
                intent.putExtra("title",articleModel.title)
                intent.putExtra("image",articleModel.imageUrl)
                startActivity(intent)
                if(auth.currentUser!!.uid != articleModel.sellerId){

                }else{
                    //본인이 올린 게시물

                }
            }else{
                //로그인을 하지않은 상황
                Snackbar.make(view, "로그인 후 사용해주세요", Snackbar.LENGTH_LONG).show()
            }

        //이 부분이 recyclerView를 클릭했을 때 발생하는 코드
        })
        //adapter 체크
//        articleAdapter.submitList(mutableListOf<ArticleModel>().apply{
//            add(ArticleModel("0","abcd",1000000,"5000원",""))
//            add(ArticleModel("0","abddd",2000000,"2000원",""))
//        })


        /****/
        //floating 버튼을 클릭하여 글 작성 페이지 이동
        binding.addFloatingButton.setOnClickListener {
            //TODO 로그인 기능 생성 후 주석 제거 : 회원일 때만 글을 작성할 수 있도록 설정
            if (auth.currentUser != null) {
                val intent = Intent(requireContext(), AddArticleActivity::class.java)
                startActivity(intent)
                //parentFragmentManager.beginTransaction().add(R.id.main_fragment,addArticleFragment).addToBackStack(null).commit();
            }else {
                Snackbar.make(view, "로그인 후 사용해주세요", Snackbar.LENGTH_LONG).show()
            }
        }


        /****/
        //fragment 는 context가 아니므로 getcontext로 가져와야 한다.
        //그런데 kotlin 에서는 get 을 생략할 수 있으므로 context로 작성
        binding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.articleRecyclerView.adapter = articleAdapter

        //data를 가져오는 방법
        //addchildeventListener 한번 등록해놓으면 이벤트가 발생할 때마다 등록이 된다.
        // addListenercallvalueEvent
        //articleDB.addListenerForSingleValueEvent()
        //view create할 때마다 attach해주고, view destroy할 때 remove()
        articleDB.addChildEventListener(listener)

    }

    override fun onResume() {
        super.onResume()
        articleAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        articleDB.removeEventListener(listener)
    }
}


