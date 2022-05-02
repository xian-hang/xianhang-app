package com.example.xianhang.buy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.xianhang.R
import com.example.xianhang.adapter.PRODUCT_ITEM
import com.example.xianhang.databinding.FragmentProductDetailsBinding
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.model.ProductItem
import com.example.xianhang.product.ProductViewModel

class ProductDetailsFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailsBinding
    private var productItem: ProductItem? = null
    private val viewModel: ProductViewModel by viewModels {
        productItem = activity?.intent?.extras?.getParcelable(PRODUCT_ITEM)
        println("id = ${productItem?.product?.id}")
        ProductViewModel.Factory(productItem!!.product.id!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profile.setOnClickListener {
            // TODO: goto profile
            println("user id pass ${productItem?.product?.userId}")
            val bundle = bundleOf(ID to productItem?.product?.userId)
            findNavController().navigate(R.id.action_productDetailsFragment_to_userFragment, bundle)
        }

        binding.buy.setOnClickListener {
            // TODO: goto order
        }

        binding.collectionOutline.setOnClickListener {
            // TODO: collect product
        }

        binding.collection.setOnClickListener {
            // TODO: uncollect product
        }
    }
}