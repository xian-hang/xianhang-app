package com.example.xianhang

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.xianhang.adapter.ProductAdapter
import com.example.xianhang.model.Product

@BindingAdapter("listitem")
fun bindUserProducts(recyclerView: RecyclerView, products: List<Product>?) {
    val adapter = recyclerView.adapter as ProductAdapter
    adapter.submitList(products)
}

