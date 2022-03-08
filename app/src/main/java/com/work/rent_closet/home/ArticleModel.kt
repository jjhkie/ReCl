package com.work.rent_closet.home

//파이어베이스 리얼타임데이터베이스에 모델을 고대로 사용하려면 빈 생성자가 꼭 필요하다
data class ArticleModel(
    val sellerId: String,
    val title: String,
    val createdAt: Long,
    val price: String,
    val imageUrl: String
) {
    constructor(): this("","",0,"","")
}

