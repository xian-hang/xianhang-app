package com.example.xianhang.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.xianhang.PaidFragment
import com.example.xianhang.SentFragment
import com.example.xianhang.UnpaidFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UnpaidFragment()
            1 -> PaidFragment()
            2 -> SentFragment()
            else -> Fragment()
        }
    }
}