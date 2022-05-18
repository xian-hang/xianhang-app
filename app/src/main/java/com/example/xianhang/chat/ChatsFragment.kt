package com.example.xianhang.chat

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.xianhang.R
import com.example.xianhang.adapter.ChatAdapter
import com.example.xianhang.databinding.FragmentChatsBinding
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.ChatItem

const val CHAT = "chat"

class ChatsFragment : Fragment() {

    private lateinit var binding: FragmentChatsBinding
    private val viewModel: ChatsViewModel by viewModels()
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val adapter = ChatAdapter()
        adapter.setOnItemClickListener(object: ChatAdapter.OnItemClickListener {
            override fun onItemClick(chat: ChatItem) {
                val bundle = bundleOf(CHAT to chat.id)
                findNavController().navigate(R.id.action_chatsFragment_to_chatFragment, bundle)
            }
        })

        binding = FragmentChatsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.chats.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        token = sharedPreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(context, "Please login", Toast.LENGTH_LONG).show()
            return
        }

        viewModel.setChats(context, token!!)
    }
}