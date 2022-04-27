package com.example.xianhang

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.xianhang.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.Product
import com.example.xianhang.model.TradingMethod
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SellProductFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sell_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val image = view.findViewById<ImageView>(R.id.image)
        image.setOnClickListener {
            Toast.makeText(requireActivity(), "Upload Image", Toast.LENGTH_LONG).show()
        }
    }

    private fun requestSellProduct(view: View) {
        val image = view.findViewById<ImageView>(R.id.image)
        image.setOnClickListener {
            uploadImage()
        }

        val name = view.findViewById<TextInputEditText>(R.id.product_name).text.toString()
        val description = view.findViewById<TextInputEditText>(R.id.product_description).text.toString()
        val price = view.findViewById<TextInputEditText>(R.id.product_price).text.toString().toDouble()
        val stock = view.findViewById<TextInputEditText>(R.id.product_stock).text.toString().toInt()
        val deliver = view.findViewById<CheckBox>(R.id.deliver).isChecked
        val pickup = view.findViewById<CheckBox>(R.id.pickup).isChecked
        val address = view.findViewById<TextInputEditText>(R.id.pickup_address).text.toString()

        if (pickup && address.isEmpty()) {
            Toast.makeText(requireActivity(), "Please fill in pickup address", Toast.LENGTH_LONG).show()
            return
        }

        val tradingMethod = if (deliver && pickup) TradingMethod.BOTH
        else if (deliver) TradingMethod.DELIVERY
        else TradingMethod.PICKUP

        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_LONG).show()
            return
        }

        val product = Product(name, description, price, stock, tradingMethod, address)
        CoroutineScope(Dispatchers.Main).launch {
            // TODO: post create product
            val resp = Api.retrofitService.sellProduct(token, product)
            if (resOk(resp)) {

            }
        }
    }

    private fun uploadImage() {
        Toast.makeText(requireActivity(), "Upload Image", Toast.LENGTH_LONG).show()
    }
}
