package com.kharblabs.equationbalancer2

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kharblabs.equationbalancer2.otherUtils.PeriodicAdapter

class PeriodicTable : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PeriodicAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_periodic_table)
        supportActionBar?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.prdTable)
        recyclerView.layoutManager = GridLayoutManager(this, 18)

        // Prepare a list of 118 elements with gaps if needed (empty elements for empty cells)

        val context = this
        adapter = PeriodicAdapter(context, isNightModeActive(context))
        recyclerView.adapter = adapter
    }

    fun isNightModeActive(context: Context): Boolean {
        return when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> {
                when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> true
                    Configuration.UI_MODE_NIGHT_NO,
                    Configuration.UI_MODE_NIGHT_UNDEFINED -> false

                    else -> false
                }
            }
        }
    }
}


