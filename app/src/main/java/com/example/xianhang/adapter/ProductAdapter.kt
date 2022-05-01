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
import com.example.xianhang.model.ProductItem
import com.example.xianhang.product.ProductViewModel.Companion.IMAGE_URL

class ProductAdapter: ListAdapter<ProductItem, ProductAdapter.ProductItemViewHolder>(DiffCallback) {

    class ProductItemViewHolder(private var binding: ProductListItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        val view = binding.view

        fun bind(productItem: ProductItem) {
            val product = productItem.product

            binding.productName.text = product.name
            "price $${product.price} | stock ${product.stock}".also { binding.productDetails.text = it }

            var imageUrl: String = ""
            if (productItem.imagesId.isNotEmpty()) {
                imageUrl = "${IMAGE_URL}${productItem.imagesId[0]}"
            }
            val imgUrl = imageUrl.toUri().buildUpon().scheme("https").build()
            binding.productImage.load(imgUrl) {
                placeholder(R.mipmap.ic_loading)
                error(R.drawable.ic_broken_image)
            }

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductItemViewHolder {
        return ProductItemViewHolder(
            ProductListItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: ProductItemViewHolder, position: Int) {
        val product = getItem(position)
        val bundle = bundleOf("product" to product)
        holder.view.setOnClickListener (
            Navigation.createNavigateOnClickListener(R.id.action_productFragment2_to_viewProductFragment, bundle)
        )
        holder.bind(product)
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
