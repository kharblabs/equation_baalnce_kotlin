package com.kharblabs.equationbalancer2.ui.oxidation

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kharblabs.equationbalancer2.R
import com.kharblabs.equationbalancer2.dataManagers.OxidationAdapter
import com.kharblabs.equationbalancer2.databinding.FragmentGalleryBinding
import com.kharblabs.equationbalancer2.databinding.FragmentOxidationBinding
import com.kharblabs.equationbalancer2.ui.gallery.GalleryViewModel
import com.kharblabs.equationbalancer2.ui.oxidation.OxidationViewModel

class Oxidation : Fragment() {
    private var _binding: FragmentOxidationBinding? = null
    companion object {
        fun newInstance() = Oxidation()
    }
    private val binding get() = _binding!!
    lateinit var oxidationViewModel: OxidationViewModel
    private val viewModel: OxidationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        oxidationViewModel =
            ViewModelProvider(this)[OxidationViewModel::class.java]

        _binding = FragmentOxidationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.calculateMolecularOxyButton.setOnClickListener {
            //val s = if(binding.molecularFormulaTextInput.text.isEmpty())  binding.molecularFormulaTextInput.text.toString() else ""
            oxidationViewModel.buttonClick( binding.molecularFormulaTextInput.text.toString())
        }
        oxidationViewModel.massLive.observe(viewLifecycleOwner)  {
            binding.oxyName.text=it
        }
        val listView: ListView = view.findViewById(R.id.oxidList)

        viewModel.oxidList.observe(viewLifecycleOwner, Observer { entries ->
            listView.adapter = OxidationAdapter(requireContext(), entries)
        })
        super.onViewCreated(view, savedInstanceState)
    }
}