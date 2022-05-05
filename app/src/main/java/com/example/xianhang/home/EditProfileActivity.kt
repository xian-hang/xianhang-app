package com.example.xianhang.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.xianhang.login.LoginFragment
import com.example.xianhang.R
import com.example.xianhang.model.EditUser
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        val etUsername = findViewById<TextInputEditText>(R.id.username)
        val username = intent.extras?.getString("username").toString()
        println("username: $username")
        etUsername.setText(username)

        val etIntroduction = findViewById<TextInputEditText>(R.id.introduction)
        val introduction = intent.extras?.getString("introduction").toString()
        println("introduction: $introduction")
        etIntroduction.setText(introduction)

        val change = findViewById<Button>(R.id.change)
        change.setOnClickListener {
            requestEditProfile()
        }
    }

    private fun requestEditProfile() {
        val username = findViewById<TextInputEditText>(R.id.username).text.toString()
        val introduction = findViewById<TextInputEditText>(R.id.introduction).text.toString()

        if (username.length < 4) {
            val errMsg = "username length must more than or equal 4\n"
            Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show()
            return
        }

        val sharedPreferences = getSharedPreferences(LoginFragment.LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences.getString(LoginFragment.TOKEN, null)

        if (token == null) {
            Toast.makeText(this, "Please Login First", Toast.LENGTH_LONG).show()
            return
        }

        val data = EditUser(username, introduction)
        val that = this
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.editProfile(token, data)
                if (resOk(that, resp)) {
                    startActivity(Intent(that, MainActivity::class.java))
                }
            } catch (e: HttpException) {
                Toast.makeText(that, e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(that, e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }
}