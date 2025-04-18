package com.kharblabs.equationbalancer2.chemicalPlant

import android.content.Context

class Stochiometry() {

    var elements =ConstantValues().elements
    var masses =ConstantValues().roundedMasses

    fun getAtomicNumber(s: String): Int {
        var c = 0
        for (sx in elements) {
            if (sx.matches(s.toRegex())) {
                return c
            }
            c++
        }
        return -1
    }

    fun getAtMass(s: String): Float {
        val x = getAtomicNumber(s)
        if (x == -1) return 0f
        else {
            val m = masses[x]
            return m as Float
        }
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
    } // taken reactants moles            coefficient

    fun chechLimitingreagent(
        reacts: FloatArray,
        reactinMoles: IntArray,
        moles: IntArray,
        prodMolarMasses: FloatArray
    ): Int {
        val numReacts = reacts.size
        val productMoles = IntArray(moles.size - reacts.size)
        for (i in productMoles.indices) productMoles[i] = moles[reacts.size - 1 + i]

        var maxAt = 0
        val massCreated = FloatArray(numReacts)
        for (x in reacts.indices) {
            val calcuatedMasses = FloatArray(prodMolarMasses.size)
            var calcuatedMoles = FloatArray(prodMolarMasses.size)
            calcuatedMoles = MassCal(productMoles, reactinMoles[x].toFloat(), reacts[x])

            massCreated[x] = calcuatedMoles[0]
        }
        for (i in massCreated.indices) {
            maxAt = if (massCreated[i] < massCreated[maxAt]) i else maxAt
        }
        return maxAt
    }
}
