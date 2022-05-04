package com.example.xianhang

import android.content.Context
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
import com.example.xianhang.databinding.FragmentUnpaidBinding
import com.example.xianhang.login.LoginFragment
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.ALL
import com.example.xianhang.model.UNPAID
import com.example.xianhang.order.OrdersViewModel

class UnpaidFragment : Fragment() {

    private lateinit var binding: FragmentUnpaidBinding
    private val viewModel: OrdersViewModel by viewModels {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)
        val method = BUYER
        println("token = " + token.toString())
        OrdersViewModel.Factory(method, token!!, UNPAID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUnpaidBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val method = BUYER
        binding.orders.adapter = OrderAdapter(method, context)

        return binding.root
    }
}