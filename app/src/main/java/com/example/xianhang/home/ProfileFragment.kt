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
import androidx.core.view.ViewCompat
import com.example.xianhang.*
import com.example.xianhang.databinding.FragmentProfileBinding
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.PASSWORD
import com.example.xianhang.login.LoginFragment.Companion.REMEMBER
import com.example.xianhang.login.LoginFragment.Companion.ROLE
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.login.LoginFragment.Companion.USER
import com.example.xianhang.login.LoginActivity
import com.example.xianhang.network.Api
import com.example.xianhang.order.OrderActivity
import com.example.xianhang.order.ViewOrderActivity
import com.example.xianhang.product.ProductActivity
import com.example.xianhang.rest.resOk
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class ProfileFragment : Fragment() {

    private var _id: Int = 0
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpProfile(view)

        binding.editProfile.setOnClickListener { changeActivityEditProfile(view) }
        binding.changePassword.setOnClickListener { changeActivityChangePassword() }
        binding.sellingProducts.setOnClickListener { changeActivityProducts() }
        binding.orders.setOnClickListener { changeActivityMyOrders() }
        binding.notification.setOnClickListener { changeActivityNotifications() }
        binding.logout.setOnClickListener { showLogoutDialog() }
        binding.deleteAccount.setOnClickListener { showDeleteAccountDialog() }
    }

    private fun setUpProfile(view: View) {
        val sharePreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharePreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_LONG).show()
            return
        }

        val username = view.findViewById<TextView>(R.id.username)
        val userId = view.findViewById<TextView>(R.id.userId)
        val details = view.findViewById<TextView>(R.id.details)
        val introduction = view.findViewById<TextView>(R.id.introduction)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.getProfile(token)
                if (resOk(resp)) {
                    progressBar.visibility = View.GONE
                    _id = resp.id!!
                    username.text = resp.username
                    userId.text = resp.userId
                    introduction.text = resp.introduction
                    details.text = resources.getString(
                        R.string.profile_details,
                        String.format("%.2f", resp.credit),
                        resp.likes,
                        resp.soldItem
                    )
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

    private fun changeActivityEditProfile(view: View) {
        val username = view.findViewById<TextView>(R.id.username).text.toString()
        val introduction = view.findViewById<TextView>(R.id.introduction).text.toString()
        val intent = Intent(requireActivity(), EditProfileActivity::class.java)
        intent.putExtra("username", username)
        intent.putExtra("introduction", introduction)
        startActivity(intent)
    }

    private fun changeActivityChangePassword() {
        startActivity(Intent(requireActivity(), ChangePasswordActivity::class.java))
    }

    private fun changeActivityProducts() {
        val intent = Intent(requireActivity(), ProductActivity::class.java)
        startActivity(intent)
    }

    // TODO: wait backend change productId to product
    private fun changeActivityMyOrders() {
        val intent = Intent(requireActivity(), ViewOrderActivity::class.java)
        startActivity(intent)
    }

    // TODO: implement
    private fun changeActivityNotifications() {
        Toast.makeText(requireActivity(), "not yet implement", Toast.LENGTH_LONG).show()
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
            Toast.makeText(requireActivity(), "User Must Login", Toast.LENGTH_LONG).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.logout(token)
                if (resOk(resp)) {
                    editor?.putString(ROLE, null)
                    editor?.putString(TOKEN, null)
                    editor?.putBoolean(REMEMBER, false)
                    editor?.apply()

                    val intent = Intent(context, LoginActivity::class.java)
                    context?.startActivity(intent)
                } else {
                    Toast.makeText(requireActivity(), "Logout Error", Toast.LENGTH_LONG).show()
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
                if (resOk(resp)) {
                    editor?.putString(USER, null)
                    editor?.putString(PASSWORD, null)
                    editor?.putString(ROLE, null)
                    editor?.putString(TOKEN, null)
                    editor?.putBoolean(REMEMBER, false)
                    editor?.apply()

                    val intent = Intent(context, LoginActivity::class.java)
                    context?.startActivity(intent)
                } else {
                    Toast.makeText(requireActivity(), "Delete Error", Toast.LENGTH_LONG).show()
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