package com.example.xianhang.order

import android.content.Context.MODE_PRIVATE
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
import com.example.xianhang.adapter.PRODUCT_ITEM
import com.example.xianhang.databinding.FragmentProductDetailsBinding
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.*
import com.example.xianhang.network.Api
import com.example.xianhang.product.ProductViewModel
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

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
        binding.profile.setOnClickListener {
            profile()
        }

        binding.buy.setOnClickListener {
            println("order")
            order()
        }

        binding.uncollect.setOnClickListener {
            // TODO: get collect id
            collect()
        }

        binding.collect.setOnClickListener {
            uncollect()
        }
    }

    private fun profile() {
        println("user id pass ${productItem?.product?.userId}")
        val bundle = bundleOf(ID to productItem?.product?.userId)
        findNavController().navigate(R.id.action_productDetailsFragment_to_userFragment, bundle)
    }

    private fun order() {
        if (!checkData()) return
        val tradingMethod = when (binding.tradingMethod.checkedRadioButtonId) {
            R.id.deliver -> DELIVERY
            R.id.pickup -> PICKUP
            else -> -1
        }
        val amount = binding.num.text.toString().toInt()
        orderViewModel.setOrder(productItem!!.product, amount, tradingMethod)

        findNavController().navigate(R.id.action_productDetailsFragment_to_buyFragment)
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

    private fun collect() {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_LONG).show()
            return
        }

        binding.uncollect.visibility = View.GONE
        binding.collect.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val obj = ProductId(productItem!!.product.id!!)
                val resp = Api.retrofitService.collect(token, obj)
                if (resOk(resp)) {
                    Toast.makeText(requireActivity(), "collected", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireActivity(), resp.message, Toast.LENGTH_LONG).show()
                }
            } catch (e: HttpException) {
                Toast.makeText(requireActivity(), e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun uncollect() {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_LONG).show()
            return
        }

        binding.collect.visibility = View.GONE
        binding.uncollect.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.uncollect(token, binding.viewModel!!.collectId!!)
                if (resOk(resp)) {
                    Toast.makeText(requireActivity(), "uncollected", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireActivity(), resp.message, Toast.LENGTH_LONG).show()
                }
            } catch (e: HttpException) {
                Toast.makeText(requireActivity(), e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}