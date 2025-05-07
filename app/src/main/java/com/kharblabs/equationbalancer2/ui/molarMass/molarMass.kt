package com.kharblabs.equationbalancer2.ui.molarMass

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kharblabs.equationbalancer2.R
import com.kharblabs.equationbalancer2.databinding.FragmentHomeBinding

class molarMass : Fragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.

    companion object {
        fun newInstance() = molarMass()
    }

    private val viewModel: MolarMassViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_molar_mass2, container, false)

    }
}