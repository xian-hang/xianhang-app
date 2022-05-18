package com.example.xianhang.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.xianhang.databinding.ChatListItemBinding
import com.example.xianhang.model.ChatItem

class ChatAdapter: ListAdapter<ChatItem, ChatAdapter.ChatViewHolder>(DiffCallback) {

    private lateinit var listener: OnItemClickListener

    class ChatViewHolder(private var binding: ChatListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatItem) {
            binding.username.text = chat.username
            binding.message.text = chat.message
            binding.time.text = chat.lastMessage?.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            ChatListItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = getItem(position)
        holder.bind(chat)

    }

    interface OnItemClickListener {
        fun onItemClick(chat: ChatItem)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ChatItem>() {
        override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return oldItem.username == newItem.username &&
                   oldItem.message == newItem.message
        }
    }
}