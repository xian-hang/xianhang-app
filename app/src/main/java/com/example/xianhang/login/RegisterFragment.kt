package com.example.xianhang.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.xianhang.R
import com.example.xianhang.login.LoginFragment.Companion.USER
import com.example.xianhang.databinding.FragmentRegisterBinding
import com.example.xianhang.model.CreateUser
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.*

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.register.setOnClickListener {
            println("clicked")
            requestRegister(view)
        }
    }

    private fun requestRegister(view: View) {
        val username = binding.username.text.toString()
        val userId = binding.userId.text.toString()
        val password = binding.password.text.toString()
        val confirmPassword = binding.confirmPassword.text.toString()

        if (!checkData(username, userId, password, confirmPassword))
            return

        val user = CreateUser(username, userId, password)
        CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar.visibility = View.VISIBLE
            println("post register request")
            try {
                val resp = Api.retrofitService.register(user)
                if (resOk(context, resp)) {
                    binding.progressBar.visibility = View.GONE
                    val bundle = bundleOf(USER to userId)
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment, bundle)
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "Register Error", Toast.LENGTH_LONG).show()
                }
            } catch (e: HttpException) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                // TODO: check connection wrong
                binding.progressBar.visibility = View.GONE
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    private fun checkData(username: String,
                          userId: String,
                          password: String,
                          confirmPassword: String): Boolean {
        if (username.length < 4) {
            val errMsg = "username length must more than or equal 4\n"
            Toast.makeText(requireActivity(), errMsg, Toast.LENGTH_LONG).show()
            return false
        }

        if (password != confirmPassword) {
            val errMsg = "password and confirm password are not same\n" +
                    "password: " + password +
                    "confirmPassword: " + confirmPassword
            Toast.makeText(requireActivity(), errMsg, Toast.LENGTH_LONG).show()
            return false
        }

        if (password.length < 8) {
            val errMsg = "password length must more than or equal 8\n"
            Toast.makeText(requireActivity(), errMsg, Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}
