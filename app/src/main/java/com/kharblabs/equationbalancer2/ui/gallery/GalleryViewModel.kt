package com.kharblabs.equationbalancer2.ui.gallery

import android.text.SpannableStringBuilder
import androidx.core.graphics.toColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.PieEntry
import com.kharblabs.equationbalancer2.chemicalPlant.ChemUtils
import com.kharblabs.equationbalancer2.chemicalPlant.ElementColors
import com.kharblabs.equationbalancer2.dataManagers.StringMakers

class GalleryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    var chemUtils = ChemUtils()
    val stringMakers = StringMakers()
    var moleculeName = MutableLiveData<SpannableStringBuilder?>()
    var massLive=MutableLiveData<String>().apply { value="" }
    var downString= MutableLiveData<String>().apply { value="" }
    val text: LiveData<String> = _text
    var pieEntries = MutableLiveData<ArrayList<PieEntry>>()
    fun buttonClick(molecule : String)
    {   var s= molecule
        s = s.replace("\\+".toRegex(), "")
        s = s.replace(" ".toRegex(), "")
        if(s.isNotEmpty()) {
            val returnedMass = chemUtils.getMoleculeMass(s)
            if (returnedMass > 0.0) {
                val strDouble = String.format("%.2f", returnedMass);
                massLive.value= " :    $strDouble  gm/mol"
                moleculeName.value=formatMoleculeName(s,"#2196F3".toColorInt())
                pieEntries.value= chemUtils.getMoleculeMassPieChart(s) as ArrayList<PieEntry>?

            }
        }
    }
    fun formatMoleculeName(
        moleculeName: String,
        digitColor: Int? = null,
    ): SpannableStringBuilder {
        val result = SpannableStringBuilder()

        val colorMap= ElementColors().elementColors

            //  result.append(stringMakers.converttoSpannable(ALS!![i], digitColor))

        result.append(stringMakers.convertToSpannable_newCheck(moleculeName,digitColor, colorMap))


        return result
    }
}