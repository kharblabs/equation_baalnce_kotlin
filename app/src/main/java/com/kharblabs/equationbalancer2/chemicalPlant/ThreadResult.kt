package com.kharblabs.equationbalancer2.chemicalPlant

import android.text.SpannableStringBuilder



class ThreadResult(
    var lHS: ArrayList<String>?,
    var rHS: ArrayList<String>?,
    var aLS: ArrayList<String>?,
    var resultType: Int,
    var resulting: SpannableStringBuilder?,
    var results: IntArray?,
    var error: String,
    var elementlist: Array<String>?,
    var beforeGCD: IntArray?
) {
    var valid: Boolean = false
    var LatexString: String = ""
    var source: String? = null
    var simplyfystring:String?=""

}
