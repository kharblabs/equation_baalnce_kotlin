package com.kharblabs.equationbalancer2

import android.app.Application
import com.kharblabs.equationbalancer2.chemicalPlant.ConstantValues
import com.kharblabs.equationbalancer2.search_files.AppDatabase
import com.kharblabs.equationbalancer2.search_files.ChemistryRepository

/**
 * Custom Application class to initialize the database
 */
class ChemistryApplication : Application() {
    // Lazy initialization of the database
    val database by lazy { AppDatabase.Companion.getDatabase(this) }

    // Repository can be accessed from anywhere in the app
    val repository by lazy { ChemistryRepository(database) }

    val periodicGrid: List<PeriodicCell?> by lazy {
        buildPeriodicGrid()
    }

    data class PeriodicCell(
        val symbol: String,
        val name: String,
        val number: Int,
        val mass: Float
    )
    val consts= ConstantValues()
    fun buildPeriodicGrid(

    ): List<PeriodicCell?> {
       val symbols = consts.elements//arrayOf("H", "He", "Li", "Be", "B", "C", "N", "O", "F", "Ne"): Array<String>,
       val  names=consts.elementNames//: Array<String>,

        val masses=consts.roundedMasses//: Array<Float>
        val gridSize = 180
        val grid = MutableList<PeriodicCell?>(gridSize) { null }

        // Map of atomicNumber to grid index (based on periodic table layout)
        val positionMap = mapOf(
            // Period 1
            1 to 0, 2 to 17,

            // Period 2
            3 to 18, 4 to 19, 5 to 30, 6 to 31, 7 to 32, 8 to 33, 9 to 34, 10 to 35,

            // Period 3
            11 to 36, 12 to 37, 13 to 48, 14 to 49, 15 to 50, 16 to 51, 17 to 52, 18 to 53,

            // Period 4
            19 to 54, 20 to 55,
            21 to 56, 22 to 57, 23 to 58, 24 to 59, 25 to 60, 26 to 61, 27 to 62, 28 to 63, 29 to 64, 30 to 65,
            31 to 66, 32 to 67, 33 to 68, 34 to 69, 35 to 70, 36 to 71,

            // Period 5
            37 to 72, 38 to 73,
            39 to 74, 40 to 75, 41 to 76, 42 to 77, 43 to 78, 44 to 79, 45 to 80, 46 to 81, 47 to 82, 48 to 83,
            49 to 84, 50 to 85, 51 to 86, 52 to 87, 53 to 88, 54 to 89,

            // Period 6
            55 to 90, 56 to 91,
            // Lanthanides block (shown separately)
            57 to 147, 58 to 148, 59 to 149, 60 to 150, 61 to 151, 62 to 152,
            63 to 153, 64 to 154, 65 to 155, 66 to 156, 67 to 157, 68 to 158,
            69 to 159, 70 to 160, 71 to 161,

            72 to 92, 73 to 93, 74 to 94, 75 to 95, 76 to 96, 77 to 97,
            78 to 98, 79 to 99, 80 to 100, 81 to 101, 82 to 102, 83 to 103,
            84 to 104, 85 to 105, 86 to 106,

            // Period 7
            87 to 107, 88 to 108,
            // Actinides block
            89 to 165, 90 to 166, 91 to 167, 92 to 168, 93 to 169, 94 to 170,
            95 to 171, 96 to 172, 97 to 173, 98 to 174, 99 to 175, 100 to 176,
            101 to 177, 102 to 178, 103 to 179,

            104 to 109, 105 to 110, 106 to 111, 107 to 112, 108 to 113, 109 to 114,
            110 to 115, 111 to 116, 112 to 117, 113 to 118, 114 to 119, 115 to 120,
            116 to 121, 117 to 122, 118 to 123
        )

        for (i in symbols.indices) {
            val atomicNumber = i
            val position = positionMap[atomicNumber]

            if (position != null && position in 0 until gridSize) {
                grid[position] = PeriodicCell(
                    symbol = symbols[i],
                    name = names[i],
                    number = atomicNumber,
                    mass = masses[i]
                )
            }
        }

        return grid
    }
}