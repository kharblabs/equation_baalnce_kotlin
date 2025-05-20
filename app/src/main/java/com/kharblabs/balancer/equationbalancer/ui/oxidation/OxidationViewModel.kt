package com.kharblabs.balancer.equationbalancer.ui.oxidation

import android.text.SpannableStringBuilder
import androidx.core.graphics.toColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kharblabs.balancer.equationbalancer.chemicalPlant.ChemUtils
import com.kharblabs.balancer.equationbalancer.chemicalPlant.ElementColors
import com.kharblabs.balancer.equationbalancer.chemicalPlant.OxidationEntry
import com.kharblabs.balancer.equationbalancer.chemicalPlant.OxidationStateCalculator
import com.kharblabs.balancer.equationbalancer.dataManagers.StringMakers
import com.kharblabs.balancer.equationbalancer.otherUtils.StopwatchTimer

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
    val oxidList = MutableLiveData<List<OxidationEntry>>()
    val text: LiveData<String> = _text
    val spannableResult = MutableLiveData<SpannableStringBuilder>()
    val oxidationMap = MutableLiveData<Map<String, Map<String, Int>>>()
    val unKnownElement = MutableLiveData<Boolean>(false)
    val isOrganic = MutableLiveData<Boolean>(false)
    val residualChargeLive = MutableLiveData<Int>(0)
    val organicCheckList= listOf<String>( "CH3","CH2","C2H5","CHO","COOH","C2H4")
    fun buttonClick(molecule : String)
    {  val timer = StopwatchTimer()
        var s= molecule
        s = s.replace("\\+".toRegex(), "")
        s = s.replace(" ".toRegex(), "")
        if(s.isNotEmpty()) {
            val (returnedMass,residualCharge) = oxidationStateCalculator.calculate(s)
            unKnownElement.value= returnedMass.any { (_, innerMap) ->                innerMap.containsKey("--")            }
            isOrganic.value= organicCheckList.any { it in molecule }
            residualChargeLive.value=residualCharge
          //  val spannable = oxidationStateCalculator.buildOxidationDisplaySpannable(s, returnedMass)
                oxidationMap.value=returnedMass
                massLive.value= convertMapToString(returnedMass)
                oxidList.value= getEntries(returnedMass)
               moleculeName.value=formatMoleculeName(s,"#2196F3".toColorInt())
                downString.value= timer.stop().toString()+" ms"
               // spannableResult.value=spannable

        }
    }


    fun convertMapToString(map: Map<String, Map<String, Int>>): String {
        return map.entries.joinToString(separator = "\n") { (outerKey, innerMap) ->
            "$outerKey: " + innerMap.entries.joinToString(separator = ", ") { (innerKey, value) ->
                "$innerKey=$value"
            }
        }
    }
    fun getEntries(map: Map<String, Map<String, Int>>): List<OxidationEntry> {


        val result = mutableListOf<OxidationEntry>()
        for ((atom, groupMap) in map) {
            for ((group, state) in groupMap) {
                result.add(OxidationEntry(atom, group, state))
            }
        }


        return result
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