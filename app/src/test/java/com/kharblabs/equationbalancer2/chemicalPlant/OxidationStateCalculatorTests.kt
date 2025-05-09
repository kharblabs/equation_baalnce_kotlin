package com.kharblabs.equationbalancer2.chemicalPlant

import org.junit.Assert.*
import org.junit.Test

class OxidationStateCalculatorTest {

    @Test
    fun testSimpleCompound_H2O() {
        val result = OxidationStateCalculator.calculate("H2O")
        assertEquals(2, result.size)
        assertEquals(1, result["H"]?.get("overall"))
        assertEquals(-2, result["O"]?.get("overall"))
    }

    @Test
    fun testIonicCompound_NaCl() {
        val result = OxidationStateCalculator.calculate("NaCl")
        assertEquals(1, result["Na"]?.get("overall"))
        assertEquals(-1, result["Cl"]?.get("overall"))
    }

    @Test
    fun testPolyatomic_NH4NO3() {
        val result = OxidationStateCalculator.calculate("NH4NO3")
        assertEquals(-3, result["N"]?.get("Ammonium"))
        assertEquals(+5, result["N"]?.get("Nitrate"))
        assertEquals(+1, result["H"]?.get("Ammonium"))
        assertEquals(-2, result["O"]?.get("Nitrate"))
    }

    @Test
    fun testPolyatomicWithNested_K4FeCN6() {
        val result = OxidationStateCalculator.calculate("K4(Fe(CN)6)")
        assertEquals(1, result["K"]?.get("overall"))
        assertEquals(+2, result["Fe"]?.get("overall"))
        assertEquals(+2, result["C"]?.get("overall"))
        assertEquals(-3, result["N"]?.get("overall"))
    }

    @Test
    fun testAcetate_NaCH3COO() {
        val result = OxidationStateCalculator.calculate("NaCH3COO")
        assertEquals(1, result["Na"]?.get("overall"))
        assertEquals(3, result["C"]?.get("Acetate"))
        assertEquals(1, result["H"]?.get("Acetate"))
        assertEquals(-2, result["O"]?.get("Acetate"))
    }

    @Test
    fun testPermanganate_MnO4Negative() {
        val result = OxidationStateCalculator.calculate("MnO4^-")
        assertEquals(+7, result["Mn"]?.get("overall"))
        assertEquals(-2, result["O"]?.get("overall"))
    }

    @Test
    fun testDichromate_Cr2O7_2Minus() {
        val result = OxidationStateCalculator.calculate("Cr2O7^2-")
        assertEquals(+6, result["Cr"]?.get("overall"))
        assertEquals(-2, result["O"]?.get("overall"))
    }

    @Test
    fun testPhosphate_Ca3PO42() {
        val result = OxidationStateCalculator.calculate("Ca3(PO4)2")
        assertEquals(+2, result["Ca"]?.get("overall"))
        assertEquals(+5, result["P"]?.get("Phosphate"))
        assertEquals(-2, result["O"]?.get("Phosphate"))
    }

    @Test(expected = IllegalStateException::class)
    fun testTooManyUnknowns() {
        // C2H6O → multiple valid oxidation states, not enough constraints
        OxidationStateCalculator.calculate("C2H6O")
    }
}
