package com.example.xianhang

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class ViewProductFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_product, container, false)
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
