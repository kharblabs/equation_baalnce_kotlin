package com.kharblabs.equationbalancer2.ui.EquationBal

import android.text.SpannableStringBuilder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.kharblabs.equationbalancer2.chemicalPlant.Balancer
import com.kharblabs.equationbalancer2.chemicalPlant.ChemUtils
import com.kharblabs.equationbalancer2.chemicalPlant.ConstantValues
import com.kharblabs.equationbalancer2.chemicalPlant.Stochiometry
import com.kharblabs.equationbalancer2.chemicalPlant.ThreadResult
import com.kharblabs.equationbalancer2.dataManagers.MoleculeEntry
import com.kharblabs.equationbalancer2.otherUtils.StopwatchTimer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.apply

class HomeViewModel : ViewModel() {

    val balancer : Balancer =Balancer()
    val chemUtils: ChemUtils = ChemUtils()
    val input_equation = MutableLiveData<String>().apply { value="H2+O2=H2O" }
    val displayText= MutableLiveData<SpannableStringBuilder?>()
    val textToInsert=MutableLiveData<String>()
    val displayErrorString=MutableLiveData<String>()
    val reactantList= MutableLiveData< ArrayList<MoleculeEntry>>()
    var productList= MutableLiveData<ArrayList<MoleculeEntry>>()
    val displayLatex=MutableLiveData<String>()
    val balancedAllGood= MutableLiveData<Boolean>()
    val solverResult = MutableLiveData<IntArray?>()
    private val _numbers = MutableLiveData<List<MoleculeEntry>>(emptyList())
    val limitingName=MutableLiveData<String>().apply { value="" }
    val stochiometry = Stochiometry()
    var isAnyInvalidElement= false
    var reactass = ConstantValues().reactionWithTherm
    lateinit var csvData: MutableList<Array<String>>
    val thermalText = MutableLiveData<String>("")
    val extaradata = MutableLiveData<String>("")
    val isLoading = MutableLiveData<Boolean>(false)
   // val inputz_eq :LiveData<String> = _input_equation
    val timer = StopwatchTimer()

    fun onButtonClick(){
        isLoading.value = true
        timer.start()
        viewModelScope.launch(Dispatchers.Default) {
            val t = input_equation.value?.let { balancer.balancer(it) }



            if (t != null) {

                if (t.resultType == 1) {


                    displayText.postValue( t.resulting)

                    displayLatex.postValue(t.LatexString)


                    balancedAllGood.postValue( true)
                    convertListforAdaptor(t)
                    solverResult.postValue(t.results)
                    get_thermal_data(t.source)

                } else {
                    displayErrorString.postValue( t.error)
                    balancedAllGood.postValue(false)
                }
            }
        isLoading.postValue(false)
            val elapsedTime = timer.stop() // Calculate elapsed time

            extaradata.postValue("Balance execution time: $elapsedTime ms")
        }
    }
    private val _showOtherElements = MutableLiveData<Boolean>()
    val showOtherElements: LiveData<Boolean> = _showOtherElements

    fun setOtherElementsVisibility(visible: Boolean) {
        _showOtherElements.value = visible
    }

    private fun get_thermal_data(reaction: String?) {
        if(csvData.isNotEmpty())
        {
            if (reactass.contains(reaction)) {
                val index = reactass.indexOf(reaction)
                if (index > -1) {
                    val data = ArrayList((csvData!![index] as Array<String>).toList())
                    val complete = buildString {
                        for (d in 1 until data.size) {
                            when {
                                "tropic" in data[d] -> append("ΔS : ${data[d].replace("_", " J/(mol K) - ")}\n")
                                "therm" in data[d]  -> append("ΔH : ${data[d].replace("_", " kJ/mol - ")}\n")
                                "gon" in data[d]    -> append("ΔG : ${data[d].replace("_", " kJ/mol - ")}\n")
                            }
                        }
                    }

                    thermalText.postValue(complete)
                }
            } else {
                thermalText.postValue("Data not available for this reaction")
            }
        }

    }

    fun convertListforAdaptor(threadResult: ThreadResult)
    {var i=0
        var rlist= ArrayList<MoleculeEntry>()
        var plist= ArrayList<MoleculeEntry>()
     for(s in threadResult.lHS!!) {
            val molarmass=chemUtils.getMoleculeMass(s)
            if (molarmass== 0f ) {
                isAnyInvalidElement = true
            }
            rlist.add(MoleculeEntry(name=s, taken = threadResult.results?.get(i)?.times(molarmass) ?: 0.0f,
                                mole = threadResult.results?.get(i)?.times(1.0f) ?: 0.0f,molar=molarmass))
            i++
        }
        val lhsSize= threadResult.lHS!!.size

        i=0
        for(s in threadResult.rHS!!) {
            val molarmass=chemUtils.getMoleculeMass(s)

            plist.add(MoleculeEntry(name=s, taken = threadResult.results?.get(i+lhsSize)?.times(molarmass) ?: 0.0f, mole = threadResult.results?.get(i+lhsSize)?.times(1.0f) ?: 0.0f,molar=molarmass))
            i++
        }
        reactantList.postValue(rlist)
        //reactantList.value = reactantList.value?.plus(rlist) as ArrayList<MoleculeEntry>?
        productList.postValue(plist)// = productList.value?.plus(plist) as ArrayList<MoleculeEntry>?
    }

    fun updateList()
    {   var tempL=productList
        var i=0
        val len= productList.value?.size ?:0
        while(i < len)
        {
            tempL.value?.get(i)?.mole +=2
            i++
        }
        productList=tempL

    }
    fun splitTextIfNeeded(spannable: SpannableStringBuilder): SpannableStringBuilder {
        val fullText = spannable.toString()
        val delimiter: String = "="
        val lastDelimiterIndex = fullText.lastIndexOf(delimiter)
        if (lastDelimiterIndex == -1) return spannable

        val firstPart = SpannableStringBuilder(spannable.subSequence(0, lastDelimiterIndex))
        val secondPart = SpannableStringBuilder(spannable.subSequence(lastDelimiterIndex + 1, spannable.length))
        if (fullText.length >25  )
        {        return SpannableStringBuilder().apply {
            append(firstPart)
            append("\n=\n")
            append(secondPart)
        }
        }
        return spannable
    }
    fun editClickAdd(s:String)
    {
        textToInsert.value=s
    }
    fun onEquationInputClicked()
    {
        balancedAllGood.value=false
        productList.value= ArrayList<MoleculeEntry>()
        reactantList.value = ArrayList<MoleculeEntry>()
    }
    fun checkClicked()
    {
        updateList()
    }



    fun updateTakenMass(editedMoleculeIndex: Int, isR: Boolean, newMass: Float,isLimit: Boolean) :Int
    {   try{
        val newReactants =reactantList.value.orEmpty().toMutableList()
        if(newReactants.isEmpty())
        {
            return -1
        }
        //newReactants[editedMoleculeIndex].taken =newMass
        val sourceList =  newReactants + productList.value.orEmpty().toMutableList()

        val numR =        reactantList.value?.size ?: 0
        if (editedMoleculeIndex !in sourceList.indices) return -1
        val numMolecule = if (isR) editedMoleculeIndex else editedMoleculeIndex + numR
        val current = sourceList[numMolecule]
        if (current.taken == newMass) return -1


        val molestakenEdited = newMass / current.molar
        var newUpdatedList = ArrayList<MoleculeEntry>()
        newReactants[editedMoleculeIndex].taken =newMass
        if(!isLimit) {
                        newUpdatedList =                stochiometry.UpdateMassesNonLimited(sourceList, numMolecule, molestakenEdited)
        }
        else {
                 val limiterIndex  = getLimitmingIndex(sourceList,numR)
                    if(limiterIndex>-1) {0
                        newUpdatedList = stochiometry.UpdateMassesReagentLimited(sourceList,numMolecule, molestakenEdited,limiterIndex,numR)
                        limitingName.value = sourceList[limiterIndex].name
                    }
             }
             if(newUpdatedList.isNotEmpty()) {
                 reactantList.value = ArrayList(newUpdatedList.subList(0, numR))
                 productList.value  = ArrayList(newUpdatedList.subList(numR, newUpdatedList.size))
             }
        return 0
        }
        catch (e: Exception)
        {
            return -1
        }
    }
    fun updateFromAdapter(pos:Int,isReact : Boolean)
    {}
    fun getLimitmingIndex( fullList: List<MoleculeEntry>,numR : Int) :Int
    {
        val reactingTakenMoles = fullList.take(numR).map { it.taken.div(it.molar)  } .toFloatArray()

        val reactingMoles = solverResult.value?.take(numR)?.map { it -> it*1.0f }

        val prodMasses = fullList.slice(numR..fullList.size-1).map { it.taken }.toFloatArray()

        return stochiometry.checkLimitingreagent(reactingTakenMoles,reactingMoles, solverResult.value, prodMasses)
    }


}