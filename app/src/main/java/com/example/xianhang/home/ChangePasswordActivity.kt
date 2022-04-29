package com.example.xianhang.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.R
import com.example.xianhang.model.EditPassword
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val change = findViewById<Button>(R.id.change)
        change.setOnClickListener {
            showChangePasswordDialog()
        }
    }

    private fun showChangePasswordDialog() {
        val password = findViewById<TextInputEditText>(R.id.password).text.toString()
        val newPassword = findViewById<TextInputEditText>(R.id.new_password).text.toString()
        val confirmPassword = findViewById<TextInputEditText>(R.id.confirm_new_password).text.toString()

        if (!checkData(password, newPassword, confirmPassword))
            return

        val sharedPreferences = getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(this, "Please Login First", Toast.LENGTH_LONG).show()
            return
        }

        MaterialAlertDialogBuilder(this)
            .setMessage("确定更改密码吗？")
            .setPositiveButton("确认") { _, _ ->
                requestChangePassword()
            }
            .setNegativeButton("取消") { _, _ ->

            }
            .show()
    }

    private fun requestChangePassword() {
        val password = findViewById<TextInputEditText>(R.id.password).text.toString()
        val newPassword = findViewById<TextInputEditText>(R.id.new_password).text.toString()

        val sharedPreferences = getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences.getString(TOKEN, null)

        val data = EditPassword(password, newPassword)
        val that = this
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.editPassword(token!!, data)
                if (resOk(resp)) {
                    startActivity(Intent(that, MainActivity::class.java))
                } else {
                    Toast.makeText(that, "Failed to change", Toast.LENGTH_LONG).show()
                }
            } catch (e: HttpException) {
                Toast.makeText(that, e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(that, e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    private fun checkData(password: String, newPassword: String, confirmPassword: String): Boolean {
        if (newPassword.length < 8) {
            val errMsg = "password length must more than or equal 8\n"
            Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show()
            return false
        }

        if (newPassword != confirmPassword) {
            val errMsg = "password and confirm password are not same\n" +
                    "password: " + password +
                    "confirmPassword: " + confirmPassword
            Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show()
            return false
        }

        if (password == newPassword) {
            val errMsg = "password and new password must different\n"
            Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}