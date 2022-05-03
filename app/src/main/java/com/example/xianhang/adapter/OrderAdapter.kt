package com.example.xianhang.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.xianhang.R
import com.example.xianhang.databinding.OrderItemBinding
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.model.Order
import com.example.xianhang.model.OrderItem
import com.example.xianhang.product.ProductViewModel

class OrderAdapter(private val method: Int, private val context: Context?):
    ListAdapter<OrderItem, OrderAdapter.OrderItemViewHolder>(DiffCallback) {

    class OrderItemViewHolder(private var binding: OrderItemBinding, method: Int):
        RecyclerView.ViewHolder(binding.root) {
        val view = binding.view

        // TODO: implement
        fun bind(order: OrderItem, method: Int) {
            println("OrderAdapter start binding")
            val product = order.order.product

            binding.name.text = product!!.name
            setDetails(order.order, method)

            var imageUrl: String = ""
            if (order.imageId != null) {
                imageUrl = "${ProductViewModel.IMAGE_URL}${order.imageId}"
            }
            val imgUrl = imageUrl.toUri().buildUpon().scheme("https").build()
            binding.image.load(imgUrl) {
                placeholder(R.mipmap.ic_loading)
                error(R.drawable.ic_broken_image)
            }

            binding.executePendingBindings()
        }

        private fun setDetails(order: Order, method: Int) {
            "$${order.price} | ${order.amount}".also { binding.details.text = it }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        return OrderAdapter.OrderItemViewHolder(
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
        val bundle = bundleOf(ID to order.order.id)
        println("order id = ${order.order.id}")
        holder.view.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_ordersFragment_to_orderFragment3, bundle)
        )
    }

    companion object DiffCallback : DiffUtil.ItemCallback<OrderItem>() {
        override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem.order.id == newItem.order.id
        }

        override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem.order.id == newItem.order.id
        }
    }
}