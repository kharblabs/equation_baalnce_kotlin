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

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val homeFragment = HomeFragment()
    private lateinit var viewPager2: ViewPager2
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fragmentAdapter: FragmentAdapter
    private var activeFragment: Fragment = homeFragment
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
        bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_equation -> viewPager2.currentItem = 0

                R.id.nav_search -> viewPager2.currentItem = 1
                R.id.nav_gallery -> viewPager2.currentItem = 2

                R.id.nav_oxid_bottom -> viewPager2.currentItem = 3
                R.id.table -> viewPager2.currentItem = 4
            }
            true
        }

        // Sync ViewPager2 with BottomNavigationView when swiping between fragments
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // Sync the BottomNavigationView to reflect the current page
                when (position) {
                    0 -> bottomNav.selectedItemId = R.id.nav_equation
                    1 -> bottomNav.selectedItemId = R.id.nav_search
                    2 ->  bottomNav.selectedItemId =R.id.nav_gallery
                    3 ->  bottomNav.selectedItemId =R.id.nav_oxid_bottom
                    4 -> bottomNav.selectedItemId = R.id.table
                }
            }
        })
        val button = binding.settingsButton
        button.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)

        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewPager2.currentItem == 0) { // Home Fragment
                    if (doubleBackToExitPressedOnce) {
                        // If back pressed twice quickly, exit the app
                        finish()
                    } else {
                        // Show Toast message for the first press
                        doubleBackToExitPressedOnce = true
                        Toast.makeText(applicationContext, "Press back again to exit", Toast.LENGTH_SHORT).show()

                        // Reset doubleBackToExitPressedOnce after 2 seconds
                        Handler(Looper.getMainLooper()).postDelayed({
                            doubleBackToExitPressedOnce = false
                        }, 2000)
                    }
                } else {
                    // If not on Home Fragment, navigate to the Home Fragment
                    viewPager2.currentItem = 0
                    bottomNav.selectedItemId = R.id.nav_home
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
    override fun onBackPressed() {
        if (viewPager2.currentItem == 0) { // Home Fragment
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed() // Exit app if back pressed twice
                return
            }

            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()

            // Reset doubleBackToExitPressedOnce after 2 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                doubleBackToExitPressedOnce = false
            }, 2000)
        } else {
            // Navigate to Home Fragment if not on Home Fragment
            viewPager2.currentItem = 0
            bottomNav.selectedItemId = R.id.nav_home
        }
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                super.onBackPressed()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
}