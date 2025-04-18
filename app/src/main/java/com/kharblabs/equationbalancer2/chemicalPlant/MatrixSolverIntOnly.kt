package com.kharblabs.equationbalancer2.chemicalPlant

import kotlin.math.abs

class MatrixSolveIntOnly(var matrx: Array<IntArray>) {
    var resultingMat: Array<IntArray>
    var noSol: Int = 0

    init {
        this.resultingMat = guassJordanElimination()
        var i = 0
        i = 0
        while (i < matrx.size - 1) {
            if (countNonZeroCoeff(matrx[i]) > 1) break
            i++
        }
        if (i == matrx.size - 1) {
            noSol = 1
        } else {
            resultingMat[matrx.size - 1][0] = 1
            resultingMat[matrx.size - 1][matrx[0].size - 1] = 1
            matrx = resultingMat
            resultingMat = guassJordanElimination()
        }
    }

    fun guassJordanElimination(): Array<IntArray> {
        val rows = matrx.size
        val columns = matrx[0].size
        val m = matrx
        for (i in 0..<rows) {
            m[i] = simplifyRow(m[i])
        }
        var numPivot = 0
        for (i in 0..<columns) {
            var pivotRow = numPivot
            while (pivotRow < rows && m[pivotRow][i] == 0) pivotRow++
            if (pivotRow == rows) continue

            val pivot = m[pivotRow][i]
            //swap of gloable
            swapRow(numPivot, pivotRow)
            numPivot++


            //ELiminiation
            for (j in numPivot..<rows) {
                val g = gcd(pivot, m[j][i])
                m[j] = simplifyRow(
                    addRows(
                        multipyROw(m[j], pivot / g),
                        multipyROw(m[i], -m[j][i] / g)
                    )
                )
            }
        }

        //Reduced ehelon form
        for (i in rows - 1 downTo 0) {
            var pivotCol = 0
            while (pivotCol < columns && m[i][pivotCol] == 0) pivotCol++
            if (pivotCol == columns) continue
            val pivot = m[i][pivotCol]

            for (j in i - 1 downTo 0) {
                val g = gcd(pivot, m[j][pivotCol])
                m[j] = simplifyRow(
                    addRows(
                        multipyROw(m[j], pivot / g),
                        multipyROw(m[i], -m[j][pivotCol] / g)
                    )
                )
            }
        }


        return m
    }

    fun simplifyRow(x: IntArray): IntArray {
        var sign = 0
        for (i in x.indices) {
            if (x[i] != 0) {
                sign = if (x[i] < 0) -1
                else {
                    1
                }
                break
            }
        }
        var y = IntArray(x.size)
        y = x
        if (sign == 0) return y
        val g = (abs(gcdArr(x).toDouble()) * sign).toInt()
        for (i in y.indices) {
            y[i] /= g
        }
        return y
    }


    fun swapRow(a: Int, b: Int) {
        val temp = matrx[a]
        matrx[a] = matrx[b]
        matrx[b] = temp
    }

    fun multipyROw(x: IntArray, a: Int): IntArray {
        val tem = IntArray(x.size)
        for (i in x.indices) tem[i] = x[i] * a
        return tem
    }

    fun addRows(x: IntArray, a: IntArray): IntArray {
        val tem = IntArray(x.size)
        for (i in x.indices) tem[i] = x[i] + a[i]
        return tem
    }

    fun gcdArr(arr: IntArray): Int {
        var i = 0
        var result = 1
        while (arr[i] == 0 && i < arr.size) {
            i++
            if (i == arr.size) break
        }
        if (i == arr.size) return 1
        else result = arr[i]

        i = 1
        while (i < arr.size) {
            result = gcd(arr[i], result)
            i++
        }

        return result
    }

    fun gcd(a: Int, b: Int): Int {
        if (a == 0) return b
        return gcd(b % a, a)
    }

    fun countNonZeroCoeff(a: IntArray): Int {
        var count = 0
        for (i in a.indices) if (a[i] != 0) count++

        return count
    }
}