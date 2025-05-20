package com.kharblabs.balancer.equationbalancer.ui.search

import androidx.lifecycle.ViewModel
// Or your desired package

import android.text.SpannableString
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for the SearcherFragment. Manages search state, executes searches
 * via the repository, and prepares data for the UI.
 */
class SearchViewModel() : ViewModel() {

    // StateFlow for the processed List<CharSequence> ready for the adapter
    private val _displayableResults = MutableStateFlow<List<CharSequence>>(emptyList())
    val displayableResults: StateFlow<List<CharSequence>> = _displayableResults.asStateFlow()

    // StateFlow for search operation loading state
    private val _isSearchLoading = MutableStateFlow(false)
    val isSearchLoading: StateFlow<Boolean> = _isSearchLoading.asStateFlow()

    // StateFlow for holding search-specific errors
    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError.asStateFlow()

    // Colors needed for SpannableString creation (set by Fragment)
    var colorReagentHighlight: Int = 0
    var colorProductHighlight: Int = 0
    var colorCoefficient: Int = 0



    /**
     * Helper to apply SpannableString formatting.
     */
    private fun processListForSpannables(
        stringList: List<String>,
        queryCompounds: List<String>,

        highlightColor: Int,
        coefficientColor: Int,
        outputList: MutableList<CharSequence>
    ) {
        for (str in stringList) {
            try {
                val spannableString = SpannableString(str)
             //*   SearchStringSpanner.m4712a(str, spannableString, queryCompounds, searchMode, highlightColor)
             //   SearchStringSpanner.m4710a(str, spannableString)
               // SearchStringSpanner.m4713b(str, spannableString, coefficientColor)
               // SearchStringSpanner.m4711a(str, spannableString, coefficientColor)
                outputList.add(spannableString)
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error creating spannable string for: $str", e)
                outputList.add(str) // Add raw string on error
            }
        }
    }

    fun clearSearchError() {
        _searchError.value = null
    }

}

// ViewModel Factory
