package com.example.xianhang

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class ProfileFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpProfile(view)

        val listView = view.findViewById<ListView>(R.id.list)
        val list = arrayOf("Edit Profile", "Change Password", "Selling Products", "My Orders", "Notifications", "Logout", "Delete Account")
        val arrayAdapter = ArrayAdapter<String>(requireActivity(), R.layout.profile_list_item, list)
        listView.adapter = arrayAdapter
        listView.setOnItemClickListener { _, _, i, _ ->
            when (i) {
                0 -> changeActivityEditProfile(view)
                1 -> changeActivityChangePassword()
                2 -> changeActivityProducts()
                3 -> changeActivityMyOrders() // TODO
                4 -> changeActivityNotifications() // TODO
                5 -> requestLogout()
                6 -> requestDeleteAccount() // TODO
            }
        }
    }

    private fun setUpProfile(view: View) {
        val sharePreferences = activity?.getSharedPreferences(LoginFragment.LOGIN_PREF, Context.MODE_PRIVATE)
        val token = sharePreferences?.getString(LoginFragment.TOKEN, null)

        if (token == null) {
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_LONG).show()
            return
        }

        val username = view.findViewById<TextView>(R.id.username)
        val userId = view.findViewById<TextView>(R.id.userId)
        val details = view.findViewById<TextView>(R.id.details)
        val introduction = view.findViewById<TextView>(R.id.introduction)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.getProfile(token)
                if (resOk(resp)) {
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

    // TODO: add confirm window
    private fun changeActivityEditProfile(view: View) {
        val username = view.findViewById<TextView>(R.id.username).text.toString()
        val introduction = view.findViewById<TextView>(R.id.introduction).text.toString()
        val intent = Intent(requireActivity(), EditProfileActivity::class.java)
        intent.putExtra("username", username)
        intent.putExtra("introduction", introduction)
        startActivity(intent)
    }

    // TODO: add confirm window
    private fun changeActivityChangePassword() {
        startActivity(Intent(requireActivity(), ChangePasswordActivity::class.java))
    }

    // TODO
    private fun changeActivityProducts() {
        startActivity(Intent(requireActivity(), ProductActivity::class.java))
    }

    // TODO
    private fun changeActivityMyOrders() {
        Toast.makeText(requireActivity(), "not yet implement", Toast.LENGTH_LONG).show()
    }

    // TODO
    private fun changeActivityNotifications() {
        Toast.makeText(requireActivity(), "not yet implement", Toast.LENGTH_LONG).show()
    }

    @SuppressLint("CommitPrefEdits")
    private fun requestLogout() {
        val sharePreferences = activity?.getSharedPreferences(LoginFragment.LOGIN_PREF, Context.MODE_PRIVATE)
        val editor = sharePreferences?.edit()
        val token = sharePreferences?.getString(LoginFragment.TOKEN, null)

        if (token == null) {
            Toast.makeText(requireActivity(), "User Must Login", Toast.LENGTH_LONG).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.logout(token)
                if (resOk(resp)) {
                    editor?.putString(LoginFragment.ROLE, null)
                    editor?.putString(LoginFragment.TOKEN, null)
                    editor?.putBoolean(LoginFragment.REMEMBER, false)
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

    // TODO
    private fun requestDeleteAccount() {
        Toast.makeText(requireActivity(), "not yet implement", Toast.LENGTH_LONG).show()
    }
}