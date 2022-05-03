package com.example.xianhang.order

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.xianhang.R
import com.example.xianhang.adapter.ProductAdapter
import com.example.xianhang.adapter.USER_PRODUCT
import com.example.xianhang.databinding.FragmentUserBinding
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.UserId
import com.example.xianhang.network.Api
import com.example.xianhang.product.ProductsViewModel
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private val productsViewModel: ProductsViewModel by viewModels {
        val id = arguments?.getInt(ID)
        println("user id = $id")
        ProductsViewModel.Factory(USER_PRODUCT, id, null)
    }
    private val userViewModel: UserViewModel by viewModels()
    var userId: Int? = null
    var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.userViewModel = userViewModel
        binding.productsViewModel = productsViewModel
        binding.products.adapter = ProductAdapter(USER_PRODUCT, context)

        val id = arguments?.getInt(ID)
        println("user id = $id")
        userId = id

        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        token = sharedPreferences?.getString(TOKEN, null)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpProfile(view, userId!!)

        binding.like.setOnClickListener {
            like()
        }

        binding.unlike.setOnClickListener {
            like()
        }

        binding.follow.setOnClickListener {
            follow()
        }

        binding.report.setOnClickListener {
            // TODO: implement report
            report()
        }
    }

    private fun report() {

    }

    private fun like() {
        if (token == null) {
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_LONG).show()
            return
        }
        binding.userViewModel!!.setLike(context, token!!, userId)
    }

    private fun follow() {
        if (token == null) {
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_LONG).show()
            return
        }
        binding.userViewModel!!.setFollow(context, token!!, userId)
    }

    private fun setUpProfile(view: View, id: Int) {
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
                val resp = Api.retrofitService.getUser(token!!, id)
                if (resOk(resp)) {
                    progressBar.visibility = View.GONE
                    username.text = resp.username
                    userId.text = resp.userId
                    introduction.text = resp.introduction
                    details.text = resources.getString(
                        R.string.profile_details,
                        String.format("%.2f", resp.credit),
                        resp.likes,
                        resp.soldItem
                    )
                    binding.userViewModel!!.init(context, resp.likeId, resp.followId)
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
}