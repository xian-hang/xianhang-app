package com.example.xianhang.product

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.xianhang.R
import com.example.xianhang.adapter.ACTION
import com.example.xianhang.adapter.IMAGE_URL
import com.example.xianhang.adapter.PRODUCT
import com.example.xianhang.databinding.FragmentViewProductBinding
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.Product
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class ViewProductFragment : Fragment() {

    private lateinit var binding: FragmentViewProductBinding
    private val viewModel: ProductViewModel by viewModels {
        val product = arguments?.getParcelable<Product>(PRODUCT)
        println("id = " + product?.id.toString())
        ProductViewModel.Factory(product!!.id!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentViewProductBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val edit = view.findViewById<Button>(R.id.edit)
        edit.setOnClickListener {
            navigateEdit()
        }

        val delete = view.findViewById<Button>(R.id.delete)
        delete.setOnClickListener {
            showDeleteDialog()
        }
    }

    private fun navigateEdit() {
        val product = arguments?.getParcelable<Product>(PRODUCT)
        val imageUrl = viewModel.imageSrcUrl.value
        val bundle = bundleOf(PRODUCT to product, ACTION to "edit", IMAGE_URL to imageUrl)
        findNavController().navigate(R.id.action_viewProductFragment_to_sellProductFragment, bundle)
    }

    private fun showDeleteDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("确定删除商品吗？")
            .setPositiveButton("确认") { _, _ ->
                requestDeleteProduct()
            }
            .setNegativeButton("取消") { _, _ ->

            }
            .show()
    }

    private fun requestDeleteProduct() {
        // TODO: implement
        val id = binding.viewModel!!.product.value!!.id
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_LONG).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.deleteProduct(id!!, token)
                if (resOk(resp)) {
                    findNavController().navigate(R.id.action_viewProductFragment_to_productFragment2)
                } else {
                    Toast.makeText(requireActivity(), resp.message, Toast.LENGTH_LONG).show()
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
}
