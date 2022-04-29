package com.example.xianhang.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.DiffCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.xianhang.R
import com.example.xianhang.databinding.ProductListItemBinding
import com.example.xianhang.model.Product

class ProductAdapter: ListAdapter<Product, ProductAdapter.ProductViewHolder>(DiffCallback) {

    class ProductViewHolder(private var binding: ProductListItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.productName.text = product.name
            "price ${product.price} | stock ${product.stock}".also { binding.productDetails.text = it }
            binding.productImage.setImageResource(R.mipmap.ic_image_placeholder)
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ProductListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }
    }
}
