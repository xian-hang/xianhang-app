package com.example.xianhang.order

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.xianhang.R
import com.example.xianhang.UserActivity
import com.example.xianhang.adapter.PRODUCT_ITEM
import com.example.xianhang.databinding.FragmentProductDetailsBinding
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.*
import com.example.xianhang.product.ProductViewModel

class ProductDetailsFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailsBinding
    private var productItem: ProductItem? = null
    private val productViewModel: ProductViewModel by viewModels {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)
        productItem = activity?.intent?.extras?.getParcelable(PRODUCT_ITEM)
        println("id = ${productItem?.product?.id}")
        ProductViewModel.Factory(token!!, productItem!!.product.id!!)
    }
    private val orderViewModel: OrderViewModel by activityViewModels()
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = productViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        token = sharedPreferences?.getString(TOKEN, null)

        binding.profile.setOnClickListener {
            profile()
        }

        binding.buy.setOnClickListener {
            println("order")
            order()
        }

        binding.uncollect.setOnClickListener {
            collect()
        }

        binding.collect.setOnClickListener {
            collect()
        }
    }

    private fun profile() {
        println("user id pass ${productItem?.product?.userId}")
        val intent = Intent(context, UserActivity::class.java)
        intent.putExtra(ID, productItem?.product?.userId)
        startActivity(intent)
    }

    private fun order() {
        if (!checkData()) return
        val tradingMethod = when (binding.tradingMethod.checkedRadioButtonId) {
            R.id.deliver -> DELIVERY
            R.id.pickup -> PICKUP
            else -> -1
        }
        val amount = binding.num.text.toString().toInt()
        orderViewModel.previewOrder(productItem!!.product, amount, tradingMethod)
        findNavController().navigate(R.id.action_productDetailsFragment_to_buyFragment)
    }

    private fun collect() {
        if (token == null) {
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_LONG).show()
            return
        }
        binding.viewModel!!.setId(context, productItem?.product?.id)
    }

    private fun checkData(): Boolean {
        if (binding.num.text.isEmpty()) {
            Toast.makeText(requireActivity(), "Please fill the amount", Toast.LENGTH_LONG).show()
            return false
        }

        if (binding.num.text.toString().toInt() <= 0) {
            Toast.makeText(requireActivity(), "amount must more than 0", Toast.LENGTH_LONG).show()
            return false
        }

        if (binding.num.text.toString().toInt() > productItem!!.product.stock) {
            Toast.makeText(requireActivity(), "amount can\'t more than stock", Toast.LENGTH_LONG).show()
            return false
        }

        if (binding.tradingMethod.checkedRadioButtonId == -1) {
            Toast.makeText(requireActivity(), "Please choose a trading method", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }
}
