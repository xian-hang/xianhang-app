package com.example.xianhang.product

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.xianhang.adapter.BUYER
import com.example.xianhang.adapter.METHOD
import com.example.xianhang.adapter.OrderAdapter
import com.example.xianhang.adapter.SELLER
import com.example.xianhang.databinding.FragmentOrdersBinding
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.ALL
import com.example.xianhang.order.OrdersViewModel

class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    private val viewModel: OrdersViewModel by viewModels {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)
        val method = activity?.intent?.extras?.getInt(METHOD)
        println("token = " + token.toString())
        OrdersViewModel.Factory(method!!, token!!, ALL, context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOrdersBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val method = activity?.intent?.extras?.getInt(METHOD)
        binding.orders.adapter = OrderAdapter(method!!, ALL, context)
        binding.title.text = if (method == SELLER) "出售订单" else ""
        if (method != SELLER) binding.title.visibility = View.GONE

        return binding.root
    }
}
