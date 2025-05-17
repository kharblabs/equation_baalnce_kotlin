package com.kharblabs.equationbalancer2.otherUtils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kharblabs.equationbalancer2.R
import com.kharblabs.equationbalancer2.chemicalPlant.ConstantValues
import com.kharblabs.equationbalancer2.elementer
import androidx.core.graphics.toColorInt

class PeriodicAdapter(
    private val context: Context,
    private val isBottom: Boolean,
) : RecyclerView.Adapter<PeriodicAdapter.ElementViewHolder>() {
    private val consts= ConstantValues()
    private val elements: Array<String> = consts.elements// context.resources.getStringArray(R.array.Elements)
    private val names: Array<String> = consts.elementNames // context.resources.getStringArray(R.array.elementNames)
    private val masses: Array<Float> = consts.roundedMasses// context.resources.getStringArray(R.array.masses)

    private val gridColor = arrayOf(

        "#e8db61"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#e8bb61"
        , "#d97e99"
        , "#c47ed9"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#74d47b"
        , "#d4b174"
        , "#d4b174"
        , "#d4b174"
        , "#d4b174"
        , "#e8bb61"
        , "#d97e99"
        , "#c47ed9"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#96d474"
        , "#74d47b"
        , "#d4b174"
        , "#d4b174"
        , "#d4b174"
        , "#e8bb61"
        , "#d97e99"
        , "#c47ed9"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#96d474"
        , "#74d47b"
        , "#74d47b"
        , "#d4b174"
        , "#d4b174"
        , "#e8bb61"
        , "#d97e99"
        , "#c47ed9"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#96d474"
        , "#96d474"
        , "#74d47b"
        , "#74d47b"
        , "#d4b174"
        , "#e8bb61"
        , "#d97e99"
        , "#c47ed9"
        , "#ffffff"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#96d474"
        , "#96d474"
        , "#96d474"
        , "#74d47b"
        , "#d4b174"
        , "#e8bb61"
        , "#d97e99"
        , "#c47ed9"
        , "#ffffff"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#96d474"
        , "#96d474"
        , "#96d474"
        , "#96d474"
        , "#d4b174"
        , "#e8bb61"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#ffffff"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db",
        "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
    )

    private val gridColorNight = arrayOf(
        "#e8db61"
        , "#232323"
        , "#232323", "#232323", "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#e8bb61"
        , "#d97e99"
        , "#c47ed9"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#74d47b"
        , "#d4b174"
        , "#d4b174"
        , "#d4b174"
        , "#d4b174"
        , "#e8bb61"
        , "#d97e99"
        , "#c47ed9"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#96d474"
        , "#74d47b"
        , "#d4b174"
        , "#d4b174"
        , "#d4b174"
        , "#e8bb61"
        , "#d97e99"
        , "#c47ed9"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#96d474"
        , "#74d47b"
        , "#74d47b"
        , "#d4b174"
        , "#d4b174"
        , "#e8bb61"
        , "#d97e99"
        , "#c47ed9"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#96d474"
        , "#96d474"
        , "#74d47b"
        , "#74d47b"
        , "#d4b174"
        , "#e8bb61"
        , "#d97e99"
        , "#c47ed9"
        , "#232323"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#96d474"
        , "#96d474"
        , "#96d474"
        , "#74d47b"
        , "#d4b174"
        , "#e8bb61"
        , "#d97e99"
        , "#c47ed9"
        , "#232323"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#74bcd4"
        , "#96d474"
        , "#96d474"
        , "#96d474"
        , "#96d474"
        , "#d4b174"
        , "#e8bb61"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#232323"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db",
        "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
        , "#84f5db"
    )
    var periodicGrid = buildPeriodicGrid(elements, names, masses)
    inner class ElementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val symbol = itemView.findViewById<TextView>(R.id.elementSymbol)
        val name = itemView.findViewById<TextView>(R.id.elementName)
        val symbolNumber = itemView.findViewById<TextView>(R.id.elementNumber)
        val weight = itemView.findViewById<TextView>(R.id.elementWeight)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElementViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.table_element, parent, false)
        return ElementViewHolder(view)
    }

    override fun getItemCount(): Int = periodicGrid.size

    override fun onBindViewHolder(holder: ElementViewHolder, position: Int) {
        val cell = periodicGrid[position]

        if (cell != null) {
            holder.symbol.text = cell.symbol
            holder.name.text = cell.name
            holder.symbolNumber.text = cell.number.toString()
            holder.weight.text = cell.mass.toString()

            holder.itemView.isClickable = true
            holder.itemView.visibility = View.VISIBLE

            val color = if (!isBottom) gridColor[position] else gridColorNight[position]
            holder.itemView.setBackgroundColor(color.toColorInt())

            holder.itemView.setOnClickListener {
                val intent = Intent(context, elementer::class.java)
                intent.putExtra("Element", cell.symbol)
                context.startActivity(intent)
            }
        } else {
            holder.symbol.text = ""
            holder.name.text = ""
            holder.symbolNumber.text = ""
            holder.weight.text = ""

            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            holder.itemView.isClickable = false
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.setOnClickListener(null)
        }
    }

    fun updateData(newGrid: List<PeriodicCell?>) {
        periodicGrid = newGrid
        notifyDataSetChanged()
    }
    data class PeriodicCell(
        val symbol: String,
        val name: String,
        val number: Int,
        val mass: Float
    )
    fun buildPeriodicGrid(
        symbols: Array<String>,
        names: Array<String>,

        masses: Array<Float>
    ): List<PeriodicCell?> {
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

        for (i in elements.indices) {
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