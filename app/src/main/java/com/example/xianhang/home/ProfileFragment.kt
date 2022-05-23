package com.example.xianhang.home

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import com.example.xianhang.adapter.BUYER
import com.example.xianhang.adapter.METHOD
import com.example.xianhang.adapter.SELLER
import com.example.xianhang.databinding.FragmentProfileBinding
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.PASSWORD
import com.example.xianhang.login.LoginFragment.Companion.REMEMBER
import com.example.xianhang.login.LoginFragment.Companion.ROLE
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.login.LoginFragment.Companion.USER
import com.example.xianhang.login.LoginActivity
import com.example.xianhang.network.Api
import com.example.xianhang.network.WebSocketService
import com.example.xianhang.network.WebSocketService.Companion.dataClear
import com.example.xianhang.notification.NoticeActivity
import com.example.xianhang.order.OrderStatusActivity
import com.example.xianhang.order.POSITION
import com.example.xianhang.order.ViewOrdersActivity
import com.example.xianhang.product.ProductActivity
import com.example.xianhang.product.SALES
import com.example.xianhang.product.SOLD
import com.example.xianhang.rest.resOk
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.profile.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharePreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        token = sharePreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(context, "Please login", Toast.LENGTH_LONG).show()
            return
        }

        viewModel.setProfile(context, token!!)
        setUpTracking()
        setUpList()
    }

    private fun setUpList() {
        binding.editProfile.setOnClickListener { changeActivityEditProfile() }
        binding.changePassword.setOnClickListener { changeActivityChangePassword() }
        binding.sellingProducts.setOnClickListener { changeActivityProducts() }
        binding.orders.setOnClickListener { changeActivityMyOrders() }
        binding.notification.setOnClickListener { changeActivityNotifications() }
        binding.logout.setOnClickListener { showLogoutDialog() }
        binding.deleteAccount.setOnClickListener { showDeleteAccountDialog() }
    }

    private fun setUpTracking() {
        val sharePreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharePreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_LONG).show()
            return
        }

        val intent = Intent(context, OrderStatusActivity::class.java)
        binding.track.toPay.setOnClickListener {
            intent.putExtra(POSITION, 0)
            startActivity(intent)
        }
        binding.track.toSend.setOnClickListener {
            intent.putExtra(POSITION, 1)
            startActivity(intent)
        }
        binding.track.toReceive.setOnClickListener {
            intent.putExtra(POSITION, 2)
            startActivity(intent)
        }
    }

    private fun changeActivityEditProfile() {
        val username = binding.profile.username.text.toString()
        val introduction = binding.profile.introduction.text.toString()
        val intent = Intent(requireActivity(), EditProfileActivity::class.java)
        intent.putExtra("username", username)
        intent.putExtra("introduction", introduction)
        startActivity(intent)
    }

    private fun changeActivityChangePassword() {
        startActivity(Intent(context, ChangePasswordActivity::class.java))
    }

    private fun changeActivityProducts() {
        val intent = Intent(context, ProductActivity::class.java)
        intent.putExtra(METHOD, SELLER)
        intent.putExtra(SOLD, viewModel.sold.value)
        intent.putExtra(SALES, viewModel.totalSales.value)
        startActivity(intent)
    }

    private fun changeActivityMyOrders() {
        val intent = Intent(requireActivity(), ViewOrdersActivity::class.java)
        intent.putExtra(METHOD, BUYER)
        startActivity(intent)
    }

    private fun changeActivityNotifications() {
        val intent = Intent(context, NoticeActivity::class.java)
        startActivity(intent)
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("确定要登出这个设备吗？")
            .setPositiveButton("确认") { _, _ ->
                requestLogout()
            }
            .setNegativeButton("取消") { _, _ ->

            }
            .show()
    }

    @SuppressLint("CommitPrefEdits")
    private fun requestLogout() {
        val sharePreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val editor = sharePreferences?.edit()
        val token = sharePreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(context, "User Must Login", Toast.LENGTH_LONG).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.logout(token)
                if (resOk(context, resp)) {
                    editor?.putString(ROLE, null)
                    editor?.putString(TOKEN, null)
                    editor?.putBoolean(REMEMBER, false)
                    editor?.apply()

                    context?.stopService(Intent(activity?.applicationContext, WebSocketService::class.java))
                    dataClear()
                    val intent = Intent(context, LoginActivity::class.java)
                    context?.startActivity(intent)
                    activity?.finish()
                }
            } catch (e: HttpException) {
                Toast.makeText(requireActivity(), e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    private fun showDeleteAccountDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("警告")
            .setMessage("确定停用你的帐号吗？")
            .setPositiveButton("确认") { _, _ ->
                requestDeleteAccount()
            }
            .setNegativeButton("取消") { _, _ ->

            }
            .show()
    }

    private fun requestDeleteAccount() {
        val sharePreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val editor = sharePreferences?.edit()
        val token = sharePreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(requireActivity(), "User Must Login", Toast.LENGTH_LONG).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.deleteUser(token)
                if (resOk(context, resp)) {
                    editor?.putString(USER, null)
                    editor?.putString(PASSWORD, null)
                    editor?.putString(ROLE, null)
                    editor?.putString(TOKEN, null)
                    editor?.putBoolean(REMEMBER, false)
                    editor?.apply()

                    context?.stopService(Intent(activity?.applicationContext, WebSocketService::class.java))
                    dataClear()

                    val intent = Intent(context, LoginActivity::class.java)
                    context?.startActivity(intent)
                    activity?.finish()
                }
            } catch (e: HttpException) {
                Toast.makeText(requireActivity(), e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }
}
