package com.example.xianhang.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.xianhang.R
import com.example.xianhang.BuyProductActivity
import com.example.xianhang.databinding.ProductShowItemBinding
import com.example.xianhang.model.ProductItem
import com.example.xianhang.product.ProductViewModel.Companion.IMAGE_URL

class HomeAdapter(private val context: Context?): ListAdapter<ProductItem, HomeAdapter.ProductViewHolder>(DiffCallback) {

    class ProductViewHolder(private var binding: ProductShowItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        val view = binding.view

        fun bind(productItem: ProductItem) {
            println("HomeAdapter start binding")
            val product = productItem.product

            binding.productName.text = product.name
            "price $${product.price} | ${product.username}".also { binding.productDetails.text = it }

            var imageUrl = ""
            if (productItem.imagesId.isNotEmpty()) {
                imageUrl = "$IMAGE_URL${productItem.imagesId[0]}"
            }
            val imgUrl = imageUrl.toUri().buildUpon().scheme("https").build()
            binding.productImage.load(imgUrl) {
                placeholder(R.mipmap.ic_loading)
                error(R.drawable.ic_broken_image)
            }

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        println("create view holder")
        return ProductViewHolder(
            ProductShowItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val productItem = getItem(position)
        println("position = $position")
        val bundle = bundleOf(PRODUCT_ITEM to productItem)
        holder.view.setOnClickListener{
            val intent = Intent(context, BuyProductActivity::class.java)
            intent.putExtra(PRODUCT_ITEM, productItem)
            context?.startActivity(intent)
        }
        holder.bind(productItem)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ProductItem>() {
        override fun areItemsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean {
            return oldItem.product.id == newItem.product.id
        }
    }
}
