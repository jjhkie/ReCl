package com.work.rent_closet.mypage


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.work.rent_closet.DBKey
import com.work.rent_closet.R
import com.work.rent_closet.databinding.FragmentSignupBinding

class SignupFragment : Fragment(R.layout.fragment_signup) {
    private lateinit var binding: FragmentSignupBinding

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val userDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DBKey.DB_USER)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentSignupBinding = FragmentSignupBinding.bind(view)
        binding = fragmentSignupBinding



        binding.cancelBt.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.remove(this)
                ?.commit()
        }

        binding.signUpBt.setOnClickListener {
            binding?.let { binding ->
                val name = binding.signUpName.text.toString()
                val email = binding.signUpEmail.text.toString()
                val password = binding.signUpPassword.text.toString()
                val height = binding.signUpHeight.text.toString()
                val weight = binding.signUpWeight.text.toString()

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            val uid = auth.uid.toString()
                            Toast.makeText(context, "회원가입에 성공했습니다.", Toast.LENGTH_LONG).show()
                            signUpDB(email,uid, name, password, height, weight)
                            activity?.supportFragmentManager
                                ?.beginTransaction()
                                ?.remove(this)
                                ?.commit()
                        } else {
                            Toast.makeText(
                                context,
                                "회원가입에 실패했습니다. 이미 가입한 이메일입니다..",
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    }


            }
        }

    }

    private fun signUpDB(
        uemail: String,
        uid: String,
        uname: String,
        upassword: String,
        uheight: String,
        uweight: String
    ) {
        val model = UserModel(uemail, uid,uname, upassword, "$uheight cm", "$uweight kg")
        userDB.child(uid).setValue(model)

    }

}