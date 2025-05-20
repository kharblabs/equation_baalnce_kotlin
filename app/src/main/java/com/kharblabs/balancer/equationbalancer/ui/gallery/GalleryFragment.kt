package com.kharblabs.balancer.equationbalancer.ui.gallery

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.kharblabs.balancer.equationbalancer.R
import com.kharblabs.balancer.equationbalancer.dataManagers.SearchRecyclerAdapter
import com.kharblabs.balancer.equationbalancer.dataManagers.TextHistoryManager
import com.kharblabs.balancer.equationbalancer.databinding.FragmentGalleryBinding
import com.kharblabs.balancer.equationbalancer.search_files.MoleculeArrays

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    val moleculeArrays = MoleculeArrays()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var galleryViewModel: GalleryViewModel
    val purchased =true
    private lateinit var searchRecyclerAdapter : SearchRecyclerAdapter

    override fun onCreateView(        inflater: LayoutInflater,        container: ViewGroup?,        savedInstanceState: Bundle?    ): View {

      galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.massresulNum
        val linearLayoutManager =  LinearLayoutManager(requireContext())
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        searchRecyclerAdapter= SearchRecyclerAdapter(purchased)
        binding.searchRecyclerView.setLayoutManager(linearLayoutManager)
        binding.searchRecyclerView.setAdapter(searchRecyclerAdapter);

        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.calculateMolecularMassButton.setOnClickListener {
            //val s = if(binding.molecularFormulaTextInput.text.isEmpty())  binding.molecularFormulaTextInput.text.toString() else ""
            galleryViewModel.buttonClick( binding.molecularFormulaTextInput.text.toString())
            binding.searchRecyclerView.setVisibility(GONE);
            binding.maschartContainer.setVisibility(View.VISIBLE);
            binding.masResContainer.visibility= View.VISIBLE

        }

        galleryViewModel.moleculeName.observe(viewLifecycleOwner)  {
            TextHistoryManager.addToHistory(requireContext(),"molecules_mass",it.toString())
            binding.massName.text=it
        }
        galleryViewModel.massLive.observe(viewLifecycleOwner) {
            binding.massresulNum.text=it
        }

        galleryViewModel.pieEntries.observe(viewLifecycleOwner) {
                setupChartView(it)
        }
        binding.molecularFormulaTextInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }
            override fun afterTextChanged(s: Editable?) {

                binding.maschartContainer.setVisibility(GONE);
                binding.masResContainer.visibility= View.GONE
                binding.searchRecyclerView.setVisibility(View.VISIBLE);
                val query = s.toString().trim()
                if(!query.isEmpty()){
                    val searchResults=moleculeArrays.partialMatches(query)
                    searchRecyclerAdapter= SearchRecyclerAdapter (purchased, getCharList(searchResults.formulas), getCharList(searchResults.names), getCharList(searchResults.masses),false);
                    searchRecyclerAdapter.notifyDataSetChanged();
                    binding.searchRecyclerView.setAdapter(searchRecyclerAdapter);
                }

            }
        })
        binding.btnHistory.setOnClickListener { showTextHistoryDialog(binding.molecularFormulaTextInput,"molecules_mass") }
    }
    fun Fragment.showTextHistoryDialog(
        editText: AutoCompleteTextView,
        historyType: String
    ) {
        val history = TextHistoryManager.getHistory(requireContext(), historyType)
        if (history.isEmpty()) {
            Toast.makeText(requireContext(), "No history", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Select from history")
            .setItems(history.toTypedArray()) { _, which ->
                editText.setText(history[which])
                editText.setSelection(editText.text.length) // move cursor to end
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Clear All") { _, _ ->
                TextHistoryManager.clearHistory(requireContext(), historyType)
            }
            .show()
    }
    fun getCharList(s: List<String>): List<CharSequence> = s
    private fun setupChartView(entries : ArrayList<PieEntry>) {
        val chart = binding.chartTest

        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.setExtraOffsets(5f, 10f, 5f, 5f)

        chart.dragDecelerationFrictionCoef = 0.95f

        chart.setDrawCenterText(false)
        chart.isDrawHoleEnabled = false
        chart.transparentCircleRadius = 0f
        chart.rotationAngle = 0f
        chart.isRotationEnabled = true
        chart.isHighlightPerTapEnabled = true

        val tv1: TextView =binding.textUnderPie

        val dataSet = PieDataSet(entries, "Distribution of mass").apply {
            setDrawIcons(false)
            sliceSpace = 3f
            iconsOffset = MPPointF(0f, 40f)
            selectionShift = 5f

            val colors = ArrayList<Int>()
            for (c in ColorTemplate.PASTEL_COLORS) {
                colors.add(c)
            }
            setColors(colors)
        }

        val legend = chart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(true)
        legend.xEntrySpace = 7f
        legend.yEntrySpace = 0f
        legend.yOffset = 0f
        legend.textColor = resources.getColor(R.color.text_color_primary)

        val data = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter(chart))
            setValueTextSize(11f)
            setValueTextColor(Color.WHITE)
        }

        chart.data = data
        chart.highlightValues(null)
        chart.invalidate()
        chart.visibility= View.VISIBLE
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}