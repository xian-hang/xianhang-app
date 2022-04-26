package com.example.xianhang

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logout = view.findViewById<Button>(R.id.logout)
        logout.setOnClickListener {
            requestLogout()
        }
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
                    editor?.putString(LoginFragment.ROLE, "")
                    editor?.putString(LoginFragment.TOKEN, "")
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
                Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }

    }
}