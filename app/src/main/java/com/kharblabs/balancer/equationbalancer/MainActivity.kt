package com.kharblabs.balancer.equationbalancer

import android.content.Context
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
import com.kharblabs.balancer.equationbalancer.otherUtils.FragmentAdapter
import com.kharblabs.balancer.equationbalancer.ui.EquationBal.HomeFragment

import androidx.activity.OnBackPressedCallback
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.kharblabs.balancer.equationbalancer.databinding.ActivityMainBinding
import com.kharblabs.balancer.equationbalancer.ui.MoreOptionsBottomSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val homeFragment = HomeFragment()
    private lateinit var viewPager2: ViewPager2
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fragmentAdapter: FragmentAdapter
    private var activeFragment: Fragment = homeFragment
    private lateinit var bannerAdView: AdView
    private var previousSelectedItemId: Int = R.id.nav_equation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AudienceNetworkAds.initialize(this)
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
        bannerAds()

    }
    fun bannerAds()
    {
        bannerAdView = AdView(applicationContext, "IMG_16_9_APP_INSTALL#303139454860984_303143871527209", AdSize.BANNER_HEIGHT_50)
        val adContainer = binding.bannerContainer
        adContainer.addView(bannerAdView)
        bannerAdView.loadAd()
    }
  suspend fun  getAAID(context: Context): String? = withContext(Dispatchers.IO) {
      try {
          val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
          if (!adInfo.isLimitAdTrackingEnabled) {
              adInfo.id // This is the AAID
          } else {
              null // User opted out of ad tracking
          }
      } catch (e: Exception) {
          e.printStackTrace()
          null
      }
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

    private fun bannerAda()
    {
        bannerAdView = AdView(this, "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID", AdSize.BANNER_HEIGHT_50)
        val adContainer = binding.bannerContainer
        adContainer.addView(bannerAdView)

    }

}