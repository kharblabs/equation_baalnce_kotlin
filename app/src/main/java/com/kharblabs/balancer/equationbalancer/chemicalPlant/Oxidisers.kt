package com.kharblabs.balancer.equationbalancer.chemicalPlant

class Oxidisers {
    val oxidationNumbers = mutableMapOf<String, Int>()

    // Define rules for common atoms
    val atomicOxidationNumbers = mapOf(
        "H" to 1,      // Hydrogen is +1 when bonded to non-metals
        "O" to -2,     // Oxygen is usually -2
        "F" to -1,     // Fluorine is -1
        "Li" to +1,    // Lithium (alkali metal) is +1
        "Na" to +1,    // Sodium (alkali metal) is +1
        "K" to +1,     // Potassium (alkali metal) is +1
        "Rb" to +1,    // Rubidium (alkali metal) is +1
        "Cs" to +1,    // Cesium (alkali metal) is +1
        "Be" to +2,    // Beryllium (alkaline earth metal) is +2
        "Mg" to +2,    // Magnesium (alkaline earth metal) is +2
        "Ca" to +2,    // Calcium (alkaline earth metal) is +2
        "Sr" to +2,    // Strontium (alkaline earth metal) is +2
        "Ba" to +2,    // Barium (alkaline earth metal) is +2
        "Al" to 3,     // Aluminum's most common oxidation state is +3
        "Cl" to -1,    // Chlorine (halogen) is typically -1
        "Br" to -1,    // Bromine (halogen) is typically -1
        "I" to -1,     // Iodine (halogen) is typically -1
        "S" to -2,     // Sulfur (commonly -2, +4, +6)
        "N" to -3,     // Nitrogen (commonly -3, +3, +5)
        "C" to +4,     // Carbon (commonly +4)
        "P" to +5,     // Phosphorus (commonly +5)
        "Cu" to +2,    // Copper (commonly +2)
        "Zn" to +2,    // Zinc (commonly +2)
        "Mn" to +2,    // Manganese (commonly +2)
        "Ni" to +2     // Nickel (commonly +2)
    )

    // Define elements with multiple oxidation states
    val elementsWithMultipleOxidationStates = mapOf(
        "Fe" to setOf(2, 3),  // Iron (Fe) can be +2 or +3
        "Cu" to setOf(1, 2),  // Copper (Cu) can be +1 or +2
        "N" to setOf(-3, 3, 5)  // Nitrogen (N) can be -3, +3, or +5
    )

    // Define polyatomic ions with their oxidation states
    val polyatomicIons = mapOf(
        "SO4" to Pair(-2, 2),  // Sulfate ion (SO4^2-), sulfur is +6, oxygen is -2
        "NO3" to Pair(-1, 1),  // Nitrate ion (NO3^-), nitrogen is +5, oxygen is -2
        "NH4" to Pair(1, 1),   // Ammonium ion (NH4+), nitrogen is -3, hydrogen is +1
        "CO3" to Pair(-2, 2),  // Carbonate ion (CO3^2-), carbon is +4, oxygen is -2
        "PO4" to Pair(-3, 3),  // Phosphate ion (PO4^3-), phosphorus is +5, oxygen is -2
        "CH3COO" to Pair(-1, 2), // Acetate ion (CH3COO^-), carbon is +3, oxygen is -2, hydrogen is +1
        "OH" to Pair(-1, 1),   // Hydroxide ion (OH^-), oxygen is -2, hydrogen is +1
        "ClO3" to Pair(-2, 3), // Chlorate ion (ClO3^-), chlorine is +5, oxygen is -2
        "MnO4" to Pair(-1, 1), // Permanganate ion (MnO4^-), manganese is +7, oxygen is -2
        "Cr2O7" to Pair(-2, 2), // Dichromate ion (Cr2O7^2-), chromium is +6, oxygen is -2
        "NO2" to Pair(-1, 2),   // Nitrite ion (NO2^-), nitrogen is +3, oxygen is -2
        "C2O4" to Pair(-2, 2),  // Oxalate ion (C2O4^2-), carbon is +3, oxygen is -2
        "ClO" to Pair(-1, 1),   // Hypochlorite ion (ClO^-), chlorine is +1, oxygen is -2
        "BrO3" to Pair(-2, 3),  // Bromate ion (BrO3^-), bromine is +5, oxygen is -2
        "IO4" to Pair(-2, 4),   // Periodate ion (IO4^-), iodine is +7, oxygen is -2
        "SCN" to Pair(-1, 1),   // Thiocyanate ion (SCN^-), sulfur is -2, carbon is +4, nitrogen is -3
        "CN" to Pair(-3, 1),    // Cyanide ion (CN^-), carbon is +4, nitrogen is -3
        "N3" to Pair(-3, 3),    // Azide ion (N3^-), nitrogen is -3
        "CrO4" to Pair(-2, 2),  // Chromate ion (CrO4^2-), chromium is +6, oxygen is -2
        "F3C" to Pair(-1, 3),   // Trifluoromethyl ion (F3C^-), carbon is +4, fluorine is -1
        "C4H4O6" to Pair(-2, 4), // Tartrate ion (C4H4O6^2-), carbon is +3, oxygen is -2, hydrogen is +1
        "PO3" to Pair(-3, 3),   // Phosphite ion (PO3^3-), phosphorus is +3, oxygen is -2
        "H2S" to Pair(-2, 2),   // Hydrosulfide ion (H2S), sulfur is -2, hydrogen is +1
        "Na4P2O7" to Pair(-2, 2), // Pyrophosphate ion (Na4P2O7), phosphorus is +5, oxygen is -2
        "B4O7" to Pair(-2, 4),  // Tetraborate ion (B4O7^2-), boron is +3, oxygen is -2
        "Cr2O4" to Pair(-2, 2), // Chromyl ion (Cr2O4^2-), chromium is +6, oxygen is -2
        "AlO2" to Pair(-2, 1),  // Aluminate ion (AlO2^-), aluminum is +3, oxygen is -2
        "C2H5O2" to Pair(-1, 2),  // Ethanoate ion (C2H5O2^-), carbon is +3, oxygen is -2, hydrogen is +1
        "NO" to Pair(-1, 1)     // Nitric oxide ion (NO^-), nitrogen is +3, oxygen is -2
    )

}