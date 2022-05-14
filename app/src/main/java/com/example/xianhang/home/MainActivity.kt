package com.example.xianhang.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.xianhang.R
import com.example.xianhang.product.TO_PROFILE
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navbar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)

        val isProfile = intent?.extras?.getBoolean(TO_PROFILE)
        if (isProfile != null) {
            val graph = navController.navInflater.inflate(R.navigation.main_nav_graph)
            graph.setStartDestination(R.id.profileFragment)
            navHostFragment.navController.graph = graph
        }
    }
}