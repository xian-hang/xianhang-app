package com.example.xianhang

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.xianhang.adapter.PRODUCT_ITEM
import com.example.xianhang.databinding.FragmentBuyBinding
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.DELIVERY
import com.example.xianhang.model.OrderRequest
import com.example.xianhang.model.PICKUP
import com.example.xianhang.model.ProductItem
import com.example.xianhang.network.Api
import com.example.xianhang.order.OrderViewModel
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class BuyFragment : Fragment() {

    private lateinit var binding: FragmentBuyBinding
    private val viewModel: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBuyBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.confirm.setOnClickListener {
            // TODO: implement order
            order()
        }
    }

    private fun order() {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_LONG).show()
        }
        if (!checkData()) return

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.createOrder(token!!, getData())
                if (resOk(resp)) {
                    findNavController().navigate(R.id.action_buyFragment_to_orderFragment)
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

    private fun getData(): OrderRequest {
        val price = binding.price.text.toString().toDouble()
        val amount = binding.amount.text.toString().toInt()
        val productId = viewModel.product.value!!.id
        val name = binding.product.text.toString()
        val phoneNum = binding.phone.text.toString()
        val tradingMethod = if (binding.tradingMethod.text.toString() == "自取") PICKUP
        else DELIVERY
        var address: String? = null
        if (tradingMethod == DELIVERY) address = binding.address.text.toString()

        return OrderRequest(price, amount, productId!!, name, phoneNum, tradingMethod, address)
    }

    private fun checkData(): Boolean {

        if (binding.phone.text.isEmpty()) {
            Toast.makeText(requireActivity(), "Please fill the phone", Toast.LENGTH_LONG).show()
            return false
        }

        if (binding.phone.text.length != 11) {
            Toast.makeText(requireActivity(), "phone number length must be 11", Toast.LENGTH_LONG).show()
            return false
        }

        val tradingMethod = if (binding.tradingMethod.text.toString() == "自取") PICKUP
        else DELIVERY
        var address: String? = null
        if (tradingMethod == DELIVERY && binding.address.text.isEmpty()) {
            Toast.makeText(requireActivity(), "Please fill the address", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }
}