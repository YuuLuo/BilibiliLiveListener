package com.yuuluo.bilibililivelistener

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BroadcasterViewModel(application: Application) : AndroidViewModel(application) {
    private val _broadcasters = MutableLiveData<List<BroadcasterInfo>>()
    val broadcasters: LiveData<List<BroadcasterInfo>> = _broadcasters
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
    val queryInterval: Long = sharedPreferences.getString("query_interval", "10")?.toLong() ?: 10L

    fun addBroadcaster(uid: String) {
        viewModelScope.launch {
            Log.d("AddBroadcaster", "Starting API call for UID: $uid")
            // 检查是否已存在同一个UID的主播
            val existingBroadcaster = _broadcasters.value?.any { it.uid == uid }
            if (existingBroadcaster == true) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "该主播已存在", Toast.LENGTH_LONG).show()
                }
            } else {
                val apiResponse = BilibiliApiService.create().getStatusInfoByUids(listOf(uid))
                if (apiResponse.code == 0 && apiResponse.data.isNotEmpty()) {
                    val info = apiResponse.data.values.first()
                    Log.d("AddBroadcaster", "API response success for UID: $uid")
                    _broadcasters.value = _broadcasters.value.orEmpty() + BroadcasterInfo(
                        uid = info.uid.toString(),
                        name = info.uname,
                        roomId = info.room_id.toString(),
                        isLive = false,
                        faceUrl = info.face,
                        coverUrl = info.cover_from_user
                    )
                } else {
                    Log.e("AddBroadcaster", "Failed to fetch or no data for UID: $uid, API Response Code: ${apiResponse.code}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(getApplication(), "无法找到UID对应的数据或请求失败", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }



    fun removeBroadcaster(broadcaster: BroadcasterInfo) {
        _broadcasters.value = _broadcasters.value?.filterNot { it.uid == broadcaster.uid }
    }

    fun checkLiveStatus() {
        val subscribedBroadcasters = _broadcasters.value?.filter { it.isSubscribed }
        subscribedBroadcasters?.let { broadcasters ->
            // 获取所有已订阅主播的 UID 列表
            val uids = broadcasters.map { it.uid }

            viewModelScope.launch {
                val response = BilibiliApiService.create().getStatusInfoByUids(uids)
                if (response.code == 0 && response.data.isNotEmpty()) {
                    response.data.forEach { (uid, liveInfo) ->
                        val broadcaster = broadcasters.find { it.uid == uid }
                        broadcaster?.let {
                            if (liveInfo.live_status == 1 && !it.isLive) {
                                it.isLive = true
                                sendNotification(it, getApplication())
                            } else if (liveInfo.live_status != 1 && it.isLive) {
                                it.isLive = false
                            }
                            updateBroadcaster(it)
                        }
                    }
                } else {
                    Log.e("CheckLiveStatus", "API call failed or no data returned")
                }
            }
        }
    }



    private fun sendNotification(broadcaster: BroadcasterInfo, context: Context) {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "live_broadcast_channel_id"

        // 创建一个打开哔哩哔哩客户端的Intent
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("bilibili://live/${broadcaster.roomId}"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        Glide.with(context)
            .asBitmap()
            .load(broadcaster.coverUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val notification = NotificationCompat.Builder(context, channelId).apply {
                        setSmallIcon(R.drawable.ic_notification) // 通知小图标
                        setContentTitle("订阅的主播 ${broadcaster.name} 开播了!")
                        setContentText("点击查看直播")
                        setContentIntent(pendingIntent) // 通知点击事件
                        setAutoCancel(true)
                        setLargeIcon(resource) // 通知大图标
                        setStyle(NotificationCompat.BigPictureStyle().bigPicture(resource))
                        priority = NotificationCompat.PRIORITY_HIGH
                    }.build()

                    // 发送通知
                    notificationManager.notify(broadcaster.uid.hashCode(), notification)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }



    private fun updateBroadcaster(updatedBroadcaster: BroadcasterInfo) {
        _broadcasters.value = _broadcasters.value?.map {
            if (it.uid == updatedBroadcaster.uid) updatedBroadcaster else it
        }
    }

}

data class BroadcasterInfo(
    val uid: String,
    val name: String,
    val roomId: String,
    var isLive: Boolean,
    val faceUrl: String,
    val coverUrl: String,
    var isSubscribed: Boolean = true
)
