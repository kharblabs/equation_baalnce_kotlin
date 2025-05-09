package com.kharblabs.equationbalancer2.chemicalPlant
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class OxidationStateCalculatorParameterizedTest(
    private val formula: String,
    private val expected: Map<String, Map<String, Int>>
) {

    @Test
    fun testOxidationStates() {
        val result = OxidationStateCalculator.calculate(formula)

        println("Formula: $formula")
        println("Expected: $expected")
        println("Actual  : $result\n")

        for ((element, contexts) in expected) {
            for ((context, oxExpected) in contexts) {
                val actual = result[element]?.get(context)
                assertEquals("Mismatch for $element in context [$context] of $formula", oxExpected, actual)
            }
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): Collection<Array<Any>> = listOf(
            // Existing test cases
            arrayOf("H2O", mapOf(
                "H" to mapOf("overall" to 1),
                "O" to mapOf("overall" to -2)
            )),
            arrayOf("NaCl", mapOf(
                "Na" to mapOf("overall" to 1),
                "Cl" to mapOf("overall" to -1)
            )),
            arrayOf("NH4NO3", mapOf(
                "N" to mapOf("Ammonium" to -3, "Nitrate" to 5),
                "H" to mapOf("Ammonium" to 1),
                "O" to mapOf("Nitrate" to -2)
            )),
            arrayOf("NaCH3COO", mapOf(
                "Na" to mapOf("overall" to 1),
                "C" to mapOf("Acetate" to 3),
                "H" to mapOf("Acetate" to 1),
                "O" to mapOf("Acetate" to -2)
            )),
            arrayOf("MnO4^-", mapOf(
                "Mn" to mapOf("overall" to 7),
                "O" to mapOf("overall" to -2)
            )),
            arrayOf("Ca3(PO4)2", mapOf(
                "Ca" to mapOf("overall" to 2),
                "P" to mapOf("Phosphate" to 5),
                "O" to mapOf("Phosphate" to -2)
            )),
            arrayOf("K4(Fe(CN)6)", mapOf(
                "K" to mapOf("overall" to 1),
                "Fe" to mapOf("overall" to 2),
                "C" to mapOf("overall" to 2),
                "N" to mapOf("overall" to -3)
            )),
            arrayOf("Cr2O7^2-", mapOf(
                "Cr" to mapOf("overall" to 6),
                "O" to mapOf("overall" to -2)
            )),

            // 30 more molecules for testing
            arrayOf("H2SO4", mapOf(
                "H" to mapOf("overall" to 1),
                "S" to mapOf("overall" to +6),
                "O" to mapOf("overall" to -2)
            )),
            arrayOf("Na2SO4", mapOf(
                "Na" to mapOf("overall" to 1),
                "S" to mapOf("overall" to +6),
                "O" to mapOf("overall" to -2)
            )),
            arrayOf("K2CO3", mapOf(
                "K" to mapOf("overall" to 1),
                "C" to mapOf("overall" to +4),
                "O" to mapOf("overall" to -2)
            )),
            arrayOf("Fe2O3", mapOf(
                "Fe" to mapOf("overall" to +3),
                "O" to mapOf("overall" to -2)
            )),
            arrayOf("NH3", mapOf(
                "N" to mapOf("overall" to -3),
                "H" to mapOf("overall" to +1)
            )),
            arrayOf("KNO3", mapOf(
                "K" to mapOf("overall" to +1),
                "N" to mapOf("Nitrate" to +5),
                "O" to mapOf("Nitrate" to -2)
            )),
            arrayOf("NaNO3", mapOf(
                "Na" to mapOf("overall" to +1),
                "N" to mapOf("Nitrate" to +5),
                "O" to mapOf("Nitrate" to -2)
            )),
            arrayOf("HNO3", mapOf(
                "H" to mapOf("overall" to +1),
                "N" to mapOf("Nitrate" to +5),
                "O" to mapOf("Nitrate" to -2)
            )),
            arrayOf("CH3OH", mapOf(
                "C" to mapOf("overall" to -2),
                "H" to mapOf("overall" to +1),
                "O" to mapOf("overall" to -2)
            )),
            arrayOf("NaOH", mapOf(
                "Na" to mapOf("overall" to +1),
                "O" to mapOf("Hydroxide" to -2),
                "H" to mapOf("Hydroxide" to +1)
            )),
            arrayOf("CuSO4", mapOf(
                "Cu" to mapOf("overall" to +2),
                "S" to mapOf("overall" to +6),
                "O" to mapOf("overall" to -2)
            )),
            arrayOf("Na2CO3", mapOf(
                "Na" to mapOf("overall" to +1),
                "C" to mapOf("overall" to +4),
                "O" to mapOf("overall" to -2)
            )),
            arrayOf("H2O2", mapOf(
                "H" to mapOf("overall" to +1),
                "O" to mapOf("overall" to -1)
            )),
            arrayOf("Cl2", mapOf(
                "Cl" to mapOf("overall" to 0)
            )),
            arrayOf("HCl", mapOf(
                "H" to mapOf("overall" to +1),
                "Cl" to mapOf("overall" to -1)
            )),
            arrayOf("K2CrO4", mapOf(
                "K" to mapOf("overall" to +1),
                "Cr" to mapOf("overall" to +6),
                "O" to mapOf("overall" to -2)
            )),
            arrayOf("NaHCO3", mapOf(
                "Na" to mapOf("overall" to +1),
                "H" to mapOf("overall" to +1),
                "C" to mapOf("overall" to +4),
                "O" to mapOf("overall" to -2)
            )),
            arrayOf("BaCl2", mapOf(
                "Ba" to mapOf("overall" to +2),
                "Cl" to mapOf("overall" to -1)
            )),
            arrayOf("MgCl2", mapOf(
                "Mg" to mapOf("overall" to +2),
                "Cl" to mapOf("overall" to -1)
            )),
            arrayOf("FeSO4", mapOf(
                "Fe" to mapOf("overall" to +2),
                "S" to mapOf("overall" to +6),
                "O" to mapOf("overall" to -2)
            )),
            arrayOf("CuCl2", mapOf(
                "Cu" to mapOf("overall" to +2),
                "Cl" to mapOf("overall" to -1)
            )),
            arrayOf("Al2O3", mapOf(
                "Al" to mapOf("overall" to +3),
                "O" to mapOf("overall" to -2)
            )),
            arrayOf("Na2S", mapOf(
                "Na" to mapOf("overall" to +1),
                "S" to mapOf("overall" to -2)
            )),
            arrayOf("Li2O", mapOf(
                "Li" to mapOf("overall" to +1),
                "O" to mapOf("overall" to -2)
            )),
            arrayOf("C2H6O", mapOf(
                "C" to mapOf("overall" to -2),
                "H" to mapOf("overall" to +1),
                "O" to mapOf("overall" to -2)
            )),
            arrayOf("CH4", mapOf(
                "C" to mapOf("overall" to -4),
                "H" to mapOf("overall" to +1)
            )),
            arrayOf("N2O", mapOf(
                "N" to mapOf("overall" to +1),
                "O" to mapOf("overall" to -2)
            ))
        )
    }
}
