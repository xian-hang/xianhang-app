package com.example.xianhang.order

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.xianhang.R
import com.example.xianhang.databinding.FragmentPostageBinding
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.PostageReqeust
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class PostageFragment : Fragment() {

    private lateinit var binding: FragmentPostageBinding
    private val viewModel: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPostageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.confirm.setOnClickListener {
            setPostage()
        }
    }

    private fun setPostage() {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(context, "Please login", Toast.LENGTH_LONG).show()
            return
        }

        val text = binding.cost.text
        if (text.isEmpty()) {
            Toast.makeText(context, "please fill the postage", Toast.LENGTH_LONG).show()
            return
        }

        val postage = text.toString().toDouble()
        if (postage < 0) {
            Toast.makeText(context, "postage must more than or equal to 0", Toast.LENGTH_LONG).show()
            return
        }

        val id = viewModel.order.value!!.id!!
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.setPostage(token, id, PostageReqeust(postage))
                if (resOk(context, resp)) {
                    viewModel.setPostage(postage)
                    findNavController().popBackStack()
                }
            } catch (e: HttpException) {
                Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }
}