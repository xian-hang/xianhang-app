package com.example.xianhang.search

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.xianhang.adapter.ProductAdapter
import com.example.xianhang.adapter.QUERY
import com.example.xianhang.adapter.SEARCH
import com.example.xianhang.databinding.FragmentSearchProductsBinding
import com.example.xianhang.login.LoginFragment
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.product.ProductsViewModel

class SearchProductsFragment : Fragment() {

    private lateinit var binding: FragmentSearchProductsBinding
    private val viewModel: ProductsViewModel by viewModels {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(LoginFragment.TOKEN, null)
        val query = activity?.intent?.extras?.getString(QUERY)
        println("token = " + token?.isNotEmpty().toString())
        ProductsViewModel.Factory(SEARCH, null, token, query!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchProductsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.products.adapter = ProductAdapter(SEARCH, context)

        val query = activity?.intent?.extras?.getString(QUERY)
        binding.search.setQuery(query!!, false)

        return binding.root
    }
}