package com.kharblabs.equationbalancer2.chemicalPlant

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.TypefaceSpan

data class Ion(val name: String, val formula: String, val charge: Int, val oxidationStates: Map<String, Int>)

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
        Ion("Hypochlorite", "ClO", -1, mapOf("Cl" to +1, "O" to -2))
    )

    private val fixedStates = mapOf(
        "H" to +1, "O" to -2, "F" to -1,
        "Na" to +1, "K" to +1, "Li" to +1,
        "Ca" to +2, "Mg" to +2, "Ba" to +2,
        "Al" to +3, "Zn" to +2, "Ag" to +1
    )

    fun calculate(input: String): Map<String, Map<String, Int>> {
        val (rawFormula, netCharge) = extractCharge(input)
        val formula = replaceIonFormulasWithNames(rawFormula)

        val output = mutableMapOf<String, MutableMap<String, Int>>()

        val ionsFound = findPolyatomicIons(formula)
        val formulaRemaining = removeIonNames(formula, ionsFound)

        for ((ion, count) in ionsFound) {
            for ((element, ox) in ion.oxidationStates) {
                output.computeIfAbsent(element) { mutableMapOf() }
                    .compute(ion.name) { _, v -> v ?: ox }
            }
        }

        val atoms = flattenFormula(formulaRemaining)
        if (atoms.isEmpty()) return output
        val solved = try {
            solveOxidationStates(atoms, netCharge)
        } catch (e: Exception) {
            emptyMap()
        }

        for ((element, ox) in solved) {

            output.computeIfAbsent(element) { mutableMapOf() }[getNameFromSymbol(element)] = ox
        }

        return output
    }
    fun getNameFromSymbol(string: String): String
    {   val t= consts.elements.indexOf(string)
        if(t>-1)
            return consts.elementNames[t]
        return "--"
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
    ): Map<String, Int> {
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

        if (unknowns.isEmpty()) return known

        if (unknowns.size == 1) {
            val el = unknowns[0]
            val count = elementCounts[el]!!
            val value = (totalCharge - knownSum) / count
            return known + (el to value)
        }

        if (unknowns.size == 2) {
            val (e1, e2) = unknowns
            val (c1, c2) = listOf(elementCounts[e1]!!, elementCounts[e2]!!)
            val target = totalCharge - knownSum
            for (x in -10..10) {
                val remaining = target - c1 * x
                if (remaining % c2 == 0) {
                    val y = remaining / c2
                    return known + mapOf(e1 to x, e2 to y)
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
    fun buildOxidationDisplaySpannable2(formula:String, oxidationMap :Map<String, Map<String, Int>> ): SpannableStringBuilder {
        data class ElementInfo(
            val element: String,
            val count: Int,
            val oxidationNumber: Int
        )

        //val oxidationMap = calculate(formula)
        val elementCounts = flattenFormula(formula)

        val elements = elementCounts.map { (element, count) ->
            val oxMap = oxidationMap[element]
            val ox = oxMap?.get("overall") ?: oxMap?.values?.firstOrNull() ?: 0
            ElementInfo(element, count, ox)
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

        fun appendSubscriptedElement(e: ElementInfo) {
            builder.append(e.element)
            if (e.count > 1) {
                val start = builder.length
                builder.append(e.count.toString())
                builder.setSpan(SuperscriptSpan(), start, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                builder.setSpan(RelativeSizeSpan(0.7f), start, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        fun appendSuperscriptedOxNumber(ox: Int) {
            val oxStr = if (ox == 0) "0" else "${if (ox > 0) "+" else ""}$ox"
            val start = builder.length
            builder.append(oxStr)
            builder.setSpan(SuperscriptSpan(), start, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            builder.setSpan(RelativeSizeSpan(0.7f), start, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            builder.setSpan(
                ForegroundColorSpan(
                    when {
                        ox > 0 -> Color.RED
                        ox < 0 -> Color.BLUE
                        else -> Color.DKGRAY
                    }
                ),
                start,
                builder.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Line 1: Elements
        elements.forEachIndexed { i, e ->
            appendSubscriptedElement(e)
            if (i != elements.lastIndex) appendPipe()
        }

        builder.append("\n")

        // Line 2: Oxidation numbers
        elements.forEachIndexed { i, e ->
            appendSuperscriptedOxNumber(e.oxidationNumber)
            if (i != elements.lastIndex) appendPipe()
        }

        return builder
    }
}
