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
import com.kharblabs.equationbalancer2.elementer

class PeriodicAdapter(
    private val context: Context,
    private var periodicGrid: List<PeriodicCell?> = emptyList(),
    private val isBottom: Boolean,
    private val gridColor: List<String>,
    private val gridColorNight: List<String>
) : RecyclerView.Adapter<PeriodicAdapter.ElementViewHolder>() {

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
            holder.itemView.setBackgroundColor(Color.parseColor(color))

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
}