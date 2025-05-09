package com.kharblabs.equationbalancer2.ui.oxidation

import android.text.SpannableStringBuilder
import androidx.core.graphics.toColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kharblabs.equationbalancer2.chemicalPlant.ChemUtils
import com.kharblabs.equationbalancer2.chemicalPlant.ElementColors
import com.kharblabs.equationbalancer2.chemicalPlant.OxidationStateCalculator
import com.kharblabs.equationbalancer2.dataManagers.StringMakers

class OxidationViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    var oxidationStateCalculator = OxidationStateCalculator()
    var chemUtils = ChemUtils()
    val stringMakers = StringMakers()
    var moleculeName = MutableLiveData<SpannableStringBuilder?>()
    var massLive=MutableLiveData<String>().apply { value="" }
    var downString= MutableLiveData<String>().apply { value="" }
    val text: LiveData<String> = _text

    fun buttonClick(molecule : String)
    {   var s= molecule
        s = s.replace("\\+".toRegex(), "")
        s = s.replace(" ".toRegex(), "")
        if(s.isNotEmpty()) {
            val returnedMass = oxidationStateCalculator.calculate(s)

                massLive.value= convertMapToString(returnedMass)

        }
    }
    fun convertMapToString(map: Map<String, Map<String, Int>>): String {
        return map.entries.joinToString(separator = "\n") { (outerKey, innerMap) ->
            "$outerKey: " + innerMap.entries.joinToString(separator = ", ") { (innerKey, value) ->
                "$innerKey=$value"
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