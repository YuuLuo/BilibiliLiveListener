package com.yuuluo.bilibililivelistener

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface BilibiliApiService {
    @GET("room/v1/Room/get_status_info_by_uids")
    suspend fun getStatusInfoByUids(@Query("uids[]") uids: List<String>): BilibiliResponse

    companion object {
        fun create(): BilibiliApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.live.bilibili.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(BilibiliApiService::class.java)
        }
    }
}