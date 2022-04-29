package com.example.xianhang.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import com.example.xianhang.R
import com.example.xianhang.databinding.FragmentViewProductBinding

class ViewProductFragment : Fragment() {

    private val viewModel: ProductViewModel by viewModels {
        val id = arguments?.getInt("id")
        println("id = " + id.toString())
        ProductViewModel.Factory(id!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentViewProductBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val edit = view.findViewById<Button>(R.id.edit)
        edit.setOnClickListener {

        }

        val delete = view.findViewById<Button>(R.id.delete)
        delete.setOnClickListener {

        }
    }

    private fun requestEditProduct() {
        // TODO: implement
    }

    private fun requestDeleteProduct() {
        // TODO: implement
    }
}
