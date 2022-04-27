package com.example.xianhang

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
import com.example.xianhang.LoginFragment.Companion.USER
import com.example.xianhang.databinding.FragmentRegisterBinding
import com.example.xianhang.model.CreateUser
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.*

class RegisterFragment : Fragment() {

    val binding: FragmentRegisterBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val register = getView()?.findViewById<Button>(R.id.register)
        register?.setOnClickListener {
            println("clicked")
            requestRegister(view)
        }
    }

    private fun requestRegister(view: View) {
        val username = view.findViewById<EditText>(R.id.username).text.toString()
        val userId = view.findViewById<EditText>(R.id.userId).text.toString()
        val password = view.findViewById<EditText>(R.id.password).text.toString()
        val confirmPassword = view.findViewById<EditText>(R.id.confirm_password).text.toString()

        if (!checkData(username, userId, password, confirmPassword))
            return

        val user = CreateUser(username, userId, password)
        CoroutineScope(Dispatchers.Main).launch {
            println("post register request")
            try {
                val resp = Api.retrofitService.register(user)
                if (resOk(resp)) {
                    val bundle = bundleOf(USER to userId)
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment, bundle)
                } else {
                    Toast.makeText(requireActivity(), "Register Error", Toast.LENGTH_LONG).show()
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
