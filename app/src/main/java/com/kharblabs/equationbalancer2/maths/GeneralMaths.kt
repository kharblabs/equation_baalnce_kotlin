package com.kharblabs.equationbalancer2.maths
import kotlin.math.*
class GeneralMaths {

    fun gcd(a: Int, b: Int): Int = if (b == 0) abs(a) else gcd(b, a % b)

    fun lcm(a: Int, b: Int): Int = abs(a * b / gcd(a, b))

    fun GetDenom(x: Double): Int {
        val tolerance = 1.0E-6
        var h1 = 1.0
        var h2 = 0.0
        var k1 = 0.0
        var k2 = 1.0
        var b = x
        do {
            val a = floor(b)
            var aux = h1
            h1 = a * h1 + h2
            h2 = aux
            aux = k1
            k1 = a * k1 + k2
            k2 = aux
            b = 1 / (b - a)
        } while (abs(x - h1 / k1) > x * tolerance)

        return k1.toInt()
    }
    fun findGcd(numbers: IntArray): Int {
        //Find the smallest integer in the number list

        if (numbers[numbers.size - 1] == 0) {
            numbers[numbers.size - 1] = numbers[numbers.size - 2]
        }
        var smallest = numbers[0]

        for (i in 1..<numbers.size) {
            if (numbers[i] < smallest) {
                smallest = numbers[i]
            }
        }

        //Find the GCD
        while (smallest > 1) {
            var counter = 0
            var modTot = 0

            while (counter < numbers.size) {
                modTot += numbers[counter] % smallest
                counter++
            }

            if (modTot == 0) {
                //Return the gcd if any
                return smallest
            }

            //System.out.print(" "+ smallest);
            smallest--
        }
        //return -1 if there is no gcd
        return 1
    }
    fun convertToIntegerRatios(input: List<Double>): List<Int> {
        val denoms = input.map { GetDenom(it) }
        var overallLcm = 1
        for (d in denoms) {
            overallLcm = lcm(overallLcm, d)
        }
        return input.map { (it * overallLcm).roundToInt() }
    }
    fun doubleToFraction(x: Double, maxDenominator: Int = 10000): Fraction {
        val tolerance = 1.0E-6
        var h1 = 1
        var h2 = 0
        var k1 = 0
        var k2 = 1
        var b = x
        var iteration = 0
        val maxIterations = 20

        while (iteration++ < maxIterations) {
            val a = floor(b).toInt()
            val h = a * h1 + h2
            val k = a * k1 + k2
            if (k == 0) break
            if (abs(x - h.toDouble() / k) < tolerance) return Fraction(h, k)
            h2 = h1
            h1 = h
            k2 = k1
            k1 = k
            val diff = b - a
            if (diff == 0.0) break
            b = 1.0 / diff
        }
        return Fraction(x.roundToInt(), 1)
    }
    data class Fraction(val numerator: Int, val denominator: Int)
    fun convertToMinimalIntRatios(input: List<Double>): List<Int> {
        val fractions = input.map { doubleToFraction(it) }
        val lcmDen = fractions.map { it.denominator }.reduce { acc, d -> lcm(acc, d) }

        // Multiply all to eliminate denominators
        val scaled = fractions.map { it.numerator * (lcmDen / it.denominator) }

        // Reduce all by their GCD to get minimal integer ratio
        val overallGCD = scaled.reduce { acc, x -> gcd(acc, x) }

        return scaled.map { it / overallGCD }
    }
}