package com.kharblabs.balancer.equationbalancer.pro_mode


import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray

class PurchaseStore(context: Context) {
    private val prefs = context.getSharedPreferences("purchases", Context.MODE_PRIVATE)

    fun saveEntitlements(skus: Set<String>) {
        prefs.edit {
            putString("owned_skus", JSONArray(skus.toList()).toString())
        }
    }

    fun loadEntitlements(): Set<String> {
        val json = prefs.getString("owned_skus", "[]") ?: "[]"
        val array = JSONArray(json)
        return (0 until array.length()).map { array.getString(it) }.toSet()
    }

    fun isPremium(): Boolean =
        loadEntitlements().any { it in setOf("pro_mode_new_ones", "pro_mode_new_one_yearly", "inapp_full_further") }
}