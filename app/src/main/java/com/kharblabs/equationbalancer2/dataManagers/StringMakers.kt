package com.kharblabs.equationbalancer2.dataManagers

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