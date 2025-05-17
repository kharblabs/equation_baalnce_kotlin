package com.kharblabs.equationbalancer2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.navigation.ui.AppBarConfiguration

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kharblabs.equationbalancer2.databinding.ActivityMainBinding
import com.kharblabs.equationbalancer2.otherUtils.FragmentAdapter
import com.kharblabs.equationbalancer2.ui.EquationBal.HomeFragment

import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.kharblabs.equationbalancer2.ui.MoreOptionsBottomSheet

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val homeFragment = HomeFragment()
    private lateinit var viewPager2: ViewPager2
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fragmentAdapter: FragmentAdapter
    private var activeFragment: Fragment = homeFragment
    private var previousSelectedItemId: Int = R.id.nav_equation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNav
        //val navController = findNavController(R.id.nav_host_fragment_content_main)

        viewPager2 = findViewById(R.id.viewPager2)
        bottomNav =  binding.bottomNav

        // Initialize Adapter for ViewPager2
        fragmentAdapter = FragmentAdapter(this)
        viewPager2.adapter = fragmentAdapter

        // Sync BottomNavigationView with ViewPager2
        bottomNav.setOnItemSelectedListener  { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_equation -> {
                    viewPager2.currentItem = 0
                    previousSelectedItemId = R.id.nav_equation
                    true
                }
                R.id.nav_search -> {
                    viewPager2.currentItem = 1
                    previousSelectedItemId = R.id.nav_search
                    true
                }
                R.id.nav_gallery -> {
                    viewPager2.currentItem = 2
                    previousSelectedItemId = R.id.nav_gallery
                    true
                }
                R.id.nav_oxid_bottom -> {
                    viewPager2.currentItem = 3
                    previousSelectedItemId = R.id.nav_oxid_bottom
                    true
                }
                R.id.nav_more -> {
                    // Show BottomSheet
                    MoreOptionsBottomSheet().show(supportFragmentManager, "MoreOptions")

                    // Reset selection back immediately so "More" doesn't stay selected
                    bottomNav.post {
                        bottomNav.selectedItemId = previousSelectedItemId
                    }
                    true
                }
                else -> false
            }
        }

        // Sync ViewPager2 with BottomNavigationView when swiping between fragments
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val id = when (position) {
                    0 -> R.id.nav_equation
                    1 -> R.id.nav_search
                    2 -> R.id.nav_gallery
                    3 -> R.id.nav_oxid_bottom
                    else -> previousSelectedItemId
                }
                previousSelectedItemId = id
                bottomNav.selectedItemId = id
            }
        })
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    showExitConfirmationDialog() // or finishAffinity() if you want to close the whole task
                } else {
                    doubleBackToExitPressedOnce = true
                    Toast.makeText(this@MainActivity, "Press back again to exit", Toast.LENGTH_SHORT).show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        doubleBackToExitPressedOnce = false
                    }, 2000) // Reset flag after 2 seconds
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.main, menu)
        return true
    }


    private fun switchFragment(target: Fragment) {
        if (target != activeFragment) {
            supportFragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(target)
                .commit()
            activeFragment = target
        }
    }
    private var doubleBackToExitPressedOnce = false


    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
}