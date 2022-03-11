package com.work.rent_closet.suggest

data class SuggestModel(
    val title: String,
    val price: String,
    val createdAt: Long,
    val content: String,
    val imageUrl:String,
    val seller: String?,
    val suggetId: String,
    val key: String
){
    constructor(): this("","",0,"","","","","")
}