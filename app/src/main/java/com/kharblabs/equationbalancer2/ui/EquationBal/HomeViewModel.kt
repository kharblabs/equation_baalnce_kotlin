package com.kharblabs.equationbalancer2.ui.EquationBal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kharblabs.equationbalancer2.chemicalPlant.Balancer

class HomeViewModel : ViewModel() {

    val balancer : Balancer =Balancer()
    val input_equation = MutableLiveData<String>().apply { value="" }
    val displayText= MutableLiveData<String>()
    val displayLatex=MutableLiveData<String>()
   // val inputz_eq :LiveData<String> = _input_equation

    fun onButtonClick(){


        val t=  input_equation.value?.let {  balancer.balancer(it) }
        if (t != null) {
            displayText.value= t.simplyfystring
            displayLatex.value=t.LatexString
        }

    }
}