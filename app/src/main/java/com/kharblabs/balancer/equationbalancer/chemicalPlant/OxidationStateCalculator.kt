package com.kharblabs.balancer.equationbalancer.chemicalPlant

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.SuperscriptSpan
import android.text.style.TypefaceSpan

data class Ion(val name: String, val formula: String, val charge: Int, val oxidationStates: Map<String, Int>)
sealed class Token(val count: Int) {
    data class Element(val symbol: String, val count_: Int) : Token(count_)
    data class Ion(val name: String, val count_: Int) : Token(count_)
    data class Group(val tokens: List<Token>, val count_: Int) : Token(count_)
}

class OxidationStateCalculator {

    private val polyatomicIons = listOf(
        Ion("Sulfate", "SO4", -2, mapOf("S" to +6, "O" to -2)),
        Ion("Sulfite", "SO3", -2, mapOf("S" to +4, "O" to -2)),
        Ion("Nitrate", "NO3", -1, mapOf("N" to +5, "O" to -2)),
        Ion("Nitrite", "NO2", -1, mapOf("N" to +3, "O" to -2)),
        Ion("Phosphate", "PO4", -3, mapOf("P" to +5, "O" to -2)),
        Ion("Phosphite", "PO3", -3, mapOf("P" to +3, "O" to -2)),
        Ion("Carbonate", "CO3", -2, mapOf("C" to +4, "O" to -2)),
        Ion("Hydroxide", "OH", -1, mapOf("O" to -2, "H" to +1)),
        Ion("Ammonium", "NH4", +1, mapOf("N" to -3, "H" to +1)),
        Ion("Acetate", "CH3COO", -1, mapOf("C" to +3, "H" to +1, "O" to -2)),
        Ion("Cyanide", "CN", -1, mapOf("C" to +2, "N" to -3)),
        Ion("Permanganate", "MnO4", -1, mapOf("Mn" to +7, "O" to -2)),
        Ion("Chromate", "CrO4", -2, mapOf("Cr" to +6, "O" to -2)),
        Ion("Dichromate", "Cr2O7", -2, mapOf("Cr" to +6, "O" to -2)),
        Ion("Perchlorate", "ClO4", -1, mapOf("Cl" to +7, "O" to -2)),
        Ion("Chlorate", "ClO3", -1, mapOf("Cl" to +5, "O" to -2)),
        Ion("Chlorite", "ClO2", -1, mapOf("Cl" to +3, "O" to -2)),
        Ion("Hypochlorite", "ClO", -1, mapOf("Cl" to +1, "O" to -2)),
        Ion("Bicarbonate", "HCO3", -1, mapOf("H" to +1, "C" to +4, "O" to -2)),
        Ion("Hydrogen_sulfate", "HSO4", -1, mapOf("H" to +1, "S" to +6, "O" to -2)),
        Ion("Hydrogen_sulfite", "HSO3", -1, mapOf("H" to +1, "S" to +4, "O" to -2)),
        Ion("Dihydrogen_phosphate", "H2PO4", -1, mapOf("H" to +1, "P" to +5, "O" to -2)),
        Ion("Hydrogen_phosphate", "HPO4", -2, mapOf("H" to +1, "P" to +5, "O" to -2)),

        Ion("Hydronium", "H3O", +1, mapOf("H" to +1, "O" to -2)),
        Ion("Peroxide", "O2", -2, mapOf("O" to -1)),
        Ion("Superoxide", "O2", -1, mapOf("O" to -0)), // Special case, often avoided in basic apps

        Ion("Thiosulfate", "S2O3", -2, mapOf("S" to +2, "O" to -2)), // Approx. oxidation
        Ion("Oxalate", "C2O4", -2, mapOf("C" to +3, "O" to -2)),
        Ion("Tartrate", "C4H4O6", -2, mapOf("C" to +3, "H" to +1, "O" to -2)), // Approx.
        Ion("Formate", "HCOO", -1, mapOf("H" to +1, "C" to +2, "O" to -2)),

        Ion("Cyanate", "OCN", -1, mapOf("O" to -2, "C" to +4, "N" to -3)),
        Ion("Thiocyanate", "SCN", -1, mapOf("S" to -2, "C" to +4, "N" to -3)),

        Ion("Arsenate", "AsO4", -3, mapOf("As" to +5, "O" to -2)),
        Ion("Arsenite", "AsO3", -3, mapOf("As" to +3, "O" to -2)),

        Ion("Tetraborate", "B4O7", -2, mapOf("B" to +3, "O" to -2)), // Common in borax
        Ion("Borate", "BO3", -3, mapOf("B" to +3, "O" to -2)),

        Ion("Silicate", "SiO4", -4, mapOf("Si" to +4, "O" to -2)),

        Ion("Tellurate", "TeO4", -2, mapOf("Te" to +6, "O" to -2)),
        Ion("Tellurite", "TeO3", -2, mapOf("Te" to +4, "O" to -2)),

        Ion("Manganate", "MnO4", -2, mapOf("Mn" to +6, "O" to -2)),
        Ion("Ethyl", "C2H5", 1, mapOf("C" to -2, "H" to +1)),
        Ion("Ethyl", "CH3CH2", 1, mapOf("C" to -2, "H" to +1)),
        Ion("Methyl", "CH3", 1, mapOf("C" to -2, "H" to +1)),

    )

    private val fixedStates = mapOf(
        "H" to +1, "O" to -2, "F" to -1,
        "Na" to +1, "K" to +1, "Li" to +1,
        "Ca" to +2, "Mg" to +2, "Ba" to +2,
        "Al" to +3, "Zn" to +2, "Ag" to +1
    )
    fun assignOnlyOneIon(formula: String,netCharge:Int): Pair<Map<String, Map<String, Int>>,Int>  {

        return Pair(mapOf(formula to mapOf(getNameFromSymbol(formula)+" Molecule" to 0)),netCharge)
    }
    fun calculate(input: String): Pair<Map<String, Map<String, Int>>,Int> {
        val (rawFormula, netCharge) = extractCharge(input)
        val orderofElements=rawFormula.replace(Regex("[^A-Za-z ]"), "").split(Regex("(?=[A-Z])")).filter { it.isNotEmpty() }
        val numElements=orderofElements.toSet().size
        //if molecule or ion
        if(numElements==1)
            return assignOnlyOneIon(orderofElements[0],netCharge)
        val formula = replaceIonFormulasWithNames(rawFormula)
        val brokenFormula= ChemUtils().BracketBreaker(formula)
        val output = mutableMapOf<String, MutableMap<String, Int>>()

        val ionsFound = findPolyatomicIons(brokenFormula.toString())
        val formulaRemaining = removeIonNames(brokenFormula.toString(), ionsFound)
        var residualcharge=netCharge
        for ((ion, count) in ionsFound) {
            for ((element, ox) in ion.oxidationStates) {
                output.computeIfAbsent(element) { mutableMapOf() }
                    .compute(ion.name) { _, v -> v ?: ox }

            }
            residualcharge=residualcharge-ion.charge*count
        }

        val atoms = flattenFormula(formulaRemaining)
        if (atoms.isEmpty()) return Pair(reorderNestedMap(output,orderofElements),residualcharge)

        val (solved,residualChargeNew) = try {
            solveOxidationStates(atoms, residualcharge)
        } catch (e: Exception) {
            Pair(emptyMap(),0)
        }

        for ((element, ox) in solved) {

            output.computeIfAbsent(element) { mutableMapOf() }[getNameFromSymbol(element)] = ox
        }

        return Pair(reorderNestedMap(output,orderofElements),-(residualChargeNew+residualcharge))
    }
    fun getNameFromSymbol(string: String): String
    {   val t= consts.elements.indexOf(string)
        if(t>-1)
            return consts.elementNames[t]
        return "--"
    }

    fun reorderNestedMap(
        original: Map<String, Map<String, Int>>,
        desiredOrder: List<String>
    ): LinkedHashMap<String, Map<String, Int>> {
        val ordered = linkedMapOf<String, Map<String, Int>>()

        for (key in desiredOrder) {
            original[key]?.let { ordered[key] = it }
        }

        return ordered
    }
    val consts= ConstantValues()
    private fun extractCharge(formula: String): Pair<String, Int> {
        val regex = Regex("""\^(\d*)([+-])$""")
        val match = regex.find(formula)
        return if (match != null) {
            val mag = match.groupValues[1].ifEmpty { "1" }.toInt()
            val sign = match.groupValues[2]
            val charge = if (sign.isEmpty()) mag else -mag
            formula.removeSuffix(match.value) to charge
        } else formula to 0
    }

    private fun replaceIonFormulasWithNames(formula: String): String {
        var result = formula
        for (ion in polyatomicIons.sortedByDescending { it.formula.length }) {
            result = result.replace(ion.formula, ion.name)
        }
        return result
    }

    private fun findPolyatomicIons(formula: String): List<Pair<Ion, Int>> {
        val found = mutableListOf<Pair<Ion, Int>>()
        for (ion in polyatomicIons) {
            val regex = Regex("""(${ion.name})(\d*)""")
            for (match in regex.findAll(formula)) {
                val count = match.groupValues[2].ifEmpty { "1" }.toInt()
                found.add(ion to count)
            }
        }
        return found
    }

    private fun removeIonNames(formula: String, ions: List<Pair<Ion, Int>>): String {
        var result = formula
        for ((ion, count) in ions) {
            val suffix = if (count > 1) "$count" else ""
            result = result.replace(Regex("""${ion.name}$suffix"""), "")
        }
        return result
    }

     fun flattenFormula(formula: String): Map<String, Int> {
        val stack = mutableListOf<MutableMap<String, Int>>()
        var current = mutableMapOf<String, Int>()
        var i = 0

        while (i < formula.length) {
            when (val c = formula[i]) {
                '(' -> {
                    stack.add(current)
                    current = mutableMapOf()
                    i++
                }
                ')' -> {
                    val (mult, len) = parseNumber(formula, i + 1)
                    i += len + 1
                    val top = stack.removeAt(stack.lastIndex)
                    for ((el, count) in current) {
                        top[el] = top.getOrDefault(el, 0) + count * mult
                    }
                    current = top
                }
                else -> {
                    val (element, len1) = parseElement(formula, i)
                    val (count, len2) = parseNumber(formula, i + len1)
                    current[element] = current.getOrDefault(element, 0) + count
                    i += len1 + len2
                }
            }
        }
        return current
    }

    private fun parseElement(s: String, i: Int): Pair<String, Int> {
        var end = i + 1
        if (i + 1 < s.length && s[i + 1].isLowerCase()) end++
        return s.substring(i, end) to (end - i)
    }

    private fun parseNumber(s: String, i: Int): Pair<Int, Int> {
        if (i >= s.length || !s[i].isDigit()) return 1 to 0
        var j = i
        while (j < s.length && s[j].isDigit()) j++
        return s.substring(i, j).toInt() to (j - i)
    }

    private fun solveOxidationStates(
        elementCounts: Map<String, Int>,
        totalCharge: Int
    ): Pair<Map<String, Int>,Int> {
        val known = mutableMapOf<String, Int>()
        val unknowns = mutableListOf<String>()
        var knownSum = 0

        for ((element, count) in elementCounts) {
            val ox = fixedStates[element]
            if (ox != null) {
                known[element] = ox
                knownSum += ox * count
            } else {
                unknowns.add(element)
            }
        }

        if (unknowns.isEmpty()) return Pair(known,-knownSum)

        if (unknowns.size == 1) {
            val el = unknowns[0]
            val count = elementCounts[el]!!
            val value = (totalCharge - knownSum) / count
            return Pair(known + (el to value),-(knownSum+value*count))
        }

        if (unknowns.size == 2) {
            val (e1, e2) = unknowns
            val (c1, c2) = listOf(elementCounts[e1]!!, elementCounts[e2]!!)
            val target = totalCharge - knownSum
            for (x in -10..10) {
                val remaining = target - c1 * x
                if (remaining % c2 == 0) {
                    val y = remaining / c2
                    return Pair(known + mapOf(e1 to x, e2 to y),-target)
                }
            }
            throw IllegalStateException("No integer solution found")
        }

        throw IllegalStateException("Too many unknowns")
    }
    fun buildOxidationDisplaySpannable(formula:String, oxidationMap :Map<String, Map<String, Int>> ): SpannableStringBuilder {
        data class ElementInfo(
            val element: String,
            val count: Int,
            val oxidationNumbers: List<Int>
        )

        val elementCounts = flattenFormula(formula.split("^")[0])
        val elements = elementCounts.map { (element, count) ->
            val oxMap = oxidationMap[element] ?: emptyMap()
            val values = oxMap.values.toSet().toList().sorted()
            ElementInfo(element, count, values)
        }

        val builder = SpannableStringBuilder()

        fun appendPipe() {
            val start = builder.length
            builder.append(" | ")
            builder.setSpan(
                ForegroundColorSpan(Color.GRAY),
                start,
                builder.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Line 1: oxidation numbers
        elements.forEachIndexed { i, e ->
            val oxString = e.oxidationNumbers.joinToString("/") { num ->
                if (num == 0) "0" else "${if (num > 0) "+" else ""}$num"
            }

            val padded = padCenter(oxString, cellWidth)
            val start = builder.length
            builder.append(padded)
            builder.setSpan(monoTypefaceSpan, start, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            builder.setSpan(SuperscriptSpan(), start, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            builder.setSpan(RelativeSizeSpan(0.7f), start, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Optional: color based on sign of the first oxidation number
            val color = when {
                e.oxidationNumbers.first() > 0 -> Color.RED
                e.oxidationNumbers.first() < 0 -> Color.BLUE
                else -> Color.DKGRAY
            }
            builder.setSpan(ForegroundColorSpan(color), start, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            if (i < elements.lastIndex) builder.append("|")
        }

        builder.append("\n")

// Line 2: element symbols
        elements.forEachIndexed { i, e ->
            val padded = padCenter(e.element, cellWidth)
            val start = builder.length
            builder.append(padded)
            builder.setSpan(monoTypefaceSpan, start, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            builder.setSpan(StyleSpan(Typeface.BOLD), start, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (i < elements.lastIndex) builder.append("|")
        }

        return builder
    }
    val cellWidth = 6
    val monoTypefaceSpan = TypefaceSpan("monospace")
    fun padCenter(text: String, width: Int): String {
        if (text.length >= width) return text
        val totalPadding = width - text.length
        val padStart = totalPadding / 2
        val padEnd = totalPadding - padStart
        return " ".repeat(padStart) + text + " ".repeat(padEnd)

    }
}
