package com.kharblabs.balancer.equationbalancer.dataManagers

import android.content.Context
import org.json.JSONArray

object TextHistoryManager {
    private const val PREF_NAME = "text_history_prefs"
    private const val MAX_HISTORY = 6

    fun addToHistory(context: Context, historyType: String, newText: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val key = "history_$historyType"
        val history = getHistory(context, historyType).toMutableList()

        history.remove(newText) // remove duplicate
        history.add(newText)    // add to end

        val trimmed = history.takeLast(MAX_HISTORY)
        val jsonArray = JSONArray().apply {
            for (entry in trimmed) put(entry)
        }

        prefs.edit().putString(key, jsonArray.toString()).apply()
    }

    fun getHistory(context: Context, historyType: String): List<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val key = "history_$historyType"
        val jsonString = prefs.getString(key, "[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)

        return List(jsonArray.length()) { i -> jsonArray.getString(i) }
    }

    fun clearHistory(context: Context, historyType: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove("history_$historyType").apply()
    }
}