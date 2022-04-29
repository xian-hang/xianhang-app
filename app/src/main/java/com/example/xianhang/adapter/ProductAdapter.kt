package com.example.xianhang.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.xianhang.R
import com.example.xianhang.model.Product
import com.example.xianhang.network.Api
import com.example.xianhang.product.ProductViewModel
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class ProductAdapter(
    private val context: Context?,
    private val products: List<Product>
): RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View?): RecyclerView.ViewHolder(view!!) {
        val image: ImageView? = view!!.findViewById(R.id.product_image)
        val name: TextView? = view!!.findViewById(R.id.product_name)
        val details: TextView? = view!!.findViewById(R.id.product_details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_list_item, parent, false)
        return ProductViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.image?.setImageResource(R.drawable.ic_launcher_foreground)
        holder.name?.text = product.name
        "price ${product.price} | stock ${product.stock}".also { holder.details?.text = it }
    }

    override fun getItemCount(): Int = products.size
}