package com.example.xianhang.chat

import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.xianhang.adapter.ChatAdapter
import com.example.xianhang.databinding.FragmentChatsBinding
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.ChatItem
import com.example.xianhang.model.Message

const val CHAT = "chat"

class ChatsFragment : Fragment() {

    private lateinit var binding: FragmentChatsBinding
    private val viewModel: ChatsViewModel by viewModels()
    private var token: String? = null
    private lateinit var adapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.chatList.observe(this, observer())

        adapter = ChatAdapter(requireContext())
        binding.chats.adapter = adapter

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        token = sharedPreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(context, "Please login", Toast.LENGTH_LONG).show()
            return
        }
    }

    private fun observer(): Observer<List<ChatItem>> {
        return Observer { _ ->
            adapter.notifyDataSetChanged()
        }
    }
}
