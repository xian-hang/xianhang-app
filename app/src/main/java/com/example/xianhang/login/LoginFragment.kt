package com.example.xianhang.login

import android.annotation.SuppressLint
import android.content.Context
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
import com.example.xianhang.model.LoginUser
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class LoginFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = arguments?.getString(USER)
        if (userId != null) {
            val verifyText = view.findViewById<TextView>(R.id.verify_email)
            verifyText.text = resources.getString(R.string.verify_email, userId)
            val resent = view.findViewById<TextView>(R.id.resent)
            resent.text = resources.getString(R.string.resent)
            // TODO: resent clickable
        }

        val login = view.findViewById<Button>(R.id.login)
        login?.setOnClickListener {
            requestLogin(view)
        }

        val newUser = getView()?.findViewById<TextView>(R.id.new_user)
        newUser?.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun requestLogin(view: View) {
        val userId = view.findViewById<TextInputEditText>(R.id.userId).text.toString()
        val password = view.findViewById<TextInputEditText>(R.id.password).text.toString()
        val rememberMe = view.findViewById<CheckBox>(R.id.remember_me).isChecked

        val sharePreferences = activity?.getSharedPreferences(LOGIN_PREF, Context.MODE_PRIVATE)
        val editor = sharePreferences?.edit()

        if (!checkData(userId, password))
            return

        val user = LoginUser(userId, password)
        CoroutineScope(Dispatchers.Main).launch {
            println("post login request")
            try {
                val resp = Api.retrofitService.login(user)
                if (resOk(resp) && resp.role == 0) {
                    editor?.putInt(ID, resp.id)
                    editor?.putString(USER, userId)
                    editor?.putString(PASSWORD, password)
                    editor?.putInt(ROLE, resp.role)
                    editor?.putString(TOKEN, "Token " + resp.token)
                    editor?.putBoolean(REMEMBER, rememberMe)
                    editor?.apply()
                    findNavController().navigate(R.id.action_loginFragment_to_mainActivity)
                } else if (resp.role == 1) {
                    Toast.makeText(requireActivity(), "Admin User please use web login", Toast.LENGTH_LONG).show()
                } else {
                    val code = resp.code
                    val role = resp.role
                    Toast.makeText(requireActivity(), "code: $code, role: $role", Toast.LENGTH_LONG).show()
                }

            } catch (e: HttpException) {
                Toast.makeText(requireActivity(), e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    private fun checkData(userId: String, password: String): Boolean {
        if (userId.isEmpty()) {
            Toast.makeText(requireActivity(), "Please fill your Id", Toast.LENGTH_LONG).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(requireActivity(), "Please fill your password", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    companion object {
        const val LOGIN_PREF = "login_preferences"
        const val ID = "id"
        const val USER = "userId"
        const val PASSWORD = "password"
        const val ROLE = "user_role"
        const val TOKEN = "auth_token"
        const val REMEMBER = "remember_me"
    }
}
