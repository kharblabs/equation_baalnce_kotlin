package com.kharblabs.equationbalancer2.dataManagers


data class MoleculeEntry(
    var name: String,
    var taken: Float,
    var molar: Float,
    var mole: Float,
    var residue: Float = 0f
)
