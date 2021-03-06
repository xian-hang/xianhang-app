package com.example.xianhang.order

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.xianhang.adapter.BUYER
import com.example.xianhang.adapter.OrderAdapter
import com.example.xianhang.databinding.FragmentSentBinding
import com.example.xianhang.login.LoginActivity
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.PAID
import com.example.xianhang.model.SHIPPED

class SentFragment : Fragment() {

    private lateinit var binding: FragmentSentBinding
    private val viewModel: OrdersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.orders.adapter = OrderAdapter(BUYER, SHIPPED, context)

        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)
        if (token == null) {
            Toast.makeText(context, "Please login", Toast.LENGTH_LONG).show()
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
            return null
        }
        viewModel.setOrders(context, token, BUYER, SHIPPED)

        binding.refresh.setOnRefreshListener {
            viewModel.setOrders(context, token, BUYER, SHIPPED)
            binding.refresh.isRefreshing = false
        }

        return binding.root
    }
}