package com.example.xianhang.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.xianhang.R
import com.example.xianhang.databinding.FragmentViewProductBinding
import com.example.xianhang.model.Product

class ViewProductFragment : Fragment() {

    private lateinit var binding: FragmentViewProductBinding
    private val viewModel: ProductViewModel by viewModels {
        val product = arguments?.getParcelable<Product>("product")
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

        }
    }

    private fun navigateEdit() {
        val product = arguments?.getParcelable<Product>("product")
        val bundle = bundleOf("product" to product, "type" to "edit")
        findNavController().navigate(R.id.action_viewProductFragment_to_sellProductFragment, bundle)
    }

    private fun requestDeleteProduct() {
        // TODO: implement
    }
}
