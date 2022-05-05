package com.example.xianhang.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.xianhang.adapter.CONTENT
import com.example.xianhang.databinding.FragmentNoticeBinding
import com.example.xianhang.login.LoginFragment.Companion.ID

class NoticeFragment : Fragment() {

    private lateinit var binding: FragmentNoticeBinding
    private val viewModel: NoticeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNoticeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.setNotice(arguments?.getInt(ID)!!, arguments?.getString(CONTENT)!!)
        return binding.root
    }
}