package com.kharblabs.equationbalancer2.chemicalPlant

import com.kharblabs.equationbalancer2.dataManagers.MoleculeEntry
import org.mockito.kotlin.isNull

class Stochiometry() {
    val chemUtils =ChemUtils()
    var elements =ConstantValues().elements
    var masses =ConstantValues().roundedMasses

    fun getAtomicNumber(s: String): Int {

        return elements.indexOf(s)
    }

    fun getAtMass(s: String): Float {
        val x = getAtomicNumber(s)
        return if (x == -1) 0f
        else {

            masses[x]
        }
    }

    fun getMolecularMass(s: String ): Float
    {   var totalMas=0.0f
        val proper= chemUtils.elementParser(s)
        if (proper != null) {

            for (e in proper)
            {   val massthis=getAtMass(e.name)
                if (massthis>0) {
                    totalMas += massthis*e.count
                    println(totalMas)
                }
                else
                    return -1.0f
                println(e.name + "  "+ e.count)
            }
            return  totalMas
        }
        return 0f
    }

    fun MassCal(products: IntArray, reactingMoles: Float, takenMoles: Float): FloatArray {
        val perUnit = FloatArray(products.size)
        for (i in products.indices) {
            perUnit[i] = products[i] / reactingMoles
        }

        val finalVol = FloatArray(products.size)
        for (i in products.indices) {
            finalVol[i] = perUnit[i] * takenMoles
        }
        return finalVol
    }

    // taken reactants moles            coefficient

    fun checkLimitingreagent(reacts: FloatArray?, reactinMoles: List<Float>?, moles: IntArray?, prodMolarMasses: FloatArray?): Int
    {
        if ( reacts== null || reactinMoles ==null || moles ==null || prodMolarMasses==null)
            return -1
        val numReacts = reacts.size
        val productMoles = IntArray(moles.size - reacts.size)
        for (i in productMoles.indices) productMoles[i] = moles[reacts.size - 1 + i]

        var maxAt = 0
        val massCreated = FloatArray(numReacts)
        for (x in reacts.indices) {

            var calcuatedMoles = FloatArray(prodMolarMasses.size)
            calcuatedMoles = MassCal(productMoles, reactinMoles[x], reacts[x])

            massCreated[x] = calcuatedMoles[0]
        }
        for (i in massCreated.indices) {
            maxAt = if (massCreated[i] < massCreated[maxAt]) i else maxAt
        }
        return maxAt
    }

     fun UpdateMassesNonLimited(fullList: List<MoleculeEntry>,       moleculeIndex: Int, newMoles: Float ): ArrayList<MoleculeEntry> {

        try {
            val current=fullList[moleculeIndex]
            val updatedList = fullList.mapIndexed { i, molecule ->
                val updatedTaken =
                    ((molecule.molar * newMoles * molecule.mole / current.mole) * 100 as Int / 100) * 1.0f
                molecule.copy(taken = updatedTaken)
            }

            return updatedList as ArrayList<MoleculeEntry>

        }
        catch (e : Exception )
        {
            return ArrayList<MoleculeEntry>()
        }


    }

     fun UpdateMassesReagentLimited(fullList: List<MoleculeEntry>,  moleculeEditedIndex: Int, newMolesEdited: Float, limitingIndex: Int, numR: Int ): ArrayList<MoleculeEntry> {


        fullList[moleculeEditedIndex].taken = newMolesEdited * fullList[moleculeEditedIndex].molar
        val limiterIndex = limitingIndex
        if (limiterIndex > -1) {

            val limitingCoeff = fullList[limitingIndex].mole
            val limitingMoles = fullList[limitingIndex].taken/fullList[limitingIndex].molar
            val reactionScale =  limitingMoles/ limitingCoeff
            val updated = fullList.mapIndexed { index, mol ->
                val moleRequired = mol.mole * reactionScale
                val massRequired = moleRequired * mol.molar
                val residue = mol.taken - massRequired
                mol.copy(
                    taken = if (index == moleculeEditedIndex) newMolesEdited * mol.molar else if(index > numR-1) massRequired else mol.taken,
                    residue = residue
                )
            }

            return updated as ArrayList<MoleculeEntry>
            //return newList as ArrayList<MoleculeEntry>

        }

        return ArrayList<MoleculeEntry>()
    }
    fun findLimitingReagentIndex(molecules: List<MoleculeEntry>, numR: Int): Int? {
        // Find the number of reactants.  Assume reactants are at the beginning of the list.
        val numReactants = numR
        // Calculate mole ratio for each reactant (moles / coefficient).
        val reactantMoleRatios = (0 until numReactants).map { i ->
            val molecule = molecules[i]
            molecule.taken / (molecule.mole).toFloat()  // Use abs for the coefficient
        }

        // Find the minimum mole ratio.  The reactant with the minimum ratio is limiting.
        var limitingIndex: Int? = null
        var minRatio = Float.MAX_VALUE
        for (i in 0 until numReactants) {
            if (reactantMoleRatios[i] < minRatio) {
                minRatio = reactantMoleRatios[i]
                limitingIndex = i
            }
        }
        return limitingIndex
    }

}
