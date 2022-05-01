package com.example.xianhang.adapter

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
import com.example.xianhang.databinding.ProductListItemBinding
import com.example.xianhang.model.Product

class HomeAdapter: ListAdapter<Product, HomeAdapter.ProductViewHolder>(DiffCallback) {

    class ProductViewHolder(private var binding: ProductListItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        val view = binding.view

        fun bind(product: Product, imageUrl: String) {
            binding.productName.text = product.name
            "price $${product.price} | stock ${product.stock}".also { binding.productDetails.text = it }
            //binding.productImage.setImageResource(R.drawable.ic_broken_image)

            val imgUrl = imageUrl.toUri().buildUpon().scheme("https").build()
            binding.productImage.load(imgUrl) {
                placeholder(R.mipmap.ic_loading)
                error(R.drawable.ic_broken_image)
            }

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

    // TODO: bind product first image
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        val bundle = bundleOf(PRODUCT to product)
        holder.view.setOnClickListener (
            Navigation.createNavigateOnClickListener(R.id.action_productFragment2_to_viewProductFragment, bundle)
        )
        holder.bind(product, "")
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
