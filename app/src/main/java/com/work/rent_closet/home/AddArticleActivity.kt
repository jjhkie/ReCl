package com.work.rent_closet.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
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
import com.work.rent_closet.DBKey.Companion.DB_ARTICLES
import com.work.rent_closet.databinding.ActivityAddArticleBinding

//이미지 등록 버튼을 눌렀을 때 권한을 가져와서 권한이 실행되면
//contentprovider를 통해 이미지를 선택하고 선택된 이미지를 uri 로 가져온다.
class AddArticleActivity : AppCompatActivity() {

    private var selectedUri: Uri? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }
    private val articleDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_ARTICLES)
    }
    private lateinit var binding: ActivityAddArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //이미지 등록 버튼을 클릭했을 때
        binding.imageAddButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startContentProvider()
                }//교육용 팝업이 필요한 경우
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

        //글등록 버튼을 클릭했을 때
        binding.submitButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val price = binding.priceEditText.text.toString()
            val sellerId = auth.currentUser?.uid.orEmpty()

            //중간에 이미지가 있으면 업로드 과정을 추가
            if (selectedUri != null) {
                val photoUri =
                    selectedUri ?: return@setOnClickListener//if문으로 null 확인을 해서 굳이... 필요없다..
                uploadPhoto(photoUri,
                    successHandler = {uri ->
                        uploadArticle(sellerId, title,price,uri)
                    },
                    errorHandler = {
                        Toast.makeText(this,"사진 업로드에 실패했습니다.",Toast.LENGTH_LONG).show()
                    }
                )
            }else{
                uploadArticle(sellerId,title,price," ")
            }



        }
    }

    //storage를 사용하여 업로드를 하겠다.
    //파일 이름은 현재 시간을 기준으로 작성을 해준 후
    // storage에 하위 항목(article/photo) 에 생성한 파일명으로 생성된 파일에 uri을 생성한 후
    //업로드가 정상적으로 되었다면 해당 파일로 이동한 후 downloadUrl
    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = "${System.currentTimeMillis()}.png"
        storage.reference.child("article/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener{
                //업로드가 된 것
                if(it.isSuccessful){
                    storage.reference.child("article/photo").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri->
                            successHandler(uri.toString())
                        }.addOnFailureListener {
                            errorHandler()
                        }
                }else{
                    //업로드에 실패
                    errorHandler()
                }
           }
    }

    //article 업로드

    private fun uploadArticle(sellerId: String, title: String, price: String, imageUrl: String){
        val model = ArticleModel(sellerId, title, System.currentTimeMillis(), "$price 원", "")
        articleDB.push().setValue(model)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1000 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startContentProvider()
                } else {
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"//이미지 타입만 가져와라
        startActivityForResult(intent, 2000)
    }

    //데이터를 가져온다.
    //파라미터에 data에 사진의 uri가 넘어왔을 것이다.
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
                    selectedUri = uri
                } else {
                    Toast.makeText(this, "사진ㅇ,ㄹ 가져오지 못했습니다.", Toast.LENGTH_LONG).show()
                }

            }
            else -> {
                Toast.makeText(this, "사진ㅇ,ㄹ 가져오지 못했습니다.", Toast.LENGTH_LONG).show()
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