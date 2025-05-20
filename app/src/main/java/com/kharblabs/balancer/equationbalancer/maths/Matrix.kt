package com.kharblabs.balancer.equationbalancer.maths


class Matrix(rows: Int, columns: Int) {
    var rows: Int = 1
    var columns: Int = 1
    lateinit var m: Array<DoubleArray>

    init {
        this.rows = rows
        this.columns = columns
        initialisetoZero(rows, columns)
    }

    fun println() {
        kotlin.io.println()
    }
    fun multiplyRow(m: Array<DoubleArray>, row: Int, scalar: Double) {
        for (c1 in m[0].indices) m[row][c1] *= scalar
    }
    fun swapRows(m: Array<DoubleArray>, row1: Int, row2: Int) {
        val swap = DoubleArray(m[0].size)

        for (c1 in m[0].indices) swap[c1] = m[row1][c1]

        for (c1 in m[0].indices) {
            m[row1][c1] = m[row2][c1]
            m[row2][c1] = swap[c1]
        }
    }
    fun subtractRows(        m: Array<DoubleArray>,        scalar: Double,        subtract_scalar_times_this_row: Int,        from_this_row: Int    )
    {
        for (c1 in m[0].indices) m[from_this_row][c1] -= scalar * m[subtract_scalar_times_this_row][c1]
    }
    fun rref(m: Array<DoubleArray>) {
        var lead = 0
        val rowCount = m.size
        val colCount = m[0].size
        var i: Int
        var quit = false

        var row = 0
        while (row < rowCount && !quit) {
            //print(m);
            println()

            if (colCount <= lead) {
                quit = true
                break
            }

            i = row

            while (!quit && m[i][lead] == 0.0) {
                i++
                if (rowCount == i) {
                    i = row
                    lead++

                    if (colCount == lead) {
                        quit = true
                        break
                    }
                }
            }

            if (!quit) {
               swapRows(m, i, row)

                if (m[row][lead] != 0.0) multiplyRow(
                    m,
                    row,
                    1.0f / m[row][lead]
                )

                i = 0
                while (i < rowCount) {
                    if (i != row)
                        subtractRows(  m, m[i][lead], row, i )
                    i++
                }
            }
            row++
        }
    }
    fun initialisetoZero(x: Int, y: Int) {
        m = Array(x) { DoubleArray(y) }
        for (i in 0..<x) for (j in 0..<y) m[i][j] = 0.0
    }

    fun swapRow(a: Int, b: Int) {
        var tempR = DoubleArray(columns)
        tempR = m[a]
        m[a] = m[b]
        m[b] = tempR
    }

    fun setCell(x: Int, y: Int, z: Double) {
        m[x][y] = z
    }

    fun addRows(i: DoubleArray, j: DoubleArray): DoubleArray? {
        if (i.size == j.size) {
            val y = DoubleArray(i.size)
            for (n in 0..<columns) {
                y[n] = i[n] + j[n]
            }
            return y
        }
        return null
    }

    fun multipyROw(x: DoubleArray, scalar: Double): DoubleArray {
        for (i in x.indices) {
            x[i] = x[i] * scalar
        }
        return x
    }

    //Greatest Common Divisor of two nums
    fun gcd(a: Int, b: Int): Int {
        if (a == 0) return b
        return gcd(b % a, a)
    }

    //?array GCD
    fun gcdArr(arr: DoubleArray): Double {
        var result = arr[0]
        for (i in 1..<arr.size) result = gcd(arr[i].toInt(), result.toInt()).toDouble()

        return result
    }

    fun simplifyRow(x: DoubleArray): DoubleArray {
        var sign = 0
        for (i in x.indices) {
            if (x[i] > 0) {
                sign = 1
                break
            } else if (x[i] < 0) {
                sign = -1
                break
            }
        }
        var y = DoubleArray(x.size)
        y = x
        if (sign == 0) return y
        val g = gcdArr(x) * sign
        for (i in y.indices) {
            y[i] /= g
        }
        return y
    }

    fun guassJordanElimination() {
        for (i in 0..<rows) {
            m[i] = simplifyRow(m[i])
        }
        var numPivot = 0
        for (i in 0..<columns) {
            var pivotRow = numPivot
            while (pivotRow < rows && m[pivotRow][i] == 0.0) pivotRow++
            if (pivotRow == rows) continue
            val pivot = (m[pivotRow][i].toInt())
            swapRow(numPivot, pivotRow)
            numPivot++


            //ELiminiation
            for (j in numPivot..<rows) {
                val g = gcd(pivot, m[j][i].toInt())
                m[j] = simplifyRow(
                    addRows(
                        multipyROw(m[j], (pivot / g).toDouble()),
                        multipyROw(m[i], -m[j][i] / g)
                    )!!
                )
            }
        }

        //Reduced ehelon form
        for (i in rows - 1 downTo 0) {
            var pivotCol = 0
            while (pivotCol < columns && m[i][pivotCol] == 0.0) pivotCol++
            if (pivotCol == columns) continue
            val pivot = m[i][pivotCol].toInt()

            for (j in i - 1 downTo 0) {
                val g = gcd(pivot, m[j][pivotCol].toInt())
                m[j] = simplifyRow(
                    addRows(
                        multipyROw(m[j], (pivot / g).toDouble()),
                        multipyROw(m[i], -m[j][pivotCol] / g)
                    )!!
                )
            }
        }
    }

    fun solve(a: Matrix): Int {
        //a.guassJordanElimination();
        var i = 0
        while (i < a.rows - 1) {
            if (cNonZero(a, i) > 1) {
                break
            }
            i++
        }
        if (i == a.rows - 1) return 0
        //
        if (a.rows != a.columns) {
            a.setCell(rows - 1, i, 1.0)
            a.setCell(rows - 1, columns - 1, 1.0)
        }
        rref((a.m))
        // a.guassJordanElimination();
        return 1
    }

    fun cNonZero(a: Matrix, i: Int): Int {
        var cnt = 0
        for (j in 0..<a.columns) {
            if (a.m.get(i).get(j) != 0.0) cnt++
        }
        return cnt
    }



    fun returnIntegerMat(): Array<IntArray> {
        /*
        val a = Array(rows) { IntArray(columns) }
        for (i in 0..<rows) for (j in 0..<columns) a[i][j] = m[i][j].toInt()
        return a
        */
        return m.map { row -> row.map { it.toInt() }.toIntArray() }.toTypedArray()
    }



}