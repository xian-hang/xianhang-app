package com.example.xianhang

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.xianhang.adapter.ProductAdapter
import com.example.xianhang.model.ProductItem

@BindingAdapter("listitem")
fun bindUserProducts(recyclerView: RecyclerView, products: List<ProductItem>?) {
    val adapter = recyclerView.adapter as ProductAdapter
    adapter.submitList(products)
}

@BindingAdapter("imageUrl")
fun bindImage(img: ImageView, imgUrl: String?) {
    imgUrl?.let {
        println("imgUrl = $it")
        val imgUrl = imgUrl.toUri().buildUpon().scheme("https").build()
        println("build success")
        img.load(imgUrl) {
            placeholder(R.mipmap.ic_loading)
            error(R.drawable.ic_broken_image)
        }
        println("load finish")
    }
}
