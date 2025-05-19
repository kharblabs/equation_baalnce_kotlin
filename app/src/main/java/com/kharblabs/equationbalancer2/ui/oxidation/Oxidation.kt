package com.kharblabs.equationbalancer2.ui.oxidation

import android.animation.LayoutTransition
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.TextView.BufferType
import androidx.collection.emptyLongSet
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kharblabs.equationbalancer2.R
import com.kharblabs.equationbalancer2.chemicalPlant.OxidationStateCalculator
import com.kharblabs.equationbalancer2.chemicalPlant.element
import com.kharblabs.equationbalancer2.dataManagers.OxidationAdapter
import com.kharblabs.equationbalancer2.databinding.FragmentGalleryBinding
import com.kharblabs.equationbalancer2.databinding.FragmentOxidationBinding
import com.kharblabs.equationbalancer2.otherUtils.Animators
import com.kharblabs.equationbalancer2.ui.gallery.GalleryViewModel
import com.kharblabs.equationbalancer2.ui.oxidation.OxidationViewModel
import kotlin.collections.component1
import kotlin.collections.component2

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
            var textIn = binding.molecularFormulaTextInput.text.toString().replace("[","(").replace("]",")")

            val cleaned=textIn.replace(Regex("[^a-zA-Z0-9+\\-^()]"), "")
            if(cleaned.isEmpty())
            {
                binding.molecularFormulaTextInput.setError("No input")
                return@setOnClickListener
            }
            if (hasImproperParentheses(cleaned))
            {
                binding.molecularFormulaTextInput.setError("Improper Brackets")
                return@setOnClickListener
            }

            oxidationViewModel.buttonClick(cleaned)
        }
        oxidationViewModel.massLive.observe(viewLifecycleOwner)  {
            binding.oxyName.text=it
        }
       // oxidationViewModel.spannableResult.observe(viewLifecycleOwner) {
      //      binding.oxidationSpan.setText(it, BufferType.SPANNABLE)
      //  }
        val listView: ListView = view.findViewById(R.id.oxidList)
        oxidationViewModel.unKnownElement.observe(viewLifecycleOwner) {
            if (it)
                binding.oxidationError.visibility= View.VISIBLE
            else
                binding.oxidationError.visibility= View.GONE
        }
        oxidationViewModel.oxidationMap.observe(viewLifecycleOwner) {
            try {
                binding.oxidationResultMolecule.text=binding.molecularFormulaTextInput.text.toString()
                populateOxidationTable(binding.oxidationTable, it)
                buildOxidationDisplayViews(
                    requireContext(),
                    binding.oxidationContainer,
                    binding.molecularFormulaTextInput.text.toString(),
                    it
                )
            }
            catch (e: Exception)
            {

            }
        }
        oxidationViewModel.moleculeName.observe (viewLifecycleOwner){
            binding.oxidationResultMolecule.text=it
        }
        viewModel.oxidList.observe(viewLifecycleOwner, Observer { entries ->
            listView.adapter = OxidationAdapter(requireContext(), entries)
        })
        viewModel.residualChargeLive.observe(viewLifecycleOwner) {
            var s=""
            var textColor=binding.oxidationNetCharge.textColors.defaultColor
            if(it==0) {
                s = "0 , Neutral"

            }
            else if(it>0){
                s="+$it, Cation"
                textColor= Color.RED
            }
            else{
                s="$it, Anion"
                textColor=Color.BLUE
            }
            binding.oxidationNetCharge.text=s //+ viewModel.downString.value
            binding.oxidationNetCharge.setTextColor(textColor)
        }
        viewModel.isOrganic.observe(viewLifecycleOwner) {
            if (it)
                binding.organicError.visibility= View.VISIBLE
            else
                binding.organicError.visibility= View.GONE
        }

        binding.textViewExample.setOnClickListener { showExamples() }
        super.onViewCreated(view, savedInstanceState)
    }
    val unKnownElement =false
    fun showExamples() {
        val inputTextField=binding.molecularFormulaTextInput
        try {



            val elsa = arrayOf(
                "AgNO3",
                "Fe2O3",
                "C2H5OH",
                "H2O",
                "H2SO4",
                "Fe2(SO4)3",
                "K4Fe(CN)6",
                "KMnSO4"
            )

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Example Equation :")

            builder.setItems(elsa) { _, i ->
                try {
                    inputTextField.setText(elsa[i])
                    val bundle = Bundle().apply {
                        putString("eq", elsa[i])
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            builder.create().show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateOxidationTable(tableLayout: TableLayout, data: Map<String, Map<String, Int>>) {
        // Group elements by ion
        val ionToElements = mutableMapOf<String, MutableList<SpannableStringBuilder>>()

        data.forEach { (element, ionMap) ->
            ionMap.forEach { (ionName, oxState) ->
                val formatted = formatElementWithOxidation(element, oxState)
                ionToElements.getOrPut(ionName) { mutableListOf() }.add(formatted)
            }

        }

        tableLayout.removeAllViews()
        val tableRow = TableRow(tableLayout.context)

        val ionText = TextView(tableLayout.context).apply {
            text = "Ion"
            setPadding(24, 24, 24, 24)
            setTextColor(Color.BLACK)
            textSize = 20f
        }

        val elementsText = TextView(tableLayout.context).apply {
            // Join multiple elements with a comma and space
            text = "Element States"
            setPadding(24, 24, 24, 24)
            textSize = 20f
        }
        tableRow.addView(ionText)
        tableRow.addView(elementsText)
        tableLayout.addView(tableRow)
        for ((ionName, elements) in ionToElements) {
            val tableRow = TableRow(tableLayout.context)

            val ionText = TextView(tableLayout.context).apply {
                text = ionName
                setPadding(24, 24, 24, 24)
                setBackgroundColor(ionColors[ionName] ?: Color.LTGRAY)
                setTextColor(Color.BLACK)
                textSize = 18f
            }

            val elementsText = TextView(tableLayout.context).apply {
                // Join multiple elements with a comma and space
                val combined = elements.reduce { acc, s -> acc.append(", ").append(s) }
                text = combined
                setPadding(24, 24, 24, 24)
                textSize = 16f
            }

            tableRow.addView(ionText)
            tableRow.addView(elementsText)
            tableLayout.addView(tableRow)
        }
    }
    private val ionColors = mapOf(
        "Potassium" to Color.parseColor("#FFD54F"), // Amber
        "Nitrate" to Color.parseColor("#4FC3F7"),   // Light Blue
        "Ammonium" to Color.parseColor("#AED581")   // Light Green (example)
        // Add more ions and colors here
    )
    // Helper: formats element + oxidation state with oxidation as superscript
    private fun formatElementWithOxidation(element: String, ox: Int): SpannableStringBuilder {
        val sign = if (ox > 0) "+" else ""
        val oxString = "$sign$ox"
        val text = "$element$oxString"

        val spannable = SpannableStringBuilder(text)
        val start = element.length
        val end = text.length
        // Superscript oxidation number and make it smaller
        spannable.setSpan(SuperscriptSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(RelativeSizeSpan(0.7f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannable
    }

    fun buildOxidationDisplayViews(
        context: Context,
        container: LinearLayout,
        formula: String,
        oxidationMap: Map<String, Map<String, Int>>
    ){  data class ElementInfo(
        val element: String,
        val count: Int,
        val oxidationNumbers: List<Int>
    )

    val elementCounts = oxidationMap.keys
    val elements = elementCounts.map { element ->
        val oxMap = oxidationMap[element] ?: emptyMap()
        val values = oxMap.values.toSet().toList().sorted()
        ElementInfo(element, 0, values)
    }

    container.removeAllViews()
    container.orientation = LinearLayout.HORIZONTAL
    container.gravity = Gravity.CENTER
    container.layoutTransition = LayoutTransition().apply {
        enableTransitionType(LayoutTransition.APPEARING)
        setDuration(300)
    }

    val borderDrawable = ContextCompat.getDrawable(context, R.drawable.cell_border)

    elements.forEachIndexed { index, e ->

        // Build spannable with per-number color + superscript
        val spannable = SpannableStringBuilder()
        e.oxidationNumbers.forEachIndexed { i, num ->
            val oxStr = if (num == 0) "0" else "${if (num > 0) "+" else ""}$num"
            val start = spannable.length
            spannable.append(oxStr)
            //spannable.setSpan(SuperscriptSpan(), start, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            //spannable.setSpan(RelativeSizeSpan(0.7f), start, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val color = when {
                num > 0 -> Color.RED
                num < 0 -> Color.BLUE
                else -> Color.DKGRAY
            }
            spannable.setSpan(ForegroundColorSpan(color), start, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (i != e.oxidationNumbers.lastIndex) {
                spannable.append("/")
            }
        }

        val oxidationView = TextView(context).apply {
            text = spannable
            gravity = Gravity.CENTER
            setPadding(24, 24, 24, 24)
            textSize = 20f
            setTypeface(Typeface.MONOSPACE)
        }

        val symbolView = TextView(context).apply {
            text = e.element
            gravity = Gravity.CENTER
            setPadding(24, 24, 24, 24)
            textSize = 24f
            setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
        }

        val verticalCell = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(16, 8, 16, 8)
            background = borderDrawable
            addView(oxidationView)
            addView(symbolView)
        }

        animats.applyFadeInAnimation(verticalCell, delay = index * 80L)
        container.addView(verticalCell)
    }
    }
    val animats= Animators()
    fun hasImproperParentheses(input: String): Boolean {
        val stack = ArrayDeque<Char>()
        for (char in input) {
            when (char) {
                '(' -> stack.addLast(char)   // push
                ')' -> {
                    if (stack.isEmpty()) {
                        return true  // unmatched closing
                    } else {
                        stack.removeLast()  // pop
                    }
                }
            }
        }
        return stack.isNotEmpty()
    }
}