package com.example.xianhang

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController

class LoginActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val sharedPreferences = getSharedPreferences(Companion.LOGIN_PREF, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
    }

    companion object {
        const val LOGIN_PREF = "login_preferences"
    }
}