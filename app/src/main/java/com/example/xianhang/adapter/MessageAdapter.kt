package com.example.xianhang.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.xianhang.databinding.ReceiveMessageItemBinding
import com.example.xianhang.databinding.SentMessageItemBinding
import com.example.xianhang.model.Message

class MessageAdapter(
    private val context: Context?,
    private val userId: Int
): ListAdapter<Message, RecyclerView.ViewHolder>(DiffCallback) {

    class SentMessageViewHolder(private var binding: SentMessageItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.sentMessage.text = message.message
        }
    }

    class ReceiveMessageViewHolder(private var binding: ReceiveMessageItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.receiveMessage.text = message.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SENDER) {
            println("send message")
            SentMessageViewHolder(SentMessageItemBinding.inflate(LayoutInflater.from(parent.context)))
        } else {
            println("receive message")
            ReceiveMessageViewHolder(ReceiveMessageItemBinding.inflate(LayoutInflater.from(parent.context)))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceiveMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.userId == userId) SENDER else RECEIVER
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.userId == newItem.userId &&
                   oldItem.username == newItem.username &&
                   oldItem.message == newItem.message &&
                   oldItem.time == newItem.time
        }

        private const val SENDER = 0
        private const val RECEIVER = 1
    }
}