package com.example.xianhang.adapter

import android.content.Context
import android.content.Intent
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
import com.example.xianhang.model.ProductItem
import com.example.xianhang.network.SCHEME
import com.example.xianhang.order.OrderActivity
import com.example.xianhang.product.ProductManageActivity
import com.example.xianhang.product.ProductViewModel.Companion.IMAGE_URL

const val PRODUCT = "product"
const val PRODUCT_ITEM = "product_item"
const val ACTION = "action"
const val METHOD = "method"
const val PRICE = "price"
const val TO = "to"
const val QUERY = "query"

const val SELLER = 0
const val BUYER = 1
const val USER_PRODUCT = 2
const val COLLECTION = 3
const val FEEDS = 4
const val SEARCH = 5

class ProductAdapter(private val method: Int, private val context: Context?):
    ListAdapter<ProductItem, ProductAdapter.ProductItemViewHolder>(DiffCallback) {

    class ProductItemViewHolder(private var binding: ProductListItemBinding, method: Int):
        RecyclerView.ViewHolder(binding.root) {
        val view = binding.view

        fun bind(productItem: ProductItem, method: Int) {
            println("ProductAdapter start binding")
            val product = productItem.product

            binding.productName.text = product.name
            setDetails(product, method)

            var imageUrl = ""
            if (productItem.imagesId.isNotEmpty()) {
                imageUrl = "${IMAGE_URL}${productItem.imagesId[0]}"
            }
            val imgUrl = imageUrl.toUri().buildUpon().scheme(SCHEME).build()
            binding.productImage.load(imgUrl) {
                placeholder(R.mipmap.ic_loading)
                error(R.drawable.ic_broken_image)
            }

            binding.executePendingBindings()
        }

        private fun setDetails(product: Product, method: Int) {
            when (method) {
                SELLER -> {
                    "$${product.price} | stock ${product.stock}".also { binding.productDetails.text = it }
                }
                BUYER, COLLECTION, SEARCH, FEEDS -> {
                    "$${product.price} | ${product.username}".also { binding.productDetails.text = it }
                }
                USER_PRODUCT -> {
                    "$${product.price} | stock ${product.stock}".also { binding.productDetails.text = it }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductItemViewHolder {
        return ProductItemViewHolder(
            ProductListItemBinding.inflate(
                LayoutInflater.from(parent.context)
            ),
            method
        )
    }

    override fun onBindViewHolder(holder: ProductItemViewHolder, position: Int) {
        val product = getItem(position)
        setClickListener(method, product, holder)
        holder.bind(product, method)
    }

    private fun setClickListener(method: Int, product: ProductItem, holder: ProductItemViewHolder) {
        when (method) {
            SELLER -> {
                holder.view.setOnClickListener {
                    val intent = Intent(context, ProductManageActivity::class.java)
                    intent.putExtra(PRODUCT_ITEM, product)
                    context?.startActivity(intent)
                }
            }
            BUYER -> {
                val bundle = bundleOf(PRODUCT_ITEM to product)
                holder.view.setOnClickListener(
                    Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_buyProductActivity, bundle)
                )
            }
            // USER_PRODUCT, SEARCH and FEEDs are same
            else -> {
                holder.view.setOnClickListener {
                    val intent = Intent(context, OrderActivity::class.java)
                    intent.putExtra(PRODUCT_ITEM, product)
                    context?.startActivity(intent)
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ProductItem>() {
        override fun areItemsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean {
            return oldItem.imagesId == newItem.imagesId &&
                   oldItem.product.price == newItem.product.price &&
                   oldItem.product.name == newItem.product.name &&
                   oldItem.product.stock == newItem.product.stock &&
                   oldItem.product.username == newItem.product.username &&
                   oldItem.product.address == newItem.product.address &&
                   oldItem.product.description == newItem.product.description &&
                   oldItem.product.tradingMethod == newItem.product.tradingMethod &&
                   oldItem.collectId == newItem.collectId
        }
    }
}
