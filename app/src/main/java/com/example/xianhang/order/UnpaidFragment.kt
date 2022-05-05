package com.example.xianhang.order

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.xianhang.adapter.BUYER
import com.example.xianhang.adapter.OrderAdapter
import com.example.xianhang.databinding.FragmentUnpaidBinding
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.SHIPPED
import com.example.xianhang.model.UNPAID

class UnpaidFragment : Fragment() {

    private lateinit var binding: FragmentUnpaidBinding
    private val viewModel: OrdersViewModel by viewModels {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)
        val method = BUYER
        println("token = " + token.toString())
        OrdersViewModel.Factory(method, token!!, UNPAID, context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUnpaidBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.orders.adapter = OrderAdapter(BUYER, UNPAID, context)

        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)
        viewModel.setOrders(context, token!!, BUYER, UNPAID)

        return binding.root
    }
}