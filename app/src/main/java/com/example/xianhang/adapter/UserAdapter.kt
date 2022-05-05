package com.example.xianhang.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.xianhang.UserActivity
import com.example.xianhang.databinding.UserListItemBinding
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.model.User
import com.example.xianhang.network.response.UserBody

class UserAdapter(private val context: Context?): ListAdapter<UserBody, UserAdapter.UserViewHolder>(DiffCallback) {

    class UserViewHolder(private var binding: UserListItemBinding): RecyclerView.ViewHolder(binding.root) {
        val view = binding.view

        fun bind(user: UserBody) {
            binding.name.text = user.username
            binding.userId.text = user.studentId
            "信誉分 ${user.credit}% | 售出物品 ${user.soldItem}".also { binding.details.text = it }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            UserListItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.view.setOnClickListener {
            val intent = Intent(context, UserActivity::class.java)
            intent.putExtra(ID, user.id)
            context?.startActivity(intent)
        }
        holder.bind(user)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<UserBody>() {
        override fun areItemsTheSame(oldItem: UserBody, newItem: UserBody): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserBody, newItem: UserBody): Boolean {
            return oldItem.id == newItem.id
        }
    }
}