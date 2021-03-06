package com.example.xianhang.chat

import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.xianhang.adapter.MessageAdapter
import com.example.xianhang.databinding.FragmentChatBinding
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.login.LoginFragment.Companion.USERNAME
import com.example.xianhang.model.ChatItem
import com.example.xianhang.model.Message
import com.example.xianhang.network.WebSocketService.Companion.read
import com.example.xianhang.network.webSocket
import org.json.JSONObject

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var adapter: MessageAdapter

    private var token: String? = null
    private var id: Int? = null

    private val viewModel: MessageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // set title
        var title = arguments?.getString(USERNAME)
        if (title == null) title = activity?.intent?.getStringExtra(USERNAME)
        (activity as AppCompatActivity).supportActionBar?.title = title

        // get arguments
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        id = arguments?.getInt(ID)
        if (id == null) id = activity?.intent?.getIntExtra(ID, 0)
        token = sharedPreferences?.getString(TOKEN, null)
        val myId = sharedPreferences?.getInt(ID, 0)

        // init messages
        viewModel.initMessages(id!!)
        viewModel.messages?.observe(this, observer())
        viewModel.chatList.observe(this, newObserver())

        // setup adapter
        adapter = MessageAdapter(context, myId!!)
        binding.messages.adapter = adapter

        // setup send button
        binding.send.setOnClickListener {
            if (checkText()) {
                val json = JSONObject().apply {
                    put("userId", id)
                    put("type", "newMessage")
                    put("message", binding.text.text)
                }
                webSocket.send(json.toString())
                binding.text.setText("")
            }
        }

        return binding.root
    }

    private fun observer(): Observer<MutableList<Message>> {
        return Observer { messages ->
            if (messages.isNotEmpty()) {
                adapter.notifyItemInserted(messages.size - 1)
                binding.messages.scrollToPosition(messages.size - 1)
            } else {
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun newObserver(): Observer<List<ChatItem>> {
        return Observer { _ ->
            viewModel.setChat(id!!, adapter, binding.messages)
        }
    }

    private fun checkText(): Boolean {
        if (binding.text.text.isEmpty()) {
            Toast.makeText(context, "Don't send empty message", Toast.LENGTH_LONG).show()
            return false
        }
        if (binding.text.text.length >= 150) {
            Toast.makeText(context, "Message length can't more than 150", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroyView() {
        val json = JSONObject().apply {
            put("type", "readMessage")
            put("userId", id)
        }
        webSocket.send(json.toString())
        read(id!!)

        super.onDestroyView()
    }
}