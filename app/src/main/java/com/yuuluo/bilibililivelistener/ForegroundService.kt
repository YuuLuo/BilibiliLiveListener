package com.yuuluo.bilibililivelistener

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ForegroundService : Service() {

    companion object {
        const val CHANNEL_ID = "foreground_service_channel"
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(1, notification)
        return START_STICKY
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("BilibiliLiveListener运行中")
            .setContentText("正在保持后台运行")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // 常驻通知
            .build()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
