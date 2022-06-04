package com.example.xianhang.product

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.xianhang.R
import com.example.xianhang.adapter.ACTION
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.adapter.ProductAdapter
import com.example.xianhang.adapter.SELLER
import com.example.xianhang.databinding.FragmentProductBinding
import com.example.xianhang.login.LoginActivity
import com.example.xianhang.login.LoginFragment.Companion.TOKEN

const val SOLD = "sold"
const val SALES = "sales"

class ProductFragment : Fragment() {

    private lateinit var binding: FragmentProductBinding
    private val viewModel: ProductsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProductBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.productsItem.adapter = ProductAdapter(SELLER, context)

        fillInfo()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val id = sharedPreferences?.getInt(ID, 0)
        val token = sharedPreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(context, "Please login", Toast.LENGTH_LONG).show()
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
            return
        }

        viewModel.setProducts(context, SELLER, token, id)
        binding.sellProduct.setOnClickListener {
            createProduct()
        }

        binding.refresh.setOnRefreshListener {
            viewModel.setProducts(context, SELLER, token, id)
            binding.refresh.isRefreshing = false
        }
    }

    private fun fillInfo() {
        val sold = activity?.intent?.getStringExtra(SOLD)
        val sales = activity?.intent?.getDoubleExtra(SALES, 0.0)
        binding.soldInfo.sold.text = sold
        binding.soldInfo.sales.text = String.format("售出价格: $%.2f", sales)
    }

    private fun createProduct() {
        val intent = Intent(context, SellActivity::class.java)
        intent.putExtra(ACTION, "create")
        startActivity(intent)
    }
}