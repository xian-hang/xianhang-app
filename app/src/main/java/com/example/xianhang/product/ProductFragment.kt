package com.example.xianhang.product

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.xianhang.R
import com.example.xianhang.adapter.ACTION
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.adapter.ProductAdapter
import com.example.xianhang.adapter.SELLER
import com.example.xianhang.databinding.FragmentProductBinding
import com.example.xianhang.login.LoginFragment.Companion.TOKEN

class ProductFragment : Fragment() {

    private lateinit var binding: FragmentProductBinding
    private val viewModel: ProductsViewModel by viewModels {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val id = sharedPreferences?.getInt(ID, 0)
        val token = sharedPreferences?.getString(TOKEN, null)
        println("id = " + id.toString())
        ProductsViewModel.Factory(SELLER, id, token, null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProductBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.productsItem.adapter = ProductAdapter(SELLER, context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sellProduct.setOnClickListener {
            createProduct()
        }
    }

    private fun createProduct() {
        val bundle = bundleOf(ACTION to "create")
        findNavController().navigate(R.id.action_productFragment2_to_sellProductFragment, bundle)
    }
}