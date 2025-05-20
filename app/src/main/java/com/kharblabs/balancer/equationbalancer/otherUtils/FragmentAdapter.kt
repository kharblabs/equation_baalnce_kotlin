package com.kharblabs.balancer.equationbalancer.otherUtils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kharblabs.balancer.equationbalancer.ui.EquationBal.HomeFragment
import com.kharblabs.balancer.equationbalancer.ui.gallery.GalleryFragment
import com.kharblabs.balancer.equationbalancer.ui.oxidation.Oxidation
import com.kharblabs.balancer.equationbalancer.ui.search.SearchFragment

class FragmentAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 4 // Number of fragments to swipe through

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> SearchFragment()
            2 -> GalleryFragment()
            3 -> Oxidation()
            else -> HomeFragment()
        }
    }
}