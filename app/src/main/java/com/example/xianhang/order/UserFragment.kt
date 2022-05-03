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
    private val viewModel: ProductsViewModel by viewModels {
        val id = arguments?.getInt(ID)
        println("user id = $id")
        ProductsViewModel.Factory(USER_PRODUCT, id, null)
    }
    private var userId: Int? = null
    private var likeId: Int? = null
    private var followId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.products.adapter = ProductAdapter(USER_PRODUCT, context)

        val id = arguments?.getInt(ID)
        println("user id = $id")
        userId = id!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpProfile(view, userId!!)

        binding.like.setOnClickListener {
            // TODO: implement unlike
            unlike()
        }

        binding.unlike.setOnClickListener {
            // TODO: implement like
            like()
        }

        binding.follow.setOnClickListener {
            // TODO: implement follow
            follow()
        }

        binding.report.setOnClickListener {
            // TODO: implement report
            report()
        }
    }

    private fun unlike() {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_LONG).show()
            return
        }

        binding.unlike.visibility = View.VISIBLE
        binding.like.visibility = View.GONE
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.unlike(token, likeId!!)
                if (resOk(resp)) {
                    Toast.makeText(requireActivity(), "unliked", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireActivity(), resp.message, Toast.LENGTH_LONG).show()
                }
            } catch (e: HttpException) {
                Toast.makeText(requireActivity(), e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun like() {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_LONG).show()
            return
        }

        binding.unlike.visibility = View.GONE
        binding.like.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.like(token, userId!!)
                if (resOk(resp)) {
                    Toast.makeText(requireActivity(), "liked", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireActivity(), resp.message, Toast.LENGTH_LONG).show()
                }
            } catch (e: HttpException) {
                Toast.makeText(requireActivity(), e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun follow() {
        val sharePreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharePreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_LONG).show()
            return
        }

        if (followId == null) {
            binding.follow.text = resources.getString(R.string.followed)
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val resp = Api.retrofitService.follow(token, userId!!)
                    if (resOk(resp)) {
                        Toast.makeText(requireActivity(), "followed", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireActivity(), resp.message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: HttpException) {
                    Toast.makeText(requireActivity(), e.message(), Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    // TODO: check connection wrong
                    Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        } else {
            binding.follow.text = resources.getString(R.string.follow)
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val resp = Api.retrofitService.unfollow(token, followId!!)
                    if (resOk(resp)) {
                        Toast.makeText(requireActivity(), "unfollowed", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireActivity(), resp.message, Toast.LENGTH_LONG).show()
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

    private fun report() {

    }

    private fun setUpProfile(view: View, id: Int) {
        val sharePreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharePreferences?.getString(TOKEN, null)

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
                val resp = Api.retrofitService.getUser(token, id)
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
                    likeId = resp.likeId
                    followId = resp.followId
                    binding.like.visibility = if (likeId != null) View.VISIBLE else View.GONE
                    binding.unlike.visibility = if (likeId == null) View.VISIBLE else View.GONE
                    binding.follow.text = if (followId == null) resources.getString(R.string.follow)
                    else resources.getString(R.string.followed)
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