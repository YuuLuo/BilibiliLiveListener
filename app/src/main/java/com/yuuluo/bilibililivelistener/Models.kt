package com.yuuluo.bilibililivelistener

data class BilibiliResponse(
    val code: Int,
    val msg: String,
    val message: String,
    val data: Map<String, LiveDataInfo>
)

data class LiveDataInfo(
    val title: String,
    val room_id: Int,
    val uid: Int,
    val live_status: Int,
    val uname: String,
    val face: String,
    val cover_from_user: String
)