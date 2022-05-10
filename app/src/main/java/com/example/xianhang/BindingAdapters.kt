package com.example.xianhang

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.xianhang.adapter.NoticeAdapter
import com.example.xianhang.adapter.OrderAdapter
import com.example.xianhang.adapter.ProductAdapter
import com.example.xianhang.adapter.UserAdapter
import com.example.xianhang.model.Notice
import com.example.xianhang.model.OrderItem
import com.example.xianhang.model.ProductItem
import com.example.xianhang.network.SCHEME
import com.example.xianhang.network.response.UserBody

@BindingAdapter("listitem")
fun bindUserProducts(recyclerView: RecyclerView, products: List<ProductItem>?) {
    val adapter = recyclerView.adapter as ProductAdapter
    println("submit list")
    println("products = $products")
    adapter.submitList(products)
}

@BindingAdapter("listorder")
fun bindOrders(recyclerView: RecyclerView, orders: List<OrderItem>?) {
    val adapter = recyclerView.adapter as OrderAdapter
    println("submit list")
    println("orders = $orders")
    adapter.submitList(orders)
}

@BindingAdapter("listuser")
fun bindUsers(recyclerView: RecyclerView, users: List<UserBody>?) {
    val adapter = recyclerView.adapter as UserAdapter
    println("submit list")
    println("users = $users")
    adapter.submitList(users)
}

@BindingAdapter("listnotice")
fun bindNotices(recyclerView: RecyclerView, notices: List<Notice>?) {
    val adapter = recyclerView.adapter as NoticeAdapter
    println("submit list")
    println("notices = $notices")
    adapter.submitList(notices)
}

@BindingAdapter("imageUrl")
fun bindImage(img: ImageView, imgUrl: String?) {
    imgUrl?.let {
        println("imgUrl = $it")
        val imgUrl = imgUrl.toUri().buildUpon().scheme(SCHEME).build()
        println("imgUri = $imgUrl")
        println("build success")
        img.load(imgUrl) {
            placeholder(R.mipmap.ic_loading)
            error(R.drawable.ic_broken_image)
        }
        println("load finish")
    }
}
