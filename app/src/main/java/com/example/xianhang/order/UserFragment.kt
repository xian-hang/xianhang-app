package com.example.xianhang.order

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.xianhang.R
import com.example.xianhang.adapter.ProductAdapter
import com.example.xianhang.adapter.USER_PRODUCT
import com.example.xianhang.databinding.FragmentUserBinding
import com.example.xianhang.home.ProfileViewModel
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.login.LoginFragment.Companion.USERNAME
import com.example.xianhang.model.UserId
import com.example.xianhang.network.Api
import com.example.xianhang.product.ProductsViewModel
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private val productsViewModel: ProductsViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    private var userId: Int? = null
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.profileViewModel = profileViewModel
        binding.productsViewModel = productsViewModel
        binding.profile.viewModel = profileViewModel
        binding.products.adapter = ProductAdapter(USER_PRODUCT, context)

        val id = activity?.intent?.extras?.getInt(ID)
        println("user id = $id")
        userId = id

        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        token = sharedPreferences?.getString(TOKEN, null)

        productsViewModel.setProducts(context, USER_PRODUCT, token!!, userId)
        binding.refresh.setOnRefreshListener {
            productsViewModel.setProducts(context, USER_PRODUCT, token!!, userId)
            binding.refresh.isRefreshing = false
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel.setProfile(context, token!!, userId!!)

        binding.like.setOnClickListener {
            like()
        }

        binding.unlike.setOnClickListener {
            like()
        }

        binding.follow.setOnClickListener {
            follow()
        }

        binding.report.setOnClickListener {
            report()
        }

        binding.inbox.setOnClickListener {
            chat()
        }
    }


    private fun report() {
        val bundle = bundleOf(ID to userId)
        findNavController().navigate(R.id.action_userFragment2_to_reportFragment2, bundle)
    }


    private fun like() {
        if (token == null) {
            Toast.makeText(context, "Please login", Toast.LENGTH_LONG).show()
            return
        }
        profileViewModel.setLike(context, token!!, userId)
    }


    private fun follow() {
        if (token == null) {
            Toast.makeText(context, "Please login", Toast.LENGTH_LONG).show()
            return
        }
        profileViewModel.setFollow(context, token!!, userId)
    }


    private fun chat() {
        val username = binding.profile.username.text
        val bundle = bundleOf(ID to userId, USERNAME to username)
        if (token == null) {
            Toast.makeText(context, "Please login", Toast.LENGTH_LONG).show()
            return
        }
        findNavController().navigate(R.id.action_userFragment2_to_chatFragment3, bundle)
    }
}