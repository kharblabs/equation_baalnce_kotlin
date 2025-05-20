package com.kharblabs.balancer.equationbalancer.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.card.MaterialCardView
import com.kharblabs.balancer.equationbalancer.PeriodicTable
import com.kharblabs.balancer.equationbalancer.R
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.kharblabs.balancer.equationbalancer.AboutActivity
import androidx.core.net.toUri
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener

class MoreOptionsBottomSheet  : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet, container, false)
    }
    private var billingClient:BillingClient?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<MaterialCardView>(R.id.sheet_about_nav).setOnClickListener {

            startActivity(Intent(requireContext(), AboutActivity::class.java))
            Toast.makeText(context, "About", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        view.findViewById<MaterialCardView>(R.id.sheet_periodic_nav).setOnClickListener {
            startActivity(Intent(requireContext(), PeriodicTable::class.java))

            Toast.makeText(context, "periodic table clicked", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        view.findViewById<MaterialCardView>(R.id.sheet_share).setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Best App for chemistry calculations. Equation Balancer \n Link: https://play.google.com/store/apps/details?id=${requireContext().packageName}"
                )
                type = "text/plain"
            }
            startActivity(sendIntent)

            Toast.makeText(context, "Share clicked", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        view.findViewById<MaterialCardView>(R.id.sheet_night).setOnClickListener {
            toggleNightMode(requireContext())
            dismiss()
        }
        view.findViewById<MaterialCardView>(R.id.sheet_request_feature).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:kharblabs.helper@gmail.com".toUri()
                putExtra(Intent.EXTRA_EMAIL, arrayOf("kharblabs.helper@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Feature Request")
                putExtra(Intent.EXTRA_TEXT, "I'd like to have these features added to the app :")
            }
            startActivity(Intent.createChooser(intent, "Send Feature list Email"))
            dismiss()
        }
        view.findViewById<MaterialCardView>(R.id.sheet_get_pro).setOnClickListener {
            toggleNightMode(requireContext())
            dismiss()
        }
    }
    fun toggleNightMode(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val currentMode = prefs.getInt("night_mode_mode", AppCompatDelegate.MODE_NIGHT_NO)

        val newMode = when (currentMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_YES
        }

        AppCompatDelegate.setDefaultNightMode(newMode)
        prefs.edit() { putInt("night_mode_mode", newMode) }
    }
    private var purchasesUpdatedListener : PurchasesUpdatedListener? = null
    fun pro_mode_helper()
    {

    }
}