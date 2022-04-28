package com.example.xianhang

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.xianhang.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.*
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SellProductFragment : Fragment() {

    private lateinit var imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sell_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.image)
        imageView.setOnClickListener {
            uploadImage()
        }

        val sell = view.findViewById<Button>(R.id.sell)
        sell.setOnClickListener {
            requestSellProduct(view)
        }
    }

    // TODO: upload image
    private fun requestSellProduct(view: View) {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_LONG).show()
            return
        }
        if (!checkData(view)) return

        val product = getData(view)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.sellProduct(token, product)
                if (resOk(resp)) {
                    // val r = Api.retrofitService.
                    val bundle = bundleOf("id" to resp.id)
                    findNavController().navigate(R.id.action_sellProductFragment_to_viewProductFragment, bundle)
                } else {
                    Toast.makeText(requireActivity(), "Create Error", Toast.LENGTH_LONG).show()
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

    private fun checkData(view: View): Boolean {
        val name = view.findViewById<TextInputEditText>(R.id.product_name).text.toString()
        val description = view.findViewById<TextInputEditText>(R.id.product_description).text.toString()
        val price = view.findViewById<TextInputEditText>(R.id.product_price).text.toString().toDouble()
        val stock = view.findViewById<TextInputEditText>(R.id.product_stock).text.toString().toInt()
        val deliver = view.findViewById<CheckBox>(R.id.deliver).isChecked
        val pickup = view.findViewById<CheckBox>(R.id.pickup).isChecked
        val address = view.findViewById<TextInputEditText>(R.id.pickup_address).text.toString()

        if (name.isEmpty()) {
            Toast.makeText(requireActivity(), "Please fill in the product name", Toast.LENGTH_LONG).show()
            return false
        }

        if (description.isEmpty()) {
            Toast.makeText(requireActivity(), "Please fill in the description", Toast.LENGTH_LONG).show()
            return false
        }

        if (price <= 0) {
            Toast.makeText(requireActivity(), "Please fill in the product price", Toast.LENGTH_LONG).show()
            return false
        }

        if (stock <= 0) {
            Toast.makeText(requireActivity(), "Please fill in the product stock", Toast.LENGTH_LONG).show()
            return false
        }

        if (!deliver && !pickup) {
            Toast.makeText(requireActivity(), "Please choose trading method", Toast.LENGTH_LONG).show()
            return false
        }

        if (pickup && address.isEmpty()) {
            Toast.makeText(requireActivity(), "Please fill in pickup address", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun getData(view: View): Product {
        val name = view.findViewById<TextInputEditText>(R.id.product_name).text.toString()
        val description = view.findViewById<TextInputEditText>(R.id.product_description).text.toString()
        val price = view.findViewById<TextInputEditText>(R.id.product_price).text.toString().toDouble()
        val stock = view.findViewById<TextInputEditText>(R.id.product_stock).text.toString().toInt()
        val deliver = view.findViewById<CheckBox>(R.id.deliver).isChecked
        val pickup = view.findViewById<CheckBox>(R.id.pickup).isChecked
        var address: String? = view.findViewById<TextInputEditText>(R.id.pickup_address).text.toString()
        val tradingMethod = if (deliver && pickup) BOTH
        else if (deliver) DELIVERY
        else PICKUP
        if (!pickup) address = null

        return Product(name, description, price, stock, tradingMethod, address)
    }

    @Suppress("DEPRECATION")
    private fun uploadImage() {
        println("upload image")
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            startActivityForResult(it, REQUEST_CODE_IMAGE)
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
            imageView.setImageURI(data?.data)
        }
    }

    companion object {
        private const val REQUEST_CODE_IMAGE = 100
    }
}
