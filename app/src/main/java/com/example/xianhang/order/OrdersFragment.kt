package com.example.xianhang.order

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    private val viewModel: OrdersViewModel by viewModels()
    private var method: Int? = null
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        method = activity?.intent?.extras?.getInt(METHOD)

        binding = FragmentOrdersBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.orders.adapter = OrderAdapter(method!!, ALL, context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        token = sharedPreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(context, "Please login", Toast.LENGTH_LONG).show()
            return
        }

        binding.title.text = if (method == SELLER) "出售订单" else ""
        if (method != SELLER) binding.title.visibility = View.GONE

        viewModel.setOrders(context, token!!, method!!, ALL)
    }
}
