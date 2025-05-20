package com.kharblabs.balancer.equationbalancer.dataManagers

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.SubscriptSpan
import com.kharblabs.balancer.equationbalancer.chemicalPlant.ElementColors

class StringMakers {

    fun converttoHTML(s: String): String {
        var s2 = ""
        //s= "Al2O3";
        val brokenMolecule =
            s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        for (x in brokenMolecule) {
            s2 =
                if (x.matches("\\d+".toRegex()) || x == ".") "$s2<sub><small><small>$x</small></small></sub>"
                else s2 + x
        }
        return s2
    }

    fun convertToSpannable_newCheck(s: String, digitColor: Int? = null, colorMap: Map<String, Int>): SpannableStringBuilder {
        val result = SpannableStringBuilder()
        val elementColors = ElementColors().elementColors
        // Regex: Match element symbols (e.g. H, He), numbers, and parentheses
        val regex = Regex("([A-Z][a-z]*|\\d+|\\(|\\))")
        val parts = regex.findAll(s)

        for (match in parts) {
            val part = match.value
            val start = result.length
            result.append(part)

            when {
                part.matches("\\d+".toRegex()) -> {
                    result.setSpan(SubscriptSpan(), start, result.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    result.setSpan(RelativeSizeSpan(0.75f), start, result.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    digitColor?.let {
                        result.setSpan(ForegroundColorSpan(it), start, result.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }

                elementColors.containsKey(part) -> {
                    colorMap[part]?.let {
                        result.setSpan(
                            ForegroundColorSpan(it),
                            start,
                            result.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
                }

        }

        return result
    }
    fun LatexMaker(LHS: ArrayList<String>, RHS: ArrayList<String>, Vals: IntArray): String {
        var latexOut = "$\$K_{c} = \\frac{"
        //NUmeratro
        var numeber = 0
        for (s in RHS) {
            latexOut += "["
            val elemets = splitString(s)
            for (i in elemets.indices) {
                latexOut += if (!containsDigit(elemets[i])) elemets[i]
                else "_{" + elemets[i] + "}"
            }
            latexOut += "]"

            latexOut += "^{" + Vals[numeber++] + "}"
        }
        latexOut += "}{"
        //RHS
        for (s in LHS) {
            latexOut += "["
            val elemets = splitString(s)
            for (i in elemets.indices) {
                latexOut += if (!containsDigit(elemets[i])) elemets[i]
                else "_{" + elemets[i] + "}"
            }
            latexOut += "]"

            latexOut += "^{" + Vals[numeber++] + "}"
        }
        latexOut += "}$$"

        return latexOut
    }


    /**
     * Applies subscript spans to digits following letters or ')' in chemical formulas.
     * E.g., H2O -> H₂O
     */
    fun applySubscriptSpans(str: String, spannable: SpannableString) {
        val regex = Regex("""(?<=[a-zA-Z)])(\d+)""")
        for (match in regex.findAll(str)) {
            spannable.setSpan(SubscriptSpan(), match.range.first, match.range.last + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(RelativeSizeSpan(0.75f), match.range.first, match.range.last + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    /**
     * Highlights the '↓' character (precipitate symbol) with increased size and color.
     */
    fun highlightDownArrow(str: String, spannable: SpannableString, color: Int) {
        var index = str.indexOf('↓')
        while (index >= 0) {
            spannable.setSpan(RelativeSizeSpan(1.5f), index, index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(ForegroundColorSpan(color), index, index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            index = str.indexOf('↓', index + 1)
        }
    }

    /**
     * Highlights matching reagent or product terms based on the '=' sign position in a chemical equation.
     */
    fun highlightSearchMatches(
        str: String,
        spannable: SpannableString,
        terms: List<String>,
        searchIn: SearchInEnum,
        color: Int
    ) {
        val eqIndex = str.indexOf('=')
        val lowerStr = str.lowercase()

        for (term in terms) {
            var index = lowerStr.indexOf(term)
            while (index >= 0) {
                val isMatch = (index < eqIndex && searchIn == SearchInEnum.BY_REAGENTS) ||
                        (index > eqIndex && searchIn == SearchInEnum.BY_PRODUCTS)
                if (isMatch) {
                    spannable.setSpan(BackgroundColorSpan(color), index, index + term.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                index = lowerStr.indexOf(term, index + 1)
            }
        }
    }

    /**
     * Highlights stoichiometric coefficients (e.g., the '2' in '2 H₂') with a given color.
     */
    fun highlightCoefficients(str: String, spannable: SpannableString, color: Int) {
        val regex = Regex("""(?<=^|\s|\+)(\d+)""")
        for (match in regex.findAll(str)) {
            spannable.setSpan(ForegroundColorSpan(color), match.range.first, match.range.last + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
    fun splitString(string: String): List<String> {
        val list: MutableList<String> = ArrayList()
        var token = ""
        var curr: Char
        var e = 0
        while (e < string.length + 1) {
            curr = if (e == 0) string[0]
            else {
                string[--e]
            }

            if (isNumber(curr)) {
                while (e < string.length && isNumber(string[e])) {
                    token += string[e++]
                }
                list.add(token)
                token = ""
            } else {
                while (e < string.length && !isNumber(string[e])) {
                    token += string[e++]
                }
                list.add(token)
                token = ""
            }

            e++
        }

        return list
    }

    fun containsDigit(s: String?): Boolean {
        var containsDigit = false

        if (!s.isNullOrEmpty()) {
            for (c in s.toCharArray()) {
                if (Character.isDigit(c).also { containsDigit = it }) {
                    break
                }
            }
        }

        return containsDigit
    }

    fun isNumber(c: Char): Boolean {
        return (c >= '0' && c <= '9') || c == '.'
    }
}