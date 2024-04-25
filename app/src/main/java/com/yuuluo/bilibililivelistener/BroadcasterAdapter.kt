package com.yuuluo.bilibililivelistener

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yuuluo.bilibililivelistener.databinding.BroadcasterItemBinding


class BroadcasterAdapter(private val onRemove: (BroadcasterInfo) -> Unit) : RecyclerView.Adapter<BroadcasterAdapter.ViewHolder>() {
    private var broadcasters = listOf<BroadcasterInfo>()

    fun setBroadcasters(list: List<BroadcasterInfo>) {
        broadcasters = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BroadcasterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val broadcaster = broadcasters[position]
        holder.bind(broadcaster, onRemove) { info, isChecked ->
            info.isSubscribed = isChecked  // 更新订阅状态
        }
    }

    override fun getItemCount(): Int = broadcasters.size

    class ViewHolder(private val binding: BroadcasterItemBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(info: BroadcasterInfo, onRemove: (BroadcasterInfo) -> Unit, onSubscribedChanged: (BroadcasterInfo, Boolean) -> Unit) {
            binding.broadcasterName.text = info.name
            binding.broadcasterRoom.text = "Room ID: ${info.roomId}"
            binding.removeButton.setOnClickListener { onRemove(info) }
            binding.subscribeSwitch.isChecked = info.isSubscribed
            binding.subscribeSwitch.setOnCheckedChangeListener { _, isChecked ->
                onSubscribedChanged(info, isChecked)
            }

            // 使用 Glide 加载图片
            Glide.with(context)
                .load(info.faceUrl)
                .circleCrop()
                .into(binding.broadcasterImage)
        }
    }
}


