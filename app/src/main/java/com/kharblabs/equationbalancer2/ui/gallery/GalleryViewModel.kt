package com.kharblabs.equationbalancer2.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kharblabs.equationbalancer2.chemicalPlant.ChemUtils

class GalleryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    var chemUtils = ChemUtils()
    var moleculeName = MutableLiveData<String>().apply { value="" }
    var massLive=MutableLiveData<String>().apply { value="" }
    var downString= MutableLiveData<String>().apply { value="" }
    val text: LiveData<String> = _text

    fun buttonClick()
    {   var s=moleculeName.value
        s = s!!.replace("\\+".toRegex(), "")
        s = s.replace(" ".toRegex(), "")
        if(s.isNotEmpty()) {
            val returnedMass = chemUtils.getMoleculeMass(moleculeName.value)
            if (returnedMass > 0.0) {
                val strDouble = String.format("%.2f", returnedMass);
                massLive.value=" :    " + strDouble + "  gm/mol"
            }
        }
    }
}