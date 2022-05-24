package com.example.xianhang.product

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.xianhang.R
import com.example.xianhang.home.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

const val TO_PROFILE = "to profile"

class ProductActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navbar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.product_nav_graph)
        bottomNavigationView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(TO_PROFILE, true)
        startActivity(intent)
        finish()
    }
}