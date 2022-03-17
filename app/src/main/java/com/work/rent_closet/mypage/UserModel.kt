package com.work.rent_closet.mypage

import com.work.rent_closet.home.ArticleModel

data class UserModel (
    val uemail: String,
    val uid: String,
    val gender: String,
    val image:String,
    val uName: String,
    val upassword: String,
    val uheight: String,
    val uweight: String,


){
    constructor(): this("","","","","","","","",)
}