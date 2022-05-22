package com.example.xianhang.chat

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import com.example.xianhang.adapter.MessageAdapter
import com.example.xianhang.databinding.FragmentChatBinding
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.login.LoginFragment.Companion.USERNAME
import com.example.xianhang.network.AUTH
import com.example.xianhang.network.MESSAGE
import com.example.xianhang.network.webSocket
import okhttp3.*
import org.json.JSONObject

private const val WS_URL = "wss://xianhang.ga/ws/chat/"
private const val DISCONNECT = 1000

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var adapter: MessageAdapter
    // private lateinit var webSocket: WebSocket

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
        (activity as AppCompatActivity).supportActionBar?.title = arguments?.getString(USERNAME)

        // get arguments
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        id = arguments?.getInt(ID, 0)
        token = sharedPreferences?.getString(TOKEN, null)
        val myId = sharedPreferences?.getInt(ID, 0)

        // init messages
        viewModel.initMessages(id!!)

        // setup adapter
        adapter = MessageAdapter(context, myId!!)
        binding.messages.adapter = adapter

        // backup plan
        // initWebSocket()

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

    private fun observer(): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->
            if (listOfWorkInfo.isNullOrEmpty()) return@Observer
            println(listOfWorkInfo)
            for (workInfo in listOfWorkInfo) {
                if (workInfo.state.isFinished) {
                    println("workInfo finished")
                    val text = workInfo.outputData.getString(MESSAGE)
                    // showMessages(text)
                    println(workInfo.id)
                }
            }
        }
    }

    private fun showMessages(text: String?) {
        println("text = $text")
        if (!text.isNullOrEmpty()) {
            if (text[0] == '0') {
                println("setMessages")
                viewModel.setMessages(binding.messages, text.substring(1))
            } else if (text[0] == '1') {
                println("addMessage")
                viewModel.addMessages(binding.messages, adapter, text.substring(1))
            }
        }
    }

    private fun initWebSocket() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .addHeader(AUTH, token!!)
            .url(WS_URL)
            .build()

        webSocket = client.newWebSocket(request, object: WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)

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
        val json = JSONObject().apply {
            put("token", token)
            put("type", "fetchMessage")
            put("userId", id)
        }
        webSocket.send(json.toString())
    }

    override fun onDestroy() {
        super.onDestroy()

        // webSocket.cancel()
        // webSocket.close(DISCONNECT, "disconnect")
    }
}