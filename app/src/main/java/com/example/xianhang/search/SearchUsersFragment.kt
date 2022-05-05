package com.example.xianhang.search

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import com.example.xianhang.adapter.UserAdapter
import com.example.xianhang.databinding.FragmentSearchUsersBinding
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN

class SearchUsersFragment : Fragment() {

    private lateinit var binding: FragmentSearchUsersBinding
    private val viewModel: UsersViewModel by activityViewModels()
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchUsersBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.users.adapter = UserAdapter(context)

        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        token = sharedPreferences?.getString(TOKEN, null)

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query == null) return false
                if (token == null) {
                    Toast.makeText(context, "Please Login", Toast.LENGTH_LONG).show()
                    return false
                }
                binding.search.clearFocus()
                viewModel.setUser(context, token!!, query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        return binding.root
    }
}