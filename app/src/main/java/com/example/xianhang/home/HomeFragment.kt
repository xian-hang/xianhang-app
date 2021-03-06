package com.example.xianhang.home

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.xianhang.search.SearchActivity
import com.example.xianhang.adapter.BUYER
import com.example.xianhang.adapter.FEEDS
import com.example.xianhang.adapter.ProductAdapter
import com.example.xianhang.adapter.QUERY
import com.example.xianhang.databinding.FragmentHomeBinding
import com.example.xianhang.login.LoginActivity
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.product.ProductsViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: ProductsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.products.adapter = ProductAdapter(BUYER, context)

        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)
        viewModel.setProducts(context, BUYER, token!!, null)

        binding.refresh.setOnRefreshListener {
            viewModel.setProducts(context, BUYER, token, null)
            binding.refresh.isRefreshing = false
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query == null) return false
                binding.search.clearFocus()
                search(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun search(query: String?) {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_LONG).show()
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
            return
        }

        val intent = Intent(context, SearchActivity::class.java)
        intent.putExtra(QUERY, query)
        startActivity(intent)
    }
}
