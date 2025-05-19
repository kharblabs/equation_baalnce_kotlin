package com.kharblabs.equationbalancer2.chemicalPlant

import android.graphics.Color
import androidx.core.graphics.toColorInt

class ElementColors {
    val elementGroupColors = mapOf(
        "Alkali Metal" to "#EC407A".toColorInt(),       // Deep Rose
        "Alkaline Earth Metal" to "#AB47BC".toColorInt(), // Deep Lavender
        "Transition Metal" to "#5C6BC0".toColorInt(),   // Indigo Blue
        "Post-Transition Metal" to "#26A69A".toColorInt(), // Teal
        "Metalloid" to "#8D6E63".toColorInt(),          // Earthy Brown
        "Nonmetal" to "#7CB342".toColorInt(),           // Olive Green
        "Halogen" to "#FDD835".toColorInt(),            // Rich Yellow
        "Noble Gas" to "#29B6F6".toColorInt(),          // Sky Blue
        "Lanthanide" to "#FFB74D".toColorInt(),         // Vibrant Orange
        "Actinide" to "#FF8A65".toColorInt(),
        "Unknown" to "#000000".toColorInt()
    )
    val elementCategories = mapOf(
        // 1–2
        "H" to "Alkaline Earth Metal", "He" to "Noble Gas",
        // 3–10
        "Li" to "Alkali Metal", "Be" to "Alkaline Earth Metal", "B" to "Metalloid", "C" to "Nonmetal", "N" to "Nonmetal", "O" to "Nonmetal", "F" to "Halogen", "Ne" to "Noble Gas",
        // 11–18
        "Na" to "Alkali Metal", "Mg" to "Alkaline Earth Metal", "Al" to "Post-Transition Metal", "Si" to "Metalloid", "P" to "Nonmetal", "S" to "Nonmetal", "Cl" to "Halogen", "Ar" to "Noble Gas",
        // 19–36
        "K" to "Alkali Metal", "Ca" to "Alkaline Earth Metal", "Sc" to "Transition Metal", "Ti" to "Transition Metal", "V" to "Transition Metal", "Cr" to "Transition Metal",
        "Mn" to "Transition Metal", "Fe" to "Transition Metal", "Co" to "Transition Metal", "Ni" to "Transition Metal", "Cu" to "Transition Metal", "Zn" to "Transition Metal",
        "Ga" to "Post-Transition Metal", "Ge" to "Metalloid", "As" to "Metalloid", "Se" to "Nonmetal", "Br" to "Halogen", "Kr" to "Noble Gas",
        // 37–54
        "Rb" to "Alkali Metal", "Sr" to "Alkaline Earth Metal", "Y" to "Transition Metal", "Zr" to "Transition Metal", "Nb" to "Transition Metal", "Mo" to "Transition Metal",
        "Tc" to "Transition Metal", "Ru" to "Transition Metal", "Rh" to "Transition Metal", "Pd" to "Transition Metal", "Ag" to "Transition Metal", "Cd" to "Transition Metal",
        "In" to "Post-Transition Metal", "Sn" to "Post-Transition Metal", "Sb" to "Metalloid", "Te" to "Metalloid", "I" to "Halogen", "Xe" to "Noble Gas",
        // 55–86
        "Cs" to "Alkali Metal", "Ba" to "Alkaline Earth Metal", "La" to "Lanthanide", "Ce" to "Lanthanide", "Pr" to "Lanthanide", "Nd" to "Lanthanide", "Pm" to "Lanthanide",
        "Sm" to "Lanthanide", "Eu" to "Lanthanide", "Gd" to "Lanthanide", "Tb" to "Lanthanide", "Dy" to "Lanthanide", "Ho" to "Lanthanide", "Er" to "Lanthanide",
        "Tm" to "Lanthanide", "Yb" to "Lanthanide", "Lu" to "Lanthanide",
        "Hf" to "Transition Metal", "Ta" to "Transition Metal", "W" to "Transition Metal", "Re" to "Transition Metal", "Os" to "Transition Metal", "Ir" to "Transition Metal",
        "Pt" to "Transition Metal", "Au" to "Transition Metal", "Hg" to "Transition Metal", "Tl" to "Post-Transition Metal", "Pb" to "Post-Transition Metal", "Bi" to "Post-Transition Metal",
        "Po" to "Metalloid", "At" to "Halogen", "Rn" to "Noble Gas",
        // 87–118
        "Fr" to "Alkali Metal", "Ra" to "Alkaline Earth Metal", "Ac" to "Actinide", "Th" to "Actinide", "Pa" to "Actinide", "U" to "Actinide", "Np" to "Actinide",
        "Pu" to "Actinide", "Am" to "Actinide", "Cm" to "Actinide", "Bk" to "Actinide", "Cf" to "Actinide", "Es" to "Actinide", "Fm" to "Actinide",
        "Md" to "Actinide", "No" to "Actinide", "Lr" to "Actinide",
        "Rf" to "Transition Metal", "Db" to "Transition Metal", "Sg" to "Transition Metal", "Bh" to "Transition Metal", "Hs" to "Transition Metal", "Mt" to "Unknown",
        "Ds" to "Unknown", "Rg" to "Unknown", "Cn" to "Unknown", "Fl" to "Post-Transition Metal", "Lv" to "Unknown", "Ts" to "Unknown", "Og" to "Noble Gas"
    )
    val defaultColor = Color.RED
    val elementColors = elementCategories.mapValues { (element, category) ->
        elementGroupColors[category] ?: defaultColor
    }



    fun assignPastelColors(strings: List<String>?): Map<String, Int> {
        if (strings.isNullOrEmpty()) return emptyMap()

        val pastelHexColors = listOf(
            "#A3C9F9", // Pastel Blue
            "#B6E2A1", // Pastel Green
            "#F7C1D9", // Pastel Pink
            "#CDB4DB", // Pastel Lavender
            "#FFF5BA", // Pastel Yellow
            "#FFB5A7", // Pastel Coral
            "#AAF0D1", // Pastel Mint
            "#FFD6A5", // Pastel Orange
            "#D7BDE2", // Pastel Purple
            "#A0E7E5"  // Pastel Teal
        )

        val darkenFactor = 0.9f  // Adjust this between 0.6–0.9 for stronger or lighter darkening

        fun darkenColor(hex: String): Int {
            val color = hex.toColorInt()
            val r = (Color.red(color) * darkenFactor).toInt().coerceIn(0, 255)
            val g = (Color.green(color) * darkenFactor).toInt().coerceIn(0, 255)
            val b = (Color.blue(color) * darkenFactor).toInt().coerceIn(0, 255)
            return Color.rgb(r, g, b)
        }

        val pastelColors = elementGroupColors.map { it.value } // pastelHexColors.map { darkenColor(it) }

        return strings.mapIndexed { index, value ->
            val color = pastelColors[index % pastelColors.size]
            value to color
        }.toMap()
    }
}