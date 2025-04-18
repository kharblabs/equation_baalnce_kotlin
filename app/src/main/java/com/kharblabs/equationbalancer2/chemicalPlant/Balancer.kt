package com.kharblabs.equationbalancer2.chemicalPlant

import android.text.Html
import android.text.SpannableStringBuilder
import com.kharblabs.equationbalancer2.dataManagers.StringMakers
import kotlin.math.abs

class Balancer  {
    var chemUtils =ChemUtils()
    val stringMakers = StringMakers()
    var equation :String = ""
    var stochiometry =Stochiometry()
    var LHS: ArrayList<String>? = ArrayList<String>()
    var RHS: ArrayList<String>? = ArrayList<String>()
    var ALS: ArrayList<String>? = ArrayList<String>()
    var allEqualSol =false
    fun  EquationCleaner(equation:String): sanitisedResult
    {   var s =equation
        s = s.replace("(?<![><!+-])[+]".toRegex(), " + ")
        s = s.replace("(?<![><!+-])[=]".toRegex(), " = ")

        s = s.replace("\\s{2,}".toRegex(), " ").trim { it <= ' ' }
        s = s.replace("(?<=[+ =])([0-9])(?=[^a-z])".toRegex(), "")
        val splitting =s.split(" ");
        val sr = sanitisedResult(s,"Check Equation", false)
        try {  // count plus and equals
            val c1: Int = s.count{it=='+'}
            val c2: Int = s.count{it=='='}

            if (c2 == 0) {
                sr.error = "Equal Sign Missing"
                return sr
            } else if (c2 > 1) {
                sr.error = "More than one Equal sign"
                return sr
            }
            var c3 = 0
            for (s in splitting) {
                if (s[0] != '+' && s[0] != '=') c3++
            }

            if (c3 == (c2 + c1 + 1)) {
                sr.error = ""
                sr.result = true
                return sr
            } else {
                sr.error = "Mismatched plus sign"
                sr.result = false
                return sr
            }
        } catch (e: Exception) {
            return sr
        }
    }

    fun balancer(equation: String) :ThreadResult
    {
        var balResult: sanitisedResult = sanitisedResult("", "NO EQN", false)
        var threadResult = ThreadResult(null, null, null, 0, null, null, "", null)
        var sanitised = EquationCleaner(equation)
        if (!sanitised.result) {
            threadResult.error=sanitised.error
            threadResult.resultType = 0
            return threadResult
        }
        try {
        //Split all
        var splitted = sanitised.cleanString.split(" ");
        ALS = ArrayList<String>(splitted.size)
        for (x in splitted) {
            ALS?.add(x)
        }

        //Convert All to LHS and RHS
        LHS = ArrayList<String>()
        RHS = ArrayList<String>()
        LHS!!.add(ALS!![0])
        var i = 1
        while (true) {
            if (i >= ALS!!.size) {
                break
            }
            val next = ALS!![i]
            if (next == "=") {
                ALS!!.removeAt(i)
                break
            } else if (next == "+") {
                ALS!!.removeAt(i)
                LHS!!.add(ALS!![i])
                i++
            }
        }
        if (i < ALS!!.size) {
            RHS!!.add(ALS!![i])
            i++
            while (true) {
                if (i >= ALS!!.size) {
                    break
                }
                val next = ALS!![i]
                if (next == "=") {
                    ALS!!.removeAt(i)
                    break
                } else if (next == null) {
                    threadResult.error="plus expected"
                    threadResult.resultType = 0
                    return threadResult
                } else if (next == "+") {
                    ALS!!.removeAt(i)
                    RHS!!.add(ALS!![i])
                    i++
                }
            }
        }
        //tv3.setText(s2);
        var lhsElements: HashMap<String, Float>? = HashMap()
        var RhsElements: HashMap<String, Float>? = HashMap()
        var allElments: HashMap<String, Float>? = HashMap()
        lhsElements = LHSElements(LHS)
        RhsElements = LHSElements(RHS)
        if (RhsElements == null || lhsElements == null) {
            threadResult.error=                "bracketError"
        }

        if (lhsElements!!.keys != RhsElements!!.keys) {
            threadResult.error="Different elements in products and reactants"
            threadResult.resultType = 3
            return threadResult
        }


        val eqn = ArrayList<String>()
        eqn.addAll(LHS!!)
        eqn.addAll(RHS!!)
        allElments = LHSElements(eqn)
        if (allElments == null) {
            threadResult.resultType = 0
            return threadResult
        }
        val list = java.util.ArrayList(allElments.keys)
        var mat = Matrix(allElments.size + 1, LHS!!.size + 1 + RHS!!.size)


        //buiding matrix

       i=0
        while (i < allElments.size) {
            var j = 0

            var k = 0
            while (k < LHS!!.size) {
                mat.setCell(i, j, chemUtils.AtomiseandFind(LHS!![k], list[i]).toDouble())
                j++
                k++
            }

            k = 0
            while (k < RHS!!.size) {
                mat.setCell(i, j, -chemUtils.AtomiseandFind(RHS!![k], list[i]).toDouble())
                j++
                k++
            }
            i++
        }

        val mat2: Matrix
        val mismatch = RHS!!.size + LHS!!.size - allElments.size - 1

        //  mismatch = Math.abs(mismatch);
        if (mismatch == 0 || mismatch < 0) {
            mat2 = mat
        } else if (mismatch > 0) {
            //mat.addRows()
            mat2 = Matrix(mat.rows + mismatch, mat.columns)

            for (x in 0..<mat.rows) for (y in 0..<mat.columns) {
                mat2.setCell(x, y, mat.m[x][y])
            }
            for (i2 in 0..<mismatch) {
                mat2.setCell(mat.rows - 1 + i2, i2 + 2, 1.0)
                mat2.setCell(mat.rows - 1 + i2, mat2.columns - 1, 1.0)
            }
        } else {
            mat2 = Matrix(mat.rows, mat.columns)

            for (x in 0..<mat.rows) for (y in 0..<mat.columns) {
                mat2.setCell(x, y, mat.m[x][y])
            }
            for (i2 in 0..<mismatch) {
                mat2.setCell(mat.rows - 1 + i2, i2 + 2, 1.0)
                mat2.setCell(mat.rows - 1 + i2, mat2.columns - 1, 1.0)
            }
        }
        val `as` = ""
        var matBack: Array<IntArray> = mat2.returnIntegerMat()
        mat2.solve(mat2)

        val solver = DoubleArray(mat2.rows)
        var z = 0.0
        if (mismatch != -1) {
            i = 0
            while (i < ALS!!.size) {
                solver[i] = mat2.m[i][mat2.columns - 1]
                z += abs(solver[i])
                i++
            }
        } else {
            i = 0
            while (i < ALS!!.size) {
                solver[i] = mat2.m[i][mat2.columns - 2]
                z += abs(solver[i])
                i++
            }
        }
        //println(allElments)
        var solAvail = true
        if (z == 0.0) {
            val matrixSolveIntOnly = MatrixSolveIntOnly(matBack)
            var test = Array<IntArray>(mat.rows) { IntArray(mat.columns) }

            test = matrixSolveIntOnly.resultingMat
            val nosol: Int = matrixSolveIntOnly.noSol
            if (nosol == 0) {
                i = 0
                while (i < allElments.size) {
                    solver[i] = test[i][mat.columns - 1].toDouble()
                    z += abs(solver[i])
                    i++
                }
            } else {
                threadResult.error="All Zero Solution, Check Equation"
                threadResult.resultType = 2
                return threadResult
            }
        }
        //additonal checker
        if (solver[allElments.size - 1] == 0.0) {
            solver[allElments.size - 1] = 1.0
        }
        var counter = 0
        {
            i = 0
            while (i < ALS!!.size) {
                solver[i] = abs(solver[i])
                i++
            }
        }
        for (x in solver) {
            counter++
            if (counter == ALS!!.size) {
                break
            }
            if (x == 0.0) {
                var aa = true
                for (i1 in ALS!!.indices) {
                    if (solver[i1] != 1.0) {
                        aa = false
                        break
                    }
                }
                if (!aa) {
                    threadResult.error="No Solution"
                    solAvail = false
                } else {
                    solAvail = true
                    allEqualSol = true
                }
                break
            }
            if (x < 0) {
                /**  threadResult.setError(context.getResources().getString(R.string.utla_rxn)); */

                allEqualSol = false
                solAvail = true
                //   threadResult.resultType = 0;
                break
            }
        }

        if (solAvail) {
            val denoms = IntArray(ALS!!.size)
            var lcm = 1
            val newLength = if (ALS!!.size == solver.size) solver.size
            else ALS!!.size


            for (ai in 0..<newLength) {
                denoms[ai] = mat2.GetDenom(solver[ai])
                lcm = lcm * mat2.GetDenom(solver[ai])
            }
            for (a2 in 0..<newLength) {
                solver[a2] = solver[a2] * lcm
            }
            var solver2 = IntArray(solver.size)
            var ia = 0
            for (x in solver) {
                solver2[ia] = Math.round(solver[ia]).toInt()
                ia++
            }
            val gc: Int = findGcd(solver2)
            for (a2 in 0..<newLength) {
                solver2[a2] = solver2.get(a2) / gc
            }

            // Buliding final string

            threadResult.resultType = 1
            val els = allElments.keys.toTypedArray<String>()
            threadResult.elementlist = els
            threadResult.resulting = null
            threadResult.simplyfystring=    simpleStringResultBuilder(solver,solver2,newLength,mat2)
            threadResult.aLS = ALS
            threadResult.lHS = LHS
            threadResult.rHS = RHS
            threadResult.results = solver2
            threadResult.error = "Error"
            threadResult.valid = chemUtils.Validater(RHS, LHS, solver2)
            threadResult.source = equation.replace(" ", "")
            threadResult.LatexString = stringMakers.LatexMaker(LHS!!, RHS!!, solver2)
            return threadResult
        }


        threadResult.source = equation

        // Invalid Elements
        var invalElem = ""
        val els = allElments.keys.toTypedArray<String>()
        for (oowie in els.indices) {
            val massa: Float = stochiometry.getAtMass(els[oowie])
            if (massa > 0) {
            } else {
                invalElem += els[oowie] + ","
            }
        }

        //if()
        threadResult.error = invalElem
        threadResult.resultType = 2
        return threadResult
        }
        catch (e :Exception)
        {
          threadResult.source = equation;
          threadResult.resultType = 0;

          return threadResult
         }

    }
    fun simpleStringResultBuilder(solver: DoubleArray, solver2:IntArray, newLength:Int, mat2:Matrix): String
    {var biulter  :String = ""
        var i=0
        if (!allEqualSol) {
            i = 0
            while (i < ALS!!.size) {
                biulter+="  "
                if (solver[i].toInt() != 1) {
                    val a: Int = solver2[i]
                    biulter+=a
                }

                biulter+=ALS!![i]
                if (i < LHS!!.size - 1 || ((i > LHS!!.size - 1) && i < ALS!!.size - 1)) biulter+= " +"
                if (i == LHS!!.size - 1) biulter+="  =  "
                i++
            }
        } else {
            i = 0
            while (i < newLength) {
                biulter+="  "
                if (solver[i].toInt() != 1) {
                    val a: Int = solver2.get(i)
                    biulter+=a
                }

                biulter+=ALS!![i]
                if (i < LHS!!.size - 1 || ((i > LHS!!.size - 1) && i < mat2.rows - 1)) biulter+= " +"
                if (i == LHS!!.size - 1) biulter+="  =  "
                i++
            }
        }
    return biulter
    }
    fun buildspan(solver: IntArray,solver2:IntArray,newLength:Int,mat2:Matrix)
    {val biulter = SpannableStringBuilder("")
        var i=0
        if (!allEqualSol) {
            i = 0
            while (i < ALS!!.size) {
                biulter.append("  ")
                if (solver[i].toInt() != 1) {
                    val a: Int = solver2.get(i)
                    biulter.append(Html.fromHtml("<b>$a</b>"))
                }

                biulter.append(Html.fromHtml(stringMakers.converttoHTML(ALS!![i])))
                if (i < LHS!!.size - 1 || ((i > LHS!!.size - 1) && i < ALS!!.size - 1)) biulter.append(
                    " +"
                )
                if (i == LHS!!.size - 1) biulter.append("  =  ")
                i++
            }
        } else {
            i = 0
            while (i < newLength) {
                biulter.append("  ")
                if (solver[i].toInt() != 1) {
                    val a: Int = solver2.get(i)
                    biulter.append(Html.fromHtml("<b>$a</b>"))
                }

                biulter.append(Html.fromHtml(stringMakers.converttoHTML(ALS!![i])))
                if (i < LHS!!.size - 1 || ((i > LHS!!.size - 1) && i < mat2.rows - 1)) biulter.append(
                    " +"
                )
                if (i == LHS!!.size - 1) biulter.append("  =  ")
                i++
            }
        }}

    fun LHSElements(lhs: ArrayList<String>?): HashMap<String, Float>? {
        try {
            val lhsElements = ArrayList<element>()
            val ElementsAS = ArrayList<element>()
            var TemplhsElements: ArrayList<element>? = ArrayList()
            //Copy all elements from each word in lHS, add to one
            if (lhs != null) {
                for (s in lhs) {
                    TemplhsElements = chemUtils.elementParser(s)
                    if (TemplhsElements == null) {

                        break
                    }
                    for (e in TemplhsElements) lhsElements.add(e)
                }
            }

            val uniq: Set<element> = HashSet(lhsElements)
            val map = HashMap<String, Float>()
            for (e in lhsElements) {
                if (map.containsKey(e.name)) {
                    val x = map[e.name]!!
                    map.remove(e.name)
                    map[e.name] = x + e.count
                } else {
                    map[e.name] = e.count
                }
            }
            val entrySet: Set<Map.Entry<String, Float>> = map.entries
            val listOfEntry = java.util.ArrayList(entrySet)
            for ((key, value) in listOfEntry) {
                ElementsAS.add(element(key.toString(), ((value as Float)!!)))
            }

            return map
        } catch (e: java.lang.Exception) {
            return null
        }

        //      }    return  ElementsAS;
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



class sanitisedResult(var cleanString:String,var error: String, var result: Boolean)
}