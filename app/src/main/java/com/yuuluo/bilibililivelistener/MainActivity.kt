package com.yuuluo.bilibililivelistener

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuuluo.bilibililivelistener.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: BroadcasterViewModel by viewModels()
    private val adapter = BroadcasterAdapter { broadcaster -> viewModel.removeBroadcaster(broadcaster) }
    private val handler = Handler(Looper.getMainLooper())
    private var isMonitoring = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("live_broadcast_channel_id", "Live Broadcast Notifications", "主播开播通知", NotificationManager.IMPORTANCE_HIGH)
            createNotificationChannel("foreground_service_channel", "App Background Service", "应用常驻后台服务", NotificationManager.IMPORTANCE_HIGH)
            Log.d("Channel","Channel created")
        }


        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setLayoutManager(GridLayoutManager(this, 1))
        binding.recyclerView.adapter = adapter

        viewModel.broadcasters.observe(this) { broadcasters ->
            adapter.setBroadcasters(broadcasters)
        }

        binding.addButton.setOnClickListener {
            val uid = binding.editText.text.toString()
            viewModel.addBroadcaster(uid)
        }

        binding.buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.startMonitoringButton.setOnClickListener {
            if (!isMonitoring) {
                startMonitoring()
                it.isEnabled = false  // 禁用按钮
            }
        }

        binding.startMonitoringButton.setOnClickListener {
            toggleMonitoring()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String, channelDescription: String, importance: Int) {
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun startMonitoring() {
        isMonitoring = true
        val interval = viewModel.queryInterval * 1000  // to seconds
        handler.postDelayed(object : Runnable {
            override fun run() {
                viewModel.checkLiveStatus()
                if (isMonitoring) {
                    handler.postDelayed(this, interval)
                }
            }
        }, interval)
    }

    private fun toggleMonitoring() {
        isMonitoring = !isMonitoring
        binding.startMonitoringButton.text = if (isMonitoring) "停止监听" else "开始监听"
        if (isMonitoring) {
            startMonitoring()
        } else {
            handler.removeCallbacksAndMessages(null)  // 停止监控
        }
    }



}

