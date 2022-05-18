package com.example.xianhang.chat

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.xianhang.adapter.MessageAdapter
import com.example.xianhang.databinding.FragmentChatBinding
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.network.AUTH
import com.example.xianhang.network.WS_URL
import com.example.xianhang.network.httpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var webSocket: WebSocket
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

        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        id = arguments?.getInt(ID, 0)
        token = sharedPreferences?.getString(TOKEN, null)
        val myId = sharedPreferences?.getInt(ID, 0)
        adapter = MessageAdapter(context, myId!!)
        binding.messages.adapter = adapter

        // TODO: set username on title from pass data
        activity?.actionBar?.title = "username (TODO)"
        println("$myId $id")

        initWebSocket()
        binding.send.setOnClickListener {
            if (checkText()) {
                val json = JSONObject()
                json.put("userId", id)
                json.put("type", "newMessage")
                json.put("message", binding.text.text)
                webSocket.send(json.toString())
                binding.text.setText("")
            } else {
                Toast.makeText(context, "don't send empty message", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }

    private fun checkText(): Boolean {
        return binding.text.text.isNotEmpty() && binding.text.text.length <= 150
    }

    private fun initWebSocket() {
        val request = Request.Builder()
            .addHeader(AUTH, token!!)
            .url(WS_URL + "chat/")
            .build()

        webSocket = httpClient.newWebSocket(request, object: WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)

                val json = JSONObject()
                json.put("token", token)
                json.put("type", "fetchMessage")
                json.put("userId", id)
                webSocket.send(json.toString())

                activity?.runOnUiThread {
                    println("WebSocket connected")
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                activity?.runOnUiThread {
                    println("get $text")
                    if (text[0] == '0') {
                        viewModel.setMessages(binding.messages, text.substring(1))
                    } else if (text[0] == '1') {
                        viewModel.addMessages(binding.messages, adapter, text.substring(1))
                    }
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                activity?.runOnUiThread {
                    println("WebSocket is closing: $code / $reason")
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                activity?.runOnUiThread {
                    println("WebSocket closed: $code / $reason")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                activity?.runOnUiThread {
                    t.printStackTrace()
                    Toast.makeText(context, "ERR: ${t.message}", Toast.LENGTH_LONG).show()
                }
            }
        })
        httpClient.dispatcher.executorService.shutdown()
    }
}