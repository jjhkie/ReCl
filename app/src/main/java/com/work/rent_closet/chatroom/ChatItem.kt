package com.work.rent_closet.chatroom

data class ChatItem(
    val senderId: String,
    val message: String

){
    constructor(): this("","")
}
