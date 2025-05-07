package com.kharblabs.equationbalancer2.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kharblabs.equationbalancer2.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var galleryViewModel: GalleryViewModel
    override fun onCreateView(        inflater: LayoutInflater,        container: ViewGroup?,        savedInstanceState: Bundle?    ): View {

      galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.massresulNum



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.calculateMolecularMassButton.setOnClickListener {
            //val s = if(binding.molecularFormulaTextInput.text.isEmpty())  binding.molecularFormulaTextInput.text.toString() else ""
            galleryViewModel.buttonClick( binding.molecularFormulaTextInput.text.toString())
        }
        galleryViewModel.moleculeName.observe(viewLifecycleOwner)  {
            binding.massName.text=it
        }
        galleryViewModel.massLive.observe(viewLifecycleOwner) {
            binding.massresulNum.text=it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}