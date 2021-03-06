package com.example.xianhang.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.xianhang.R
import com.example.xianhang.adapter.OrderStatusAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

const val POSITION = "position"

class OrderStatusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_status)

        val tab = findViewById<TabLayout>(R.id.tab)
        val viewpager = findViewById<ViewPager2>(R.id.view_pager)
        val adapter = OrderStatusAdapter(supportFragmentManager, lifecycle)
        viewpager.adapter = adapter
        TabLayoutMediator(tab, viewpager) { tab, position ->
            when (position) {
                0 -> tab.text = "待付款"
                1 -> tab.text = "待发货"
                2 -> tab.text = "待收货"
            }
        }.attach()
        val position = intent?.extras?.getInt(POSITION)
        tab.getTabAt(position!!)?.select()
    }

    override fun onBackPressed() {
        finish()
    }
}