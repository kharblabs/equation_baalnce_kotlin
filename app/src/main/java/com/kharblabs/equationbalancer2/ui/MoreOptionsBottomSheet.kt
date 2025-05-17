package com.kharblabs.equationbalancer2.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.card.MaterialCardView
import com.kharblabs.equationbalancer2.PeriodicTable
import com.kharblabs.equationbalancer2.R
import com.kharblabs.equationbalancer2.SettingsActivity

class MoreOptionsBottomSheet  : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<MaterialCardView>(R.id.sheet_settings).setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
            Toast.makeText(context, "Settings clicked", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        view.findViewById<MaterialCardView>(R.id.sheet_periodic_nav).setOnClickListener {
            startActivity(Intent(requireContext(), PeriodicTable::class.java))

            Toast.makeText(context, "periodic table clicked", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        view.findViewById<MaterialCardView>(R.id.sheet_share).setOnClickListener {
            Toast.makeText(context, "Share clicked", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        view.findViewById<MaterialCardView>(R.id.sheet_about_nav).setOnClickListener {
            Toast.makeText(context, "Share clicked", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }
}