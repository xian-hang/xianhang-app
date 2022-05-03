package com.example.xianhang.home

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.xianhang.R
import com.example.xianhang.adapter.BUYER
import com.example.xianhang.adapter.COLLECTION
import com.example.xianhang.adapter.ProductAdapter
import com.example.xianhang.databinding.FragmentCollectionBinding
import com.example.xianhang.login.LoginFragment
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.product.ProductsViewModel

class CollectionFragment : Fragment() {

    private lateinit var binding: FragmentCollectionBinding
    private val viewModel: ProductsViewModel by viewModels {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)
        println("token = " + token?.isNotEmpty().toString())
        ProductsViewModel.Factory(COLLECTION, null, token)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCollectionBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.products.adapter = ProductAdapter(COLLECTION, context)
        return binding.root
    }
}