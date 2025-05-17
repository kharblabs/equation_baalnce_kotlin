package com.kharblabs.equationbalancer2.otherUtils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kharblabs.equationbalancer2.ui.EquationBal.HomeFragment
import com.kharblabs.equationbalancer2.ui.gallery.GalleryFragment
import com.kharblabs.equationbalancer2.ui.molarMass.peridicTable
import com.kharblabs.equationbalancer2.ui.oxidation.Oxidation
import com.kharblabs.equationbalancer2.ui.search.SearchFragment

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