package com.example.xianhang.home

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.xianhang.R
import com.example.xianhang.network.WebSocketService
import com.example.xianhang.product.TO_PROFILE
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navbar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)

        val isProfile = intent?.extras?.getBoolean(TO_PROFILE)
        println(isProfile)
        val graph = navController.navInflater.inflate(R.navigation.main_nav_graph)
        if (isProfile != null) graph.setStartDestination(R.id.profileFragment)
        else graph.setStartDestination(R.id.homeFragment)
        navHostFragment.navController.graph = graph

        if (!isRunning(WebSocketService::class.java)) {
            val intent = Intent(applicationContext, WebSocketService::class.java)
            println("start service intent")
            startService(intent)
        }
    }

    private fun isRunning(serviceClass: Class<WebSocketService>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}