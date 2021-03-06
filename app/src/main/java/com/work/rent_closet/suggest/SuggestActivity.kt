package com.work.rent_closet.suggest

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private var suggestUri: Uri? = null
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

        binding.suggestCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    println("주제")
                }

                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    when (position) {
                        0 -> binding.suggestPrice.visibility = View.GONE
                        1 -> binding.suggestPrice.visibility = View.VISIBLE
                        2 -> binding.suggestPrice.visibility = View.GONE

                    }
                }
            }
        val offerId = intent.getStringExtra("offer_id")
        val key = intent.getStringExtra("key")
        val suggestId = auth.currentUser!!.uid

        //이미지 등록
        binding.photoImageView.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
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

        //제안 등록
        binding.completionBt.setOnClickListener {
            val title = binding.suggestTitle.text.toString()
            val price = binding.suggestPrice.text.toString()
            val category = binding.suggestCategory.selectedItem.toString()
            val content = binding.suggestContent.text.toString()

            Log.d("key",key.toString())
            if (suggestUri != null) {

                val photoUri = suggestUri
                uploadPhoto(
                    photoUri!!,
                    successHandler = { uri ->
                        uploadSuggest(
                            title,
                            price,
                            content,
                            category,
                            offerId.toString(),
                            suggestId,
                            uri,
                            key.toString()
                        )
                    },
                    errorHandler = {
                        Toast.makeText(
                            applicationContext,
                            "사진 업로드에 실패했습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            } else {
                uploadSuggest(
                    title,
                    price,
                    content,
                    category,
                    offerId.toString(),
                    suggestId,
                    "",
                    key.toString()
                )


            }


        }

        binding.closeBt.setOnClickListener {
            finish()
        }


    }

    private fun uploadSuggest(
        title: String,
        price: String,
        content: String,
        category: String,
        offerId: String,
        suggestId: String,
        uri: String,
        key: String
    ) {
        val model = SuggestModel(
            title,
            "$price 원",
            System.currentTimeMillis(),
            content,
            category,
            offerId,
            suggestId,
            uri,
            key
        )
        articleDB.child(key).child(DB_Suggest).push().setValue(model)
        finish()
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
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_LONG).show()

                }
        }
    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2000)
    }

    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = "${System.currentTimeMillis()}.png"
        storage.reference.child("Suggest/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener {
                //업로드가 된 것
                if (it.isSuccessful) {
                    storage.reference.child("Suggest/photo").child(fileName)
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
                    binding.photoImageView.setImageURI(uri)
                    suggestUri = uri
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .create()
            .show()
    }
}