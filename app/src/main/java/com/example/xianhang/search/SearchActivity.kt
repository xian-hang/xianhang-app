package com.example.xianhang.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.xianhang.R
import com.example.xianhang.adapter.SearchAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val tab = findViewById<TabLayout>(R.id.tab)
        val viewpager = findViewById<ViewPager2>(R.id.view_pager)
        val adapter = SearchAdapter(supportFragmentManager, lifecycle)
        viewpager.adapter = adapter
        TabLayoutMediator(tab, viewpager) { tab, position ->
            when (position) {
                0 -> tab.text = "商品"
                1 -> tab.text = "用户"
            }
        }.attach()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.view_pager)

    }
}