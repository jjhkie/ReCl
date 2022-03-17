package com.work.rent_closet.mypage


import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.config.GservicesValue.value
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.work.rent_closet.DBKey
import com.work.rent_closet.R
import com.work.rent_closet.databinding.FragmentSignupBinding

class SignupFragment : Fragment(R.layout.fragment_signup) {
    private lateinit var binding: FragmentSignupBinding
    private var selectedUri: Uri? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }
    private val userDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DBKey.DB_USER)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentSignupBinding = FragmentSignupBinding.bind(view)
        binding = fragmentSignupBinding


        //회원 대표 이미지 설정
        binding.signupImageUpload.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startContentProvider()
                }
                //PERMISSION_DENIED를 반환했을 경우 showShowRequestPermissionRationable()은 true를 반환
                //교육용 팝업이 필요한 경우
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup()
                }


                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1000
                    )
                }
            }
        }

        binding.cancelBt.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.remove(this)
                ?.commit()
        }

        binding.signUpBt.setOnClickListener {
            binding?.let { binding ->
                val name = binding.signUpName.text.toString()
                val gender = binding.genderSpinner.selectedItem.toString()
                val email = binding.signUpEmail.text.toString()
                val password = binding.signUpPassword.text.toString()
                val height = binding.signUpHeight.text.toString()
                val weight = binding.signUpWeight.text.toString()


                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            val uid = auth.uid.toString()
                            if(selectedUri != null){
                                val photoUri = selectedUri
                                uploadPhoto(
                                    photoUri!!,
                                    successHandler = {uri ->
                                        signUpDB(email, uid,gender,uri, name, password, height, weight)
                                    },
                                    errorHandler = {
                                        Toast.makeText(requireContext(),"사진 업로드에 실패했습니다.",Toast.LENGTH_LONG).show()
                                    }
                                )
                            }else{
                                if(gender == "남자") {

                                }else{

                                }
                            }
                            Toast.makeText(context, "회원가입에 성공했습니다.", Toast.LENGTH_LONG).show()
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
        gender:String,
        image:String,
        uname: String,
        upassword: String,
        uheight: String,
        uweight: String
    ) {
        val model = UserModel(uemail, uid,gender,image, uname, upassword, "$uheight cm", "$uweight kg")
        userDB.child(uid).setValue(model)

    }

    //이미지 등록에 필요한 함수들
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1000 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //권한이 허가된 것으로 아래 코드 실행
                    startContentProvider()
                } else {
                    //권한이 거부됐을 때 동작
                    Toast.makeText(requireContext(), "권한을 거부하셨습니다.", Toast.LENGTH_LONG).show()

                }
        }
    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2000)
    }
    // storage에 하위 항목(article/photo) 에 생성한 파일명으로 생성된 파일에 uri을 생성한 후
    //업로드가 정상적으로 되었다면 해당 파일로 이동한 후 downloadUrl
    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = "${System.currentTimeMillis()}.png"
        storage.reference.child("User/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener {
                //업로드가 된 것
                if (it.isSuccessful) {
                    storage.reference.child("User/photo").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            successHandler(uri.toString())
                        }.addOnFailureListener {
                            errorHandler()
                        }
                } else {
                    //업로드에 실패
                    errorHandler()
                }
            }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            2000 -> {
                val uri = data?.data
                if (uri != null) {
                    binding.signupImageUpload.setImageURI(uri)
                    selectedUri = uri
                } else {
                    Toast.makeText(requireContext(), "사진을 가져오지 못했습니다.", Toast.LENGTH_LONG).show()
                }

            }
            else -> {
                Toast.makeText(requireContext(), "사진을 가져오지 못했습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun showPermissionContextPopup() {
        AlertDialog.Builder(requireContext())
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .create()
            .show()
    }
}