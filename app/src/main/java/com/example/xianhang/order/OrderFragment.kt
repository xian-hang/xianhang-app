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
import androidx.navigation.fragment.findNavController
import com.example.xianhang.R
import com.example.xianhang.adapter.PRICE
import com.example.xianhang.adapter.TO
import com.example.xianhang.databinding.FragmentOrderBinding
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.*
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class OrderFragment : Fragment() {

    private lateinit var binding: FragmentOrderBinding
    private val viewModel: OrderViewModel by activityViewModels()
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        token = sharedPreferences?.getString(TOKEN, null)

        binding = FragmentOrderBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getOrder()
    }

    private fun getOrder() {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)

        println("token = $token")
        if (token == null) {
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_LONG).show()
            return
        }

        val id = activity?.intent?.extras?.getInt(ID)
        val userId = sharedPreferences?.getInt(ID, 0)
        println("order id = $id, userId = $userId")
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.getOrder(token!!, id!!)
                println(resp)
                if (resOk(resp)) {
                    val isBuyer = resp.order.userId == userId
                    binding.viewModel!!.setOrder(resp.order, isBuyer)
                    binding.action.setOnClickListener {
                        action(resp.order.status!!, isBuyer, resp.order.price)
                    }
                    binding.cancel.setOnClickListener {
                        cancel(isBuyer)
                    }
                } else {
                    Toast.makeText(requireActivity(), "view order failed", Toast.LENGTH_LONG).show()
                }
            } catch (e: HttpException) {
                Toast.makeText(requireActivity(), e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                // TODO: check connection wrong
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    private fun action(status: Int, isBuyer: Boolean, price: Double) {
        when (status) {
            UNPAID -> if (isBuyer) {
                val bundle = bundleOf(PRICE to price)
                findNavController().navigate(R.id.action_orderFragment_to_payFragment, bundle)
            } else {
                findNavController().navigate(R.id.action_orderFragment_to_postageFragment)
            }
            PAID -> if (!isBuyer) {
                viewModel.setStatus(context, token!!, SHIPPED, isBuyer)
            }
            SHIPPED -> if (isBuyer) {
                viewModel.setStatus(context, token!!, COMPLETE, isBuyer)
            }
        }
    }

    private fun cancel(isBuyer: Boolean) {
        viewModel.setStatus(context, token!!, CANCEL, isBuyer)
    }
}