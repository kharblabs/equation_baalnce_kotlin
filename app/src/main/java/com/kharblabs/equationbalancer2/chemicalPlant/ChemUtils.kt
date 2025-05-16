package com.kharblabs.equationbalancer2.chemicalPlant

import android.util.Log
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.data.PieEntry

class ChemUtils {
    public var s22:String=""

    fun BracketBreaker(s1: String): String? {
        var s=s1
        val left = IntArray(s.length)
        if (s[s.length-1] == ')')
        {
            s=s+"1"
        }
        var k = 0
        s22 = ""
        var numb = ""
        var Opencounter = 0
        for (i in s.indices) {
            if (s[i] == '(') {
                left[k++] = i
                Opencounter++
            }
        }
        val right = IntArray(s.length)
        val rightPlus = IntArray(s.length)
        var Closecounter = 0
        k = 0
        for (i in s.indices) {
            if (s[i] == ')') {
                Closecounter++
                right[k] = i
                var t = i
                try {
                    if (Character.isDigit(s[++t])) {
                        numb += s[t]
                        t++

                        //Do not remove or it doesn't save numb
                        try {
                            while (Character.isDigit(s[t])) {
                                numb += s[t]
                                if (t < s.length) break
                            }
                        } catch (e: Exception) {
                        }
                        rightPlus[k] = numb.toInt()
                    } else rightPlus[k] = 1
                    numb = ""
                } catch (e: Exception) {
                }
                k++
            }
        }
        // opened =cosed, all good
        if (Opencounter == Closecounter) {
            // Log.e(null,"All correct");
            if (right[0] < left[0]) {
                //  threadResult.setError(context.getResources().getString(R.string.bracket_err));
                return null
            }
            val x = GroupAtomiser(s, Closecounter, left, right, rightPlus)
            return x
        } else {
            // threadResult.setError(context.getResources().getString(R.string.bracket_err));
            return null
        }
    }
    fun GroupAtomiser(        s: String,        bracketCounter: Int,        left: IntArray?,        right: IntArray,        mult: IntArray    ): String {
        var sCop = s
        var temp = ""
        var tempMultiplied = ""
        for (i in 0..<bracketCounter) {
            temp = sCop.substring(findOpenParen(sCop.toCharArray(), right[i]) + 1, right[i])
            val templ = ArrayList<String>()

            //Splice sting , get int and multipy it
            var formula = temp

            //insert "1" in atom-atom boundry
            formula =
                formula.replace("(?<=[A-Z])(?=[A-Z])|(?<=[a-z])(?=[A-Z])|(?<=\\D)$".toRegex(), "1")

            //split at letter-digit or digit-letter boundry
            val regex = "(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)"
            val atoms = formula.split(regex.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var x: Int
            var y = 1
            while (y < atoms.size) {
                x = atoms[y].toInt()
                x *= mult[i]
                atoms[y] = x.toString()
                y += 2
            }
            for (z in atoms.indices) {
                tempMultiplied += atoms[z]
            }
            //tempMultiplied=temp.replace("\d+/"g, digit => digit * num)
            val len1 = temp.length
            val len2 = tempMultiplied.length
            var correctorifnoMUltiplier = 0
            if (sCop.contains(("(" + temp + ")" + mult[i]))) sCop =
                sCop.replace("(" + temp + ")" + mult[i], tempMultiplied)
            else {
                sCop = sCop.replace("($temp)", tempMultiplied)
                correctorifnoMUltiplier = mult[i].toString().length
            }
            tempMultiplied = ""
            val delta = len2 - len1 - 2 - mult[i].toString().length - correctorifnoMUltiplier
            for (z in i..<bracketCounter) right[z] += delta
        }
        return sCop
    }

    fun findOpenParen(text: CharArray, closePos: Int): Int {
        var openPos = closePos
        var counter = 1
        while (counter > 0) {
            val c = text[--openPos]
            if (c == '(') {
                counter--
            } else if (c == ')') {
                counter++
            }
        }
        return openPos
    }



    fun elementParser(s2: String?): ArrayList<element>? {
        var s = s2
        try {
            var e: element

            if (s!!.contains("(") || s.contains(")")) {
                s = BracketBreaker(s)
                if (s == null) return null
            }
            val sss =
                s.split("(?=\\p{Upper})".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val elems = ArrayList<String>(sss.size)
            val elementsArrayList = ArrayList<element>()
            for (xs in sss) {
                if (xs != "") {
                    elems.add(xs)
                }
            }
            var skippadd = false
            if (elems.isNotEmpty()) {
                for (x in elems.indices) {
                    var n = elems[x].replace("\\d".toRegex(), "")
                    n = n.replace(".", "")
                    var count: Float = numparse(elems[x])
                    //BUG fix for multiple occurance fo same elemnt, check if it already esists
                    for (i in elementsArrayList.indices) {
                        if (elementsArrayList[i].name.equals(n)) {
                            count += elementsArrayList[i].count
                            elementsArrayList.removeAt(i)
                            elementsArrayList.add(element(n, count))
                            skippadd = true
                            break
                        }
                    }
                    if (!skippadd)
                        elementsArrayList.add(element(n, count))
                    skippadd = false
                }
                return elementsArrayList
            } else return null
        } catch (e: java.lang.Exception) {
            Log.e("D","imprpoer")
            return null
        }

    }
    fun numparse(s: String): Float {
        val reg = "/^[0-9]+$/"
        val f = s.replace(".*?([\\d.]+).*".toRegex(), "$1")
        var i = 0f
        try {
            i = f.toFloat()
        } catch (e: java.lang.Exception) {
        }
        return if (i != 0f) i
        else 1f
    }

    fun AtomiseandFind(s: String?, el: String?): Float {
        val ae = elementParser(s)
        for (x in ae!!) {
            if (x.name == el)
                return x.count
        }
        return 0f
    }
    fun getMoleculeMass(s: String?): Float {
        var eContent =elementParser(s)
        var mass = 0.0f
        var thismass = 0.0f
        if (eContent != null) {
            for (e in eContent) {
                var stochiometry = Stochiometry()
                thismass = stochiometry.getAtMass(e.name)
                if (thismass > 0) {
                    mass += e.count * thismass
                } else {

                    return 0f
                }
            }
        }
        return mass
    }
    fun getMoleculeMassPieChart(s : String?): List<PieEntry> {
        var results=ArrayList<PieEntry>()
        var eContent =elementParser(s)
        var mass = 0.0f
        var thismass = 0.0f
        if (eContent != null) {
            for (e in eContent) {
                var stochiometry = Stochiometry()
                thismass = stochiometry.getAtMass(e.name)
                if (thismass > 0) {
                    mass += e.count * thismass
                    results.add(PieEntry(((e.count * thismass)), e.name));
                } else {

                    return results
                }
            }
        }
        return results
    }
    fun Validater(    RHS: ArrayList<String>?,    LHS: ArrayList<String>?,    solver: IntArray): Boolean {
        val R_Hash = java.util.HashMap<String, Float>()
        val L_Hash = java.util.HashMap<String, Float>()
        //HashMap<String,Float> R_Hash= new HashMap<>();
        val eContentL = java.util.ArrayList<element>()
        var num = 0
        if (LHS != null) {
            for (i in LHS.indices) {
                val reacts = LHS[i]
                val els = elementParser(reacts)
                if (els != null) {
                    for (t in els.indices) {
                        els[t] = element(els[t].name, solver[i] * els[t].count)
                    }

                    for (e in els) {
                        if (L_Hash.containsKey(e.name)) {
                            L_Hash[e.name] = L_Hash[e.name]!! + e.count
                        } else L_Hash[e.name] = e.count
                    }
                }
                //num=i;
                num++

            }
        }

        if (RHS != null) {
            for (i in RHS.indices) {
                val reacts = RHS[i]
                val els =elementParser(reacts)
                if (els != null) {
                    for (t in els.indices) {
                        els[t] = element(els[t].name, solver[num] * els[t].count)
                    }

                    num++
                    for (e in els) {
                        if (R_Hash.containsKey(e.name)) {
                            R_Hash[e.name] = R_Hash[e.name]!! + e.count
                        } else R_Hash[e.name] = e.count
                    }
                }
            }
        }

        return R_Hash.entries == L_Hash.entries
    }
}