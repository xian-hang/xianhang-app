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
import androidx.navigation.fragment.findNavController
import com.example.xianhang.R
import com.example.xianhang.adapter.PRICE
import com.example.xianhang.adapter.TO
import com.example.xianhang.databinding.FragmentPayBinding
import com.example.xianhang.login.LoginActivity
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.OrderStatusRequest
import com.example.xianhang.model.PAID
import com.example.xianhang.model.PICKUP
import com.example.xianhang.model.SHIPPED
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class PayFragment : Fragment() {

    private lateinit var binding: FragmentPayBinding
    private val viewModel: OrderViewModel by activityViewModels ()
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val price = viewModel.price.value?.plus(viewModel.postage.value!!)
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        token = sharedPreferences?.getString(TOKEN, null)

        binding = FragmentPayBinding.inflate(inflater)
        binding.needPay.text = resources.getString(R.string.need_pay, price)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.confirm.setOnClickListener {
            setStatus()
        }
    }

    private fun setStatus() {
        val text = binding.cost.text
        val needed = viewModel.price.value.toString().toDouble() + viewModel.postage.value.toString().toDouble()
        if (text.isEmpty()) {
            Toast.makeText(context, "please fill the price", Toast.LENGTH_LONG).show()
            return
        }
        val cost = text.toString().toDouble()
        if (cost != needed) {
            Toast.makeText(context, "输入金额和需缴付金额不一致", Toast.LENGTH_LONG).show()
            return
        }
        if (token == null) {
            Toast.makeText(context, "Please login", Toast.LENGTH_LONG).show()
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
            return
        }

        val to = arguments?.getInt(TO)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.editOrderStatus(
                    token!!,
                    viewModel.order.value!!.id!!,
                    OrderStatusRequest(PAID)
                )
                if (resOk(context, resp)) {
                    Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
                    if (viewModel.order.value!!.tradingMethod == PICKUP) {
                        viewModel.updateStatus(SHIPPED, true)
                    } else {
                        viewModel.updateStatus(PAID, true)
                    }
                    findNavController().popBackStack()
                }
            } catch (e: HttpException) {
                Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }
}