package com.example.xianhang.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.xianhang.R
import com.example.xianhang.chat.CHAT
import com.example.xianhang.chat.ChatActivity
import com.example.xianhang.databinding.ChatListItemBinding
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.login.LoginFragment.Companion.USERNAME
import com.example.xianhang.model.ChatItem
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ChatAdapter(private val context: Context): ListAdapter<ChatItem, ChatAdapter.ChatViewHolder>(DiffCallback) {

    class ChatViewHolder(private var binding: ChatListItemBinding): RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(chat: ChatItem) {
            binding.username.text = chat.username
            binding.message.text = chat.lastMessage!!.message

            if (chat.unread > 0) {
                binding.unread.text = "${chat.unread}"
                binding.unread.visibility = View.VISIBLE
            } else {
                binding.unread.visibility = View.GONE
            }

            val datetime = chat.lastMessage!!.time
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd+kk:mm:ss")
            val datetimeParse = LocalDateTime.parse(datetime, formatter)
            val diff = ChronoUnit.HOURS.between(datetimeParse, LocalDateTime.now())
            if (diff >= 24) {
                binding.time.text = datetimeParse.format(DateTimeFormatter.ofPattern("MM-dd"))
            } else {
                binding.time.text = datetimeParse.format(DateTimeFormatter.ofPattern("kk:mm"))
            }
        }

        fun read(chat: ChatItem) {
            chat.unread = 0
            binding.unread.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            ChatListItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = getItem(position)
        holder.bind(chat)
        val bundle = bundleOf(CHAT to chat.id)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(USERNAME, chat.username)
            intent.putExtra(ID, chat.userId)
            context.startActivity(intent)
            holder.read(chat)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ChatItem>() {
        override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return oldItem.username == newItem.username &&
                   oldItem.message == newItem.message &&
                   oldItem.unread == newItem.unread &&
                   oldItem.lastMessage?.time == newItem.lastMessage?.time
        }
    }
}