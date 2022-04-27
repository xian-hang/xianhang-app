package com.example.xianhang

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.xianhang.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.LoginFragment.Companion.REMEMBER

class LoginActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rememberLogin()
        setContentView(R.layout.activity_login)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() && super.onSupportNavigateUp()
    }

    @SuppressLint("CommitPrefEdits")
    private fun rememberLogin() {
        val sharedPreferences = getSharedPreferences(LOGIN_PREF, Context.MODE_PRIVATE)
            ?: return

        val remember = sharedPreferences.getBoolean(REMEMBER, false)
        if (remember)
            startActivity(Intent(this, MainActivity::class.java))
    }
}