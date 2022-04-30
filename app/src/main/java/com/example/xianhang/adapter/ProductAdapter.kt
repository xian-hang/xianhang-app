package com.example.xianhang.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.leanback.widget.DiffCallback
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.xianhang.R
import com.example.xianhang.databinding.ProductListItemBinding
import com.example.xianhang.model.Product

class ProductAdapter: ListAdapter<Product, ProductAdapter.ProductViewHolder>(DiffCallback) {

    private lateinit var listener: OnItemClickListener

    class ProductViewHolder(private var binding: ProductListItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        val view = binding.view

        fun bind(product: Product) {
            binding.productName.text = product.name
            "price ${product.price} | stock ${product.stock}".also { binding.productDetails.text = it }
            binding.productImage.setImageResource(R.mipmap.ic_image_placeholder)
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ProductListItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        val bundle = bundleOf("id" to product.id)
        holder.view.setOnClickListener (
            Navigation.createNavigateOnClickListener(R.id.action_productFragment2_to_viewProductFragment, bundle)
        )
        holder.bind(product)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}
