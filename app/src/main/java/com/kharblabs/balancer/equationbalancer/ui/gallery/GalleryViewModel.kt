package com.kharblabs.balancer.equationbalancer.ui.gallery

import android.text.SpannableStringBuilder
import androidx.core.graphics.toColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.PieEntry
import com.kharblabs.balancer.equationbalancer.chemicalPlant.ChemUtils
import com.kharblabs.balancer.equationbalancer.chemicalPlant.ElementColors
import com.kharblabs.balancer.equationbalancer.dataManagers.StringMakers

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
            else
            {
                massLive.value= " :    0.00  gm/mol"
                moleculeName.value=formatMoleculeName(s,"#2196F3".toColorInt())
            }
        }
    }
    fun formatMoleculeName(
        moleculeName: String,
        digitColor: Int? = null,
    ): SpannableStringBuilder {
        val result = SpannableStringBuilder()
        val regex = "(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)"
        val atoms = ChemUtils().elementParser(moleculeName)
        val colorMap= ElementColors().assignPastelColors(atoms?.map { it.name })

            //  result.append(stringMakers.converttoSpannable(ALS!![i], digitColor))

        result.append(stringMakers.convertToSpannable_newCheck(moleculeName,digitColor, colorMap))


        return result
    }
}