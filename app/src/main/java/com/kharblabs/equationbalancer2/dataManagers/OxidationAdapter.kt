package com.kharblabs.equationbalancer2.dataManagers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kharblabs.equationbalancer2.R
import com.kharblabs.equationbalancer2.chemicalPlant.OxidationEntry

class OxidationAdapter(context: Context, private val items: List<OxidationEntry>) :
    ArrayAdapter<OxidationEntry>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.oxidation_item, parent, false)
        val item = items[position]

        view.findViewById<TextView>(R.id.atomText).text = item.atom
        view.findViewById<TextView>(R.id.groupText).text = item.group
        view.findViewById<TextView>(R.id.stateText).text = item.oxidationState.toString()

        return view
    }
}