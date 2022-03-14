package com.work.rent_closet.chat

data class ChatListItem(
    val buyerId: String,
    val sellerId: String,
    val itemTitle: String,
    val itemNo:String,
    val key: Long
) {
    constructor() : this("", "", "","", 0)

}