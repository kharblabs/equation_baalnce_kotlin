package com.kharblabs.equationbalancer2.ui.gallery

import android.R.attr.data
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.kharblabs.equationbalancer2.R
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

        galleryViewModel.pieEntries.observe(viewLifecycleOwner) {
                setupChartView(it)
        }
    }

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