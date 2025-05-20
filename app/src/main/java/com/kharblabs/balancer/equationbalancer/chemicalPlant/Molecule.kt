package com.kharblabs.balancer.equationbalancer.chemicalPlant

class Molecule {
    var name: String
    var moles: Int
    var mass: Float
    var takenMass: Float
    var residue: Float

    constructor(name: String, moles: Int, mass: Float, takenMass: Float) {
        this.name = name
        this.moles = moles
        this.mass = mass
        this.takenMass = takenMass
        residue = 0f
    }

    constructor(name: String, moles: Int, mass: Float, takenMass: Float, residue: Float) {
        this.name = name
        this.moles = moles
        this.mass = mass
        this.takenMass = takenMass
        this.residue = residue
    }
}
