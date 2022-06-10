package com.example.xianhang.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.xianhang.databinding.FragmentResentBinding
import com.example.xianhang.model.StudentId
import com.example.xianhang.network.Api
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class ResentFragment : Fragment() {

    private lateinit var binding: FragmentResentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentResentBinding.inflate(inflater)
        binding.submit.setOnClickListener {
            requestResent()
        }
        return binding.root
    }

    private fun requestResent() {
        val userId = binding.userId.text.toString()
        if (userId.isEmpty()) {
            Toast.makeText(context, "Please fill your student Id", Toast.LENGTH_LONG).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            binding.progress.visibility = View.VISIBLE
            try {
                val resp = Api.retrofitService.resend(StudentId(userId))
                binding.progress.visibility = View.GONE
                Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            } catch (e: HttpException) {
                binding.progress.visibility = View.GONE
                Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                binding.progress.visibility = View.GONE
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}