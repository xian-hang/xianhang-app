package com.example.xianhang.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.xianhang.R
import com.example.xianhang.adapter.ChatAdapter
import com.example.xianhang.databinding.FragmentChatsBinding
import com.example.xianhang.model.ChatItem

const val CHAT = "chat"

class ChatsFragment : Fragment() {

    private lateinit var binding: FragmentChatsBinding
    // TODO: add ChatsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        // TODO: add viewModel on binding

        val adapter = ChatAdapter()
        adapter.setOnItemClickListener(object: ChatAdapter.OnItemClickListener {
            override fun onItemClick(chat: ChatItem) {
                val bundle = bundleOf(CHAT to chat.id)
                findNavController().navigate(R.id.action_chatsFragment_to_chatFragment, bundle)
            }
        })
        binding.chats.adapter = adapter

        return binding.root
    }
}