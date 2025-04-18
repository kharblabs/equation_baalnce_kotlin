package com.kharblabs.equationbalancer2.ui.EquationBal

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kharblabs.equationbalancer2.databinding.FragmentHomeBinding
import katex.hourglass.`in`.mathlib.MathView

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.displayText.observe(viewLifecycleOwner) {
            textView.text = it
        }
        val mathView : MathView= binding.textLatex
        homeViewModel.displayLatex.observe(viewLifecycleOwner){mathView.setDisplayText(it)}
        val editText: EditText = binding.editTextText
        editText.addTextChangedListener(object :TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
               homeViewModel.input_equation.value=s?.toString()
            }
        })
        val main_button : Button = binding.mainButton
        main_button.setOnClickListener{homeViewModel.onButtonClick()}
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}