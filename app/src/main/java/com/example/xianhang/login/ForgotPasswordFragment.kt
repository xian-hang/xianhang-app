package com.example.xianhang.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.xianhang.R
import com.example.xianhang.databinding.FragmentForgotPasswordBinding
import com.example.xianhang.model.StudentId
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class ForgotPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(inflater)
        binding.submit.setOnClickListener {
            requestForgotPassword()
        }
        return binding.root
    }

    private fun requestForgotPassword() {
        val userId = binding.userId.text.toString()
        if (userId.isEmpty()) {
            Toast.makeText(context, "Please fill your student Id", Toast.LENGTH_LONG).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.forgotPassword(StudentId(userId))
                Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            } catch (e: HttpException) {
                Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}