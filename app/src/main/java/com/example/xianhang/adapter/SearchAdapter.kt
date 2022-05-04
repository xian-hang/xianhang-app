package com.example.xianhang.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.xianhang.search.SearchProductsFragment
import com.example.xianhang.search.SearchUsersFragment

class SearchAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SearchProductsFragment()
            1 -> SearchUsersFragment()
            else -> Fragment()
        }
    }
}