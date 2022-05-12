package com.example.xianhang.search

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import com.example.xianhang.adapter.ProductAdapter
import com.example.xianhang.adapter.QUERY
import com.example.xianhang.adapter.SEARCH
import com.example.xianhang.databinding.FragmentSearchProductsBinding
import com.example.xianhang.login.LoginFragment
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.product.ProductsViewModel

class SearchProductsFragment : Fragment() {

    private lateinit var binding: FragmentSearchProductsBinding
    private val viewModel: ProductsViewModel by viewModels()

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

        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query == null) return false
                if (token == null) {
                    Toast.makeText(context, "Please Login", Toast.LENGTH_LONG).show()
                    return false
                }
                binding.search.clearFocus()
                viewModel.search(context, token, query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        return binding.root
    }
}