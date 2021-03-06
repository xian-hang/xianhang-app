package com.example.xianhang.search

import android.content.Context.MODE_PRIVATE
import android.content.Intent
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
import com.example.xianhang.login.LoginActivity
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(context, "Please login", Toast.LENGTH_LONG).show()
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
            return
        }

        val query = activity?.intent?.extras?.getString(QUERY)
        binding.search.setQuery(query!!, false)

        viewModel.search(context, token, query)
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query == null) return false
                binding.search.clearFocus()
                viewModel.search(context, token, query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

    }
}