package com.example.xianhang.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.xianhang.R
import com.example.xianhang.databinding.NoticeListItemBinding
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.model.Notice

const val CONTENT = "content"
const val TITLE = "title"

class NoticeAdapter: ListAdapter<Notice, NoticeAdapter.NoticeViewHolder>(DiffCallback) {

    class NoticeViewHolder(private var binding: NoticeListItemBinding): RecyclerView.ViewHolder(binding.root) {
        val view = binding.view

        fun bind(notice: Notice) {
            "#${notice.reportId}".also { binding.id.text = it }
            "举报${notice.reporting}结果".also { binding.content.text = it }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        return NoticeViewHolder(
            NoticeListItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val notice = getItem(position)
        val bundle = bundleOf(ID to notice.reportId, CONTENT to notice.content, TITLE to "举报${notice.reporting}结果")
        holder.view.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_noticesFragment_to_noticeFragment, bundle)
        )
        holder.bind(notice)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Notice>() {
        override fun areItemsTheSame(oldItem: Notice, newItem: Notice): Boolean {
            return oldItem.reportId == newItem.reportId
        }

        override fun areContentsTheSame(oldItem: Notice, newItem: Notice): Boolean {
            return oldItem.reportId == newItem.reportId
        }
    }
}