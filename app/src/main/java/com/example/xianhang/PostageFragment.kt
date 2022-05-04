package com.example.xianhang

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.xianhang.databinding.FragmentPostageBinding
import com.example.xianhang.order.OrderViewModel

class PostageFragment : Fragment() {

    private lateinit var binding: FragmentPostageBinding
    private val viewModel: OrderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPostageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val text = binding.cost.text
        if (text.isEmpty()) {
            Toast.makeText(context, "please fill the postage", Toast.LENGTH_LONG).show()
            return
        }

        val postage = text.toString().toDouble()
        if (postage < 0) {
            Toast.makeText(context, "postage must more than or equal to 0", Toast.LENGTH_LONG).show()
            return
        }

        binding.confirm.setOnClickListener {
            viewModel.setPostage(context, postage)
        }
    }
}