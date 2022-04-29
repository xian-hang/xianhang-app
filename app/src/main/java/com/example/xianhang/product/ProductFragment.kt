package com.example.xianhang.product

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xianhang.R
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.adapter.ProductAdapter
import com.example.xianhang.databinding.FragmentProductBinding
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class ProductFragment : Fragment() {

    private val userId: Int

    init {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        userId = sharedPreferences?.getInt(ID, 0)!!
    }

    private val viewModel: ProductViewModel by viewModels {
        ProductViewModel.Factory(userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentProductBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.productsItem.adapter = ProductAdapter(context, viewModel.products.value!!)

        return binding.root
        // return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserProducts(userId, view)

        val sell = view.findViewById<Button>(R.id.sell_product)
        sell.setOnClickListener {
            createProduct()
        }
    }

    // TODO: create products adapter
    private fun getUserProducts(id: Int, view: View) {
        println(id)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.getUserProduct(id)
                if (resOk(resp)) {
                    println(resp.products)
                    val recyclerView = view.findViewById<RecyclerView>(R.id.products_item)
                    recyclerView.adapter = ProductAdapter(context, viewModel.products.value!!)
                    recyclerView.layoutManager = LinearLayoutManager(context)
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

    private fun createProduct() {
        findNavController().navigate(R.id.action_productFragment2_to_sellProductFragment)
    }
}