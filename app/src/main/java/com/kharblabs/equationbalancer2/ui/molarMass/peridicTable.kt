package com.kharblabs.equationbalancer2.ui.molarMass

import android.content.Context
import android.content.res.Configuration
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.kharblabs.equationbalancer2.R
import com.kharblabs.equationbalancer2.databinding.FragmentGalleryBinding
import com.kharblabs.equationbalancer2.databinding.FragmentPeriodicTableBinding
import com.kharblabs.equationbalancer2.otherUtils.PeriodicTableAdapter
import com.kharblabs.equationbalancer2.otherUtils.StopwatchTimer
import com.kharblabs.equationbalancer2.ui.gallery.GalleryViewModel
import kotlinx.coroutines.launch

class peridicTable : Fragment() {
    private var _binding: FragmentPeriodicTableBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    // This property is only valid between onCreateView and
    // onDestroyView.

    companion object {
        fun newInstance() = peridicTable()
    }

    private val viewModel: PrdTableViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        timer.start()
        val periodictableViewModel =
            ViewModelProvider(this).get(PrdTableViewModel::class.java)

        _binding = FragmentPeriodicTableBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //periodicTableAdapter= new PeriodicTableAdapter(this, true,1);
        // periodTableLant.setAdapter(periodicTableAdapter);
        return root
    }
    val timer= StopwatchTimer()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val periodTable = binding.prdTable;
        var nm = isNightModeActive(requireContext());

        // int nm= getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        // periodTableLant=findViewById(R.id.prdTableLant);

        val periodicTableAdapter =  PeriodicTableAdapter(requireContext(), nm, 1);
        lifecycleScope.launch {
            periodicTableAdapter.initData()
            periodTable.adapter = periodicTableAdapter
        }

        view.post {
            binding.tableLogText.text=timer.stop().toString()+" ms"
        }
    }
}