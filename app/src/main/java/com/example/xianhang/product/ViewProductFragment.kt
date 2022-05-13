package com.example.xianhang.product

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.xianhang.R
import com.example.xianhang.adapter.ACTION
import com.example.xianhang.adapter.PRODUCT_ITEM
import com.example.xianhang.databinding.FragmentViewProductBinding
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.ProductItem
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
    private val viewModel: ProductViewModel by viewModels()
    private var productItem: ProductItem? = null
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentViewProductBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        productItem = arguments?.getParcelable(PRODUCT_ITEM)
        viewModel.setProduct(productItem!!)

        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        token = sharedPreferences?.getString(TOKEN, null)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (token == null) {
            Toast.makeText(context, "Please login", Toast.LENGTH_LONG).show()
            return
        }

        binding.edit.setOnClickListener {
            navigateEdit()
        }

        binding.delete.setOnClickListener {
            showDeleteDialog()
        }
    }

    private fun navigateEdit() {
        val bundle = bundleOf(PRODUCT_ITEM to productItem, ACTION to "edit")
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
                if (resOk(context, resp)) {
                    findNavController().navigate(R.id.action_viewProductFragment_to_productFragment2)
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
