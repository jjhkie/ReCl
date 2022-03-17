package com.work.rent_clothes.mypage

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.work.rent_closet.DBKey.Companion.DB_USER
import com.work.rent_closet.R
import com.work.rent_closet.databinding.FragmentMypageBinding
import com.work.rent_closet.mypage.SignupFragment

class MyPageFragment : Fragment(R.layout.fragment_mypage) {

    private lateinit var binding: FragmentMypageBinding


    private val signupFragment by lazy {
        SignupFragment()
    }
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private var userDB:DatabaseReference = Firebase.database.reference.child(DB_USER)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentMypageBinding = FragmentMypageBinding.bind(view)
        binding = fragmentMypageBinding


        //로그인 로그아웃 기능
        binding.signInOutButton.setOnClickListener {
            binding?.let { binding ->
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                if (auth.currentUser == null) {
                    //로그인 하기
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                successSignIn()

                            } else {
                                Toast.makeText(
                                    context,
                                    "로그인에 실패했습니다. 이메일이나 비밀번호를 확인해주세요.",
                                    Toast.LENGTH_LONG
                                ).show()

                            }
                        }
                } else {
                    auth.signOut()

                    binding.signInFallLayout.visibility = View.VISIBLE
                    binding.signInScLayout.visibility = View.GONE
                    binding.loginSubText.text = "회원 서비스를 위해 로그인 하세요"
                    binding.loginSubText.visibility = View.VISIBLE
                    binding.emailEditText.text.clear()
                    binding.emailEditText.isEnabled = true
                    binding.passwordEditText.text.clear()
                    binding.passwordEditText.isEnabled = true

                    binding.signInOutButton.text = "로그인"
                    binding.signInOutButton.isEnabled = false
                    binding.signUpButton.isEnabled = true


                }
            }
        }

        //회원가입
        binding.signUpButton.setOnClickListener {
            parentFragmentManager.beginTransaction().add(R.id.main_fragment,   signupFragment)
                .addToBackStack(null).commit();

        }

        binding.emailEditText.addTextChangedListener {
            binding?.let { binding ->
                val enable =
                    binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
                binding.signInOutButton.isEnabled = enable
            }

        }

        binding.passwordEditText.addTextChangedListener {
            binding?.let { binding ->
                val enable =
                    binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
                binding.signInOutButton.isEnabled = enable
            }
        }
    }

    override fun onStart() {
        super.onStart()

        //로그인이 안되어 있는 경우
        if (auth.currentUser == null) {
            binding.let { binding ->
                binding.loginSubText.visibility = View.VISIBLE
                binding.loginSubText.text = "회원 서비스를 위해 로그인 하세요"
                binding.signInFallLayout.visibility = View.VISIBLE
                binding.signInScLayout.visibility = View.GONE
                binding.emailEditText.text.clear()
                binding.emailEditText.isEnabled = true
                binding.passwordEditText.text.clear()
                binding.passwordEditText.isEnabled = true

                binding.signInOutButton.text = "로그인"
                binding.signInOutButton.isEnabled = false

            }
            //로그인이 되어 있는 경우
        } else binding.let { binding ->
            login_su_skill()
            binding.signInFallLayout.visibility = View.GONE
            binding.signInScLayout.visibility = View.VISIBLE

            binding.signInOutButton.text = "로그아웃"
            binding.signInOutButton.isEnabled = true
        }
    }

    private fun successSignIn() {
        if (auth.currentUser == null) {
            Toast.makeText(context, "로그인에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show()
            return
        }

        login_su_skill()

        binding?.signUpButton?.isEnabled = false
        binding?.signInOutButton.text = "로그아웃"
    }

    private fun login_su_skill() {
        userDB.child(auth.currentUser!!.uid).get().addOnSuccessListener {
            val login_su = it.getValue() //로그인한 사람의 정보를 읽어온다.

            val name = it.child("uname").getValue(String::class.java)
            val weight = it.child("uweight").getValue(String::class.java)
            val height = it.child("uheight").getValue(String::class.java)
            val uri = it.child("image").getValue(String::class.java)

            Glide.with(binding.userThumbnail)
                .load(uri)
                .circleCrop()
                .into(binding.userThumbnail)

            binding.loginSubText.visibility = View.GONE
            binding.signInFallLayout.visibility = View.GONE
            binding.signInScLayout.visibility = View.VISIBLE
            binding.signInScName.text = "$name 님 \n 환영합니다!!👋🏻"
            binding.signInScWeight.text = weight
            binding.signInScHeight.text = height

            Log.d("logintest", name.toString())
        }
    }



}