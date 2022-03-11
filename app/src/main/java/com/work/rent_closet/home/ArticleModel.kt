package com.work.rent_closet.home

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//파이어베이스 리얼타임데이터베이스에 모델을 고대로 사용하려면 빈 생성자가 꼭 필요하다
@Parcelize
data class ArticleModel(
    val sellerId: String,
    val sellerName: String,
    val title: String,
    val content: String,
    val createdAt: Long,
    val price: String,
    val imageUrl: String,
    val height: String,
    val weight: String,
    val key : String
): Parcelable
{
    constructor(): this("","","","",0,"","","","","")
}

