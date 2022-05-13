package com.example.xianhang.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.xianhang.R
import com.example.xianhang.order.ViewOrderActivity
import com.example.xianhang.databinding.OrderItemBinding
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.model.*
import com.example.xianhang.network.SCHEME
import com.example.xianhang.product.ProductViewModel.Companion.IMAGE_URL

class OrderAdapter(private val method: Int, private val status: Int, private val context: Context?):
    ListAdapter<OrderItem, OrderAdapter.OrderItemViewHolder>(DiffCallback) {

    class OrderItemViewHolder(private var binding: OrderItemBinding, method: Int):
        RecyclerView.ViewHolder(binding.root) {
        val view = binding.view

        fun bind(order: OrderItem, method: Int) {
            println("OrderAdapter start binding")
            println(order)
            val product = order.order.product

            binding.name.text = order.order.productName
            setDetails(order.order, method)

            var imageUrl = ""
            if (order.imagesId.isNotEmpty()) {
                imageUrl = "$IMAGE_URL${order.imagesId[0]}"
            }
            val imgUrl = imageUrl.toUri().buildUpon().scheme(SCHEME).build()
            binding.image.load(imgUrl) {
                placeholder(R.mipmap.ic_loading)
                error(R.drawable.ic_broken_image)
            }

            binding.executePendingBindings()
        }

        private fun setDetails(order: Order, method: Int) {
            val status = when (order.status) {
                UNPAID -> "待付款"
                PAID -> "待发货"
                SHIPPED -> "待收货"
                COMPLETE -> "已完成"
                else -> "已取消"
            }
            "$${order.price} | ${order.amount} 个物品 | $status".also { binding.details.text = it }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        return OrderItemViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context)
            ),
            method
        )
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val order = getItem(position)
        setClickListener(method, order, holder)
        holder.bind(order, method)
    }

    private fun setClickListener(method: Int, order: OrderItem, holder: OrderItemViewHolder) {
        println("order id = ${order.order.id}")
        if (method == BUYER) {
            holder.view.setOnClickListener {
                val intent = Intent(context, ViewOrderActivity::class.java)
                intent.putExtra(ID, order.order.id)
                context?.startActivity(intent)
            }
        } else if (method == SELLER) {
            holder.view.setOnClickListener {
                val intent = Intent(context, ViewOrderActivity::class.java)
                intent.putExtra(ID, order.order.id)
                context?.startActivity(intent)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<OrderItem>() {
        override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem.order.id == newItem.order.id
        }

        override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem.imagesId == newItem.imagesId &&
                   oldItem.order.price == newItem.order.price &&
                   oldItem.order.address == newItem.order.address &&
                   oldItem.order.amount == newItem.order.amount &&
                   oldItem.order.name == newItem.order.name &&
                   oldItem.order.phone == newItem.order.phone
        }
    }
}