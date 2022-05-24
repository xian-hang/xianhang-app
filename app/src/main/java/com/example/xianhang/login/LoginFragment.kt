package com.example.xianhang.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.xianhang.R
import com.example.xianhang.databinding.FragmentLoginBinding
import com.example.xianhang.home.MainActivity
import com.example.xianhang.model.LoginUser
import com.example.xianhang.model.StudentId
import com.example.xianhang.network.Api
import com.example.xianhang.network.response.LoginResponse
import com.example.xianhang.rest.resOk
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = arguments?.getString(USER)
        if (userId != null) {
            binding.verifyEmail.text = resources.getString(R.string.verify_email, userId)
            binding.resent.text = resources.getString(R.string.resend)
            requestSent()
            binding.resent.setOnClickListener {
                requestResent()
            }
        }

        binding.login.setOnClickListener {
            requestLogin()
        }

        binding.newUser.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        binding.resend.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_resentFragment)
        }
    }

    private fun requestSent() {
        val userId = arguments?.getString(USER)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                println("userId = $userId")
                val resp = Api.retrofitService.resend(StudentId(userId!!))
                if (!resOk(context, resp)) {
                    println("email send not ok")
                }
            } catch (e: HttpException) {
                Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                // TODO: check connection
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    private fun requestResent() {
        val userId = arguments?.getString(USER)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                println("userId = $userId")
                val resp = Api.retrofitService.resend(StudentId(userId!!))
                if (resOk(context, resp)) {
                    Toast.makeText(context, "Resent", Toast.LENGTH_LONG).show()
                }
            } catch (e: HttpException) {
                Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                // TODO: check connection
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun requestLogin() {
        val userId = binding.userId.text.toString()
        val password = binding.password.text.toString()
        val rememberMe = binding.rememberMe.isChecked

        val sharePreferences = activity?.getSharedPreferences(LOGIN_PREF, Context.MODE_PRIVATE)
        val editor = sharePreferences?.edit()

        if (!checkData(userId, password))
            return

        val user = LoginUser(userId, password)
        CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar.visibility = View.VISIBLE
            println("post login request")
            try {
                val resp = Api.retrofitService.login(user)
                if (resOk(context, resp) && resp.role == 0) {
                    editor?.putInt(ID, resp.id!!)
                    editor?.putString(USER, userId)
                    editor?.putString(PASSWORD, password)
                    editor?.putInt(ROLE, resp.role)
                    editor?.putString(TOKEN, "Token " + resp.token)
                    editor?.putBoolean(REMEMBER, rememberMe)
                    editor?.apply()
                    startActivity(Intent(context, MainActivity::class.java))
                    activity?.finish()
                } else {
                    println("res not ok")
                }
            } catch (e: HttpException) {
                Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                // TODO: check connection
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun checkData(userId: String, password: String): Boolean {
        if (userId.isEmpty()) {
            Toast.makeText(context, "Please fill your Id", Toast.LENGTH_LONG).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(context, "Please fill your password", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    companion object {
        const val LOGIN_PREF = "login_preferences"
        const val ID = "id"
        const val USER = "userId"
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val ROLE = "user_role"
        const val TOKEN = "auth_token"
        const val REMEMBER = "remember_me"
    }
}
