package com.kharblabs.balancer.equationbalancer.ui.search

import com.kharblabs.balancer.equationbalancer.R
import com.kharblabs.balancer.equationbalancer.dataManagers.SearchRecyclerAdapter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import com.kharblabs.balancer.equationbalancer.ChemistryApplication
import com.kharblabs.balancer.equationbalancer.dataManagers.SearchInEnum
import com.kharblabs.balancer.equationbalancer.search_files.*
import com.kharblabs.equationbalancer2.search_files.ChemistryRepository
import com.kharblabs.equationbalancer2.search_files.Reaction

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

// Assuming R.layout.fragment_searcher exists and contains the necessary views
// Assuming SearchRecyclerAdapter, pro_mode_menu, etc., exist

// Define MyApplication if not already defined, for repository instantiation
// This is a basic example; consider proper DI (Hilt, Koin) for larger apps.



class SearchFragment : Fragment() {
    private var searchJob: Job? = null
    private val TAG = "SearcherFragment"

    // ViewModel using KTX delegate and custom factory
    private lateinit var viewModel: SearchViewModel

    // UI Elements - use view binding or lateinit var
    private lateinit var editText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var radioGroup: RadioGroup
    private lateinit var progressBarSearch: ProgressBar // For search operation loading
    private lateinit var migrateButton: Button
    private lateinit var toolbar: Toolbar

    private lateinit var repository : ChemistryRepository
    private lateinit var recyclerAdaptor: SearchRecyclerAdapter // Your Kotlin adapter

    private var hasPurchased: Boolean = false
    private var searchType = SearchInEnum.BY_REAGENTS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  mfirebaseAnal = FirebaseAnalytics.getInstance(requireContext())

        // Load purchase status
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false) // Ensure this layout exists

        repository = (requireActivity().application as ChemistryApplication).repository
        editText = view.findViewById(R.id.search_text_input)
        recyclerView = view.findViewById(R.id.search_recycler_view)
        radioGroup = view.findViewById(R.id.radioGrp)

        setupRecyclerView()
       // setupAds()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupUIListeners()
    }
    private fun searchByProduct(query: String,searchString: String) {
        lifecycleScope.launch {
            repository.findReactionsByProduct(query).collect { reactions ->
                notifyRecycler(formatReactionsOutput(reactions, "reactions producing" ),searchString)
            }
        }
    }

    private fun searchByReagent(query: String,searchString: String) {
        lifecycleScope.launch {
            repository.findReactionsUsingReagent(query).collect { reactions ->
                notifyRecycler(formatReactionsOutput(reactions, "reactions using"),searchString)
            }
        }
    }
    private fun prepareSearch(s: String) : String
    {
        var newS= s.trim().lowercase()
        newS = newS.replace("+"," ").replace(Regex("\\s+"), " ")
        newS +="$"
        return newS
    }
    private fun queriedMolecules(s: String) : List<String>
    {   var newS= s.trim().lowercase()
        newS = newS.replace("+"," ").replace(Regex("\\s+"), " ")

        return newS.split(Regex("\\s+")).filter { it.isNotBlank() }

    }
    private fun setupToolbar() {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
            (activity as AppCompatActivity).supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                title = getString(R.string.search_reaction) // Ensure this string resource exists
            }
            setHasOptionsMenu(true) // Important for onOptionsItemSelected to be called in Fragment
        } else {
            Log.w(TAG, "Activity is not AppCompatActivity, cannot set support action bar.")
        }
    }

    private fun setupRecyclerView() {
        // Assuming SearchRecyclerAdapter is your Kotlin adapter
        recyclerAdaptor = SearchRecyclerAdapter(hasPurchased)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recyclerAdaptor
        }
    }

    private fun notifyRecycler(reactions: List<String>,query: String)
    {
        recyclerAdaptor.reactionList.clear()
        recyclerAdaptor.reactionList.addAll(reactions)
        recyclerAdaptor.queriedString= queriedMolecules(query) as MutableList<String>
        recyclerAdaptor.notifyDataSetChanged()
    }
    private fun formatReactionsOutput(
        reactions: List<Reaction>,
        contextText: String,

    ): List<String> {
        return if (reactions.isEmpty()) {
            listOf("No $contextText ")
        } else {
            val header = ""//"Found ${reactions.size} $contextText '$query':"
            val formattedReactions = reactions.map { reaction ->reaction.reactionString
            }
            listOf(header) + formattedReactions
        }
    }


    private fun setupUIListeners() {
        radioGroup.setOnCheckedChangeListener {group, checkedId ->
            val previos=searchType
            when (checkedId) {
                R.id.by_products_radiobtn -> {
                    searchType= SearchInEnum.BY_PRODUCTS
                }
                R.id.by_reagents_radiobtn -> {

                    searchType= SearchInEnum.BY_REAGENTS
                }
               }
            if(previos!=searchType)
            {
                val t=  editText.text.toString()
                editText.setText(t+" ")

            }
        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }
            override fun afterTextChanged(s: Editable?) {

                val query = s.toString().trim()
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(100) // debounce
                    if (query.isNotEmpty()) {
                        if (searchType == SearchInEnum.BY_REAGENTS)
                            searchByReagent(prepareSearch(query),query)
                        else
                            searchByProduct(prepareSearch(query),query)
                    }
                }

            }
        })


    }


    private fun setSearchUiEnabled(isEnabled: Boolean) {
        // Check if views are initialized before accessing them
        if (::editText.isInitialized) editText.isEnabled = isEnabled
        if (::radioGroup.isInitialized) {
            radioGroup.isEnabled = isEnabled
            for (i in 0 until radioGroup.childCount) {
                (radioGroup.getChildAt(i) as? View)?.isEnabled = isEnabled
            }
        }
        if (::migrateButton.isInitialized) migrateButton.isEnabled = isEnabled
    }




    fun inapp_prompt() {
        val shareds = Bundle()
        shareds.putString("scene", "SearcherFragment")
      //  mfirebaseAnal.logEvent("in_app_start", shareds)
        activity?.let {
       //     val i = Intent(it, pro_mode_menu::class.java) // Assuming pro_mode_menu is an Activity
       //     startActivity(i)
        }
    }

    fun log_tap() {
        val shareds = Bundle()
      //  mfirebaseAnal.logEvent("Copied_tap", shareds)
    }

    fun log_tap_pro(origin: String, eq: String) {
        val shareds = Bundle()
        shareds.putString("Origin", origin)
        shareds.putString("Eq", eq)
        //mfirebaseAnal.logEvent("Copied_pro", shareds)
    }

}

