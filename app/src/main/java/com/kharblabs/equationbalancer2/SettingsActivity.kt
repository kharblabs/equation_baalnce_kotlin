package com.kharblabs.equationbalancer2

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.commit
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroup
import androidx.preference.SwitchPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kharblabs.equationbalancer2.databinding.SettingsActivityBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: SettingsActivityBinding // Assumed binding class name
    private lateinit var activityJob: Job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        activityJob = SupervisorJob() // Or use lifecycleScope directly

        // Inflate view using ViewBinding
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root) // XML: @+id/root

        // Load the SettingsFragment only once
        if (savedInstanceState == null) {
            supportFragmentManager.commit { // Kotlin KTX for FragmentTransaction
                replace(android.R.id.list_container, SettingsFragment()) // XML: @+id/settings
                // 'null' tag, reordering allowed ('false' for last arg in Java)
            }
        }
        binding.upgrade.text = AppStrings.UPGRADE_BUTTON_TEXT // E0.f11370a.f11312n2
        binding.upgrade.visibility = View.VISIBLE // Consider logic if upgrade isn't always available
        // N3 -> OnClickListener
        binding.upgrade.setOnClickListener { /* Handle upgrade click */ }

        // Set page title
        binding.pageName.text = AppStrings.SETTINGS_TITLE // E0.f11370a.f11333t

        // Adjust padding based on top image height after layout
        val verticalPadding = resources.getDimension(R.dimen.page_vertical_icon_padding)
        WindowCompat.setDecorFitsSystemWindows(window, false) // Example for edge-to-edge if needed
        // Set status bar color/icons if needed based on theme




        // Setup Back button visibility and listener
        binding.backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }


        // Setup Reset button listener
        // N3 -> OnClickListener
        binding.resetButton.setOnClickListener {
            // Show confirmation dialog for resetting settings
            // On confirm: Reset relevant SharedPreferences, potentially recreate activity
        }

        // Optional: Setup Toolbar if using the example layout

    }
    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
    class SettingsFragment : PreferenceFragmentCompat() {


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            findPreference<PreferenceCategory>("cat_app")?.title = AppStrings.PREF_CAT_APP_TITLE // E0.f11370a.f11296j2
            findPreference<PreferenceCategory>("cat_header")?.title = AppStrings.PREF_CAT_HEADER_TITLE
            findPreference<PreferenceCategory>("cat_data")?.title = AppStrings.PREF_CAT_DATA_TITLE
            findPreference<PreferenceCategory>("cat_info")?.title = AppStrings.PREF_CAT_INFO_TITLE
            findPreference<ListPreference>("theme")?.apply {
                title = AppStrings.PREF_THEME_TITLE // E0.f11370a.f11217Q1
                entries = arrayOf(AppStrings.PREF_THEME_ENTRY_LIGHT, AppStrings.PREF_THEME_ENTRY_DARK, AppStrings.PREF_THEME_ENTRY_SYSTEM)
                // Set icon based on current value
                when (value) {
                    "Light" -> setIcon(R.drawable.ic_theme_light)
                    "Dark" -> setIcon(R.drawable.ic_theme_dark)
                    else -> setIcon(R.drawable.ic_theme_auto)
                }
                setOnPreferenceChangeListener { preference, newValue ->
                    // Handle theme change - R3.f11550c = true likely triggers activity recreate
                 //   R3.themeChanged = true // Assumed variable name
                    activity?.recreate() // Standard way to apply theme change
                    true
                }
            }

            // Reset Intro Preference
            findPreference<Preference>("intro_reset")?.apply {
                title = AppStrings.PREF_INTRO_RESET_TITLE // E0.f11370a.f11263b2
                // P3 -> OnPreferenceClickListener
                setOnPreferenceClickListener {
                    // Show confirmation dialog
                    // On confirmation:
                    // val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    // prefs.edit().putBoolean("intro_completed_key", false).apply() // "intro" key
                    // Show toast/snackbar confirming reset
                    true // Indicate click was handled
                }
            }

            // References Preference (Showing a Dialog)
            findPreference<Preference>("reference")?.apply {
                title = AppStrings.PREF_REFERENCE_TITLE // E0.f11370a.f11288h2
                // C0828a -> OnPreferenceClickListener
                setOnPreferenceClickListener {
                    // Build the reference string (as in Java code)
                    val referencesText = "buildReferencesString()" // Helper function needed

                     MaterialAlertDialogBuilder(requireContext())
                        .setTitle(title)
                        .setMessage(referencesText)
                        .setPositiveButton(android.R.string.ok, null)
                        .show()
                    true
                }
            }

            // Review App Preference
            findPreference<Preference>("review")?.apply {
                title = AppStrings.PREF_REVIEW_TITLE // E0.f11370a.f11280f2
                summary = AppStrings.PREF_REVIEW_SUMMARY // E0.f11370a.f11284g2
                // P3 -> OnPreferenceClickListener
                setOnPreferenceClickListener {
                    // Launch Google Play Store intent for review
                    // Use Play Core library for in-app review if possible
                    true
                }
            }

            // ... (Configure acknowledge, apps, hide, language, keyboard, mass, decimal, percent, header_form, length, header_name, sort, direction similarly) ...
            findPreference<ListPreference>("language")?.apply {
                title = AppStrings.PREF_LANGUAGE_TITLE // E0.f11370a.f11268c2
                // Setup entries/values, set listener to trigger language change (likely involves R3.languageChanged = true and activity recreate)
            }



            findPreference<SwitchPreference>("length")?.apply {
                title = AppStrings.PREF_LENGTH_LIMIT_TITLE // E0.f11370a.f11229T1
                // Listener might just update R3 flag or directly save pref
            }


            // Apply themed icons to all loaded preferences
        }
    }
    companion object {
        // Recursively apply theme tint to preference icons
        // Renamed from static f() in Java inner class 'a'
        fun applyThemeToPreferenceIcons(preference: Preference) {
            if (preference is PreferenceGroup) {
                for (i in 0 until preference.preferenceCount) {
                    preference.getPreference(i).let { applyThemeToPreferenceIcons(it) }
                }
                return
            }

            // Apply tint to individual preference icon

        }
    }
}
object ThemeUtils {
    fun initialize() { /*...*/ }
    fun getIconColor(context: Context): Int = ContextCompat.getColor(context, R.color.text_secondary) // Example
}

object AppStrings {
    // Define all string constants used (e.g., PREF_TITLE_RANDOM, SETTINGS_TITLE, etc.)
    const val PREF_TITLE_RANDOM = "Random Header Icon" // Example
    const val PREF_SUMMARY_RANDOM = "Show a random dice on startup" // Example
    // ... other string constants ...
    const val PREF_CAT_APP_TITLE = "Application"
    const val PREF_CAT_HEADER_TITLE = "Header"
    const val PREF_CAT_DATA_TITLE = "Data"
    const val PREF_CAT_INFO_TITLE = "Information"
    const val PREF_THEME_TITLE = "Theme"
    const val PREF_THEME_SUMMARY = "Select the app theme"
    const val PREF_THEME_ENTRY_LIGHT = "Light"
    const val PREF_THEME_ENTRY_DARK = "Dark"
    const val PREF_THEME_ENTRY_SYSTEM = "System Default"
    const val PREF_INTRO_RESET_TITLE = "Reset Tutorial"
    const val PREF_REFERENCE_TITLE = "References & Acknowledgements"
    const val PREF_REVIEW_TITLE = "Rate Application"
    const val PREF_REVIEW_SUMMARY = "Leave a review on the Play Store"
    const val PREF_ACKNOWLEDGE_TITLE = "Acknowledgements"
    const val PREF_APPS_TITLE = "More Apps"
    const val PREF_HIDE_DATA_TITLE = "Hide Data Fields"
    const val PREF_HIDE_DATA_SUMMARY = "Select fields to hide on Page 4"
    const val PREF_LANGUAGE_TITLE = "Language"
    const val PREF_LANGUAGE_SUMMARY = "Select app display language"
    const val PREF_LANG_ENTRY_SYSTEM = "System Default" // Add others
    const val PREF_KEYBOARD_TITLE = "Keyboard Layout"
    const val PREF_KEYBOARD_SUMMARY = "Select default keyboard layout"
    const val PREF_MASS_TITLE = "Atomic Mass Source"
    const val PREF_MASS_SUMMARY = "Select source for atomic masses"
    const val PREF_DECIMAL_TITLE = "Molar Mass Decimals"
    const val PREF_PERCENT_DECIMAL_TITLE = "Percentage Decimals"
    const val PREF_HEADER_FORMULA_TITLE = "Use Parsed Formula in Header"
    const val PREF_LENGTH_LIMIT_TITLE = "Allow Unlimited Header Text Length"
    const val PREF_HEADER_NAME_TITLE = "Preferred Chemical Name"
    const val PREF_HEADER_NAME_SUMMARY = "Select source for header name"
    const val PREF_NAME_ENTRY_COMMON = "Common Name"
    const val PREF_NAME_ENTRY_PUBCHEM = "PubChem Preferred"
    // Add IUPAC
    const val PREF_SORT_TITLE = "History Sort By"
    const val PREF_SORT_SUMMARY = "Choose how to sort history"
    const val PREF_SORT_ENTRY_MASS = "Molar Mass"
    const val PREF_SORT_ENTRY_NAME = "Name"
    const val PREF_SORT_ENTRY_SYMBOL = "Formula"
    const val PREF_DIRECTION_TITLE = "Sort Ascending"
    const val UPGRADE_BUTTON_TEXT = "Upgrade"
    const val SETTINGS_TITLE = "Settings"
    const val REF_DESC_PUBCHEM = "Primary Chemical Data Source"
    const val REF_DESC_WLONK = "Element Data Source"
    // ... other reference descriptions ...
}

object TranslationState {
    val isTranslatingLiveData = androidx.lifecycle.MutableLiveData<Boolean>(false) // Example
}

object UiUtils {
    fun isNavigationRailActive(context: Context): Boolean = false // Example implementation
}

object WindowUtils {
    fun setLightStatusBar(window: android.view.Window, isLight: Boolean) { /* ... */ }
}

object ViewTreeObserverUtils {
    fun addOnGlobalLayoutListenerOnce(view: View, listener: android.view.ViewTreeObserver.OnGlobalLayoutListener) {
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }
}

object CoroutineUtils {
    fun createParentJob() = SupervisorJob()
}

// Replace with actual listener implementations if needed
class ThemePreferenceChangeListener(val id: Int) : Preference.OnPreferenceChangeListener {
    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean { TODO() }
}