package com.kharblabs.equationbalancer2.dataManagers
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kharblabs.equationbalancer2.R

import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.kharblabs.equationbalancer2.ui.EquationBal.HomeFragment
import com.kharblabs.equationbalancer2.ui.EquationBal.HomeViewModel
import kotlin.math.roundToInt
interface MoleculeFragementListner {
    fun setMolar(reagentIndex: Int,mass: Float)
    fun scanTaken(): FloatArray
    fun takenEdited(editPos:Int): Int
}

class MoleculeAdapter(
    private val forcedLimiting: Boolean,
    private val isReactant: Boolean,
    private val listener: HomeFragment,
    private val onValueChanged: (index: Int, newTakenMass: Float) -> Unit,
    private val getCurrentlyEditingIndex: () -> Int?
) : ListAdapter<MoleculeEntry, MoleculeAdapter.MoleculeViewHolder>(DIFF_CALLBACK) {

    var moleculeEntries = mutableListOf<MoleculeEntry>()

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MoleculeEntry>() {
            override fun areItemsTheSame(oldItem: MoleculeEntry, newItem: MoleculeEntry): Boolean {
                return oldItem.taken== newItem.taken
            }

            override fun areContentsTheSame(oldItem: MoleculeEntry, newItem: MoleculeEntry): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoleculeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.molecule_list, parent, false)
        return MoleculeViewHolder(itemView,onValueChanged)
    }

    override fun onBindViewHolder(holder: MoleculeViewHolder, position: Int) {
        val molecule = getItem(position)
        val isEditing = getCurrentlyEditingIndex() == position
        holder.bind(molecule, position,isEditing)
    }

    override fun onCurrentListChanged(previousList: MutableList<MoleculeEntry>, currentList: MutableList<MoleculeEntry>) {
        super.onCurrentListChanged(previousList, currentList)
        moleculeEntries = copyData(currentList)

    }

    fun returnChangedMolecules(): List<MoleculeEntry> {
        try {
            currentList.forEachIndexed { index, molecule ->
                if (index < moleculeEntries.size) {
                    molecule.taken = moleculeEntries[index].taken
                    molecule.molar = moleculeEntries[index].molar
                    molecule.residue = moleculeEntries[index].residue
                    molecule.name =moleculeEntries[index].name
                    molecule.mole = moleculeEntries[index].mole
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return currentList
    }
    private fun getTakenMasses()
    {
        val taken= FloatArray(moleculeEntries.size)
        val numViews= moleculeEntries.size
        for(i in 0 until numViews)
        {
            taken[i]=moleculeEntries[i].taken
        }

    }

    private fun copyData(molecules: List<MoleculeEntry>): MutableList<MoleculeEntry> {
        return if (!forcedLimiting) {
            molecules.map {
                MoleculeEntry(it.name,it.taken, it.molar, it.mole)
            }.toMutableList()
        } else {
            molecules.map {
                MoleculeEntry(it.name,it.taken, it.molar, it.mole, it.residue)
            }.toMutableList()
        }
    }



    private fun setMolar(moleculeIndex: Int, newMolar: Float) {
        moleculeEntries[moleculeIndex].molar = newMolar
    }

    inner class MoleculeViewHolder(itemView: View, onValueChanged: (Int, Float) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.list_item)
        private val residue: TextView = itemView.findViewById(R.id.residue)
        private val moless: TextView = itemView.findViewById(R.id.moles)
        private val mmolarMass: TextView = itemView.findViewById(R.id.listMass)
        private val takenMassField: EditText = itemView.findViewById(R.id.takenMass)
        private val takenMassShow: TextView = itemView.findViewById(R.id.takenMassShow)
        private val takenMolesEditor: EditText = itemView.findViewById(R.id.takenMoles)
        private val takenMolesShow: TextView = itemView.findViewById(R.id.takenMolesShow)
        private var suppressUpdate =false
        init {
            //setupTextWatchers()
            setupClickListeners()
            newSetupViewWatchers()
        }
        private var currentWatcher: TextWatcher? = null
        fun bind(molecule: MoleculeEntry, position: Int, isEditing: Boolean) {
            suppressUpdate = true;
            name.text = molecule.name
            moless.text = molecule.mole.toString()
            //takenMassField.visibility = if (isEditing) View.VISIBLE else View.GONE
            //takenMassShow.visibility = if (!isEditing) View.VISIBLE else View.GONE
            val currentText = takenMassField.text.toString()
            val newText = molecule.taken.toString()
            currentWatcher?.let { takenMassField.removeTextChangedListener(it) }
            if (currentText != newText) {
                takenMassField.setText(newText)
                takenMassField.setSelection(newText.length)
            }
            mmolarMass.text = ((molecule.molar * 1000.0).roundToInt() / 1000.0).toString()
            takenMassShow.text =( (molecule.taken * 1000.0).roundToInt() / 1000.0).toString()

            val mole = if(molecule.molar > 0f) (molecule.taken / molecule.molar * 100.0).roundToInt() / 100.0 else 0.0f
            takenMolesShow.text = mole.toString()

            takenMassField.tag = position
            mmolarMass.tag = position
            takenMolesEditor.tag = position
            // Clean previous watcher


            // Add watcher
            val watcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (!suppressUpdate) {
                        val newMass = s.toString().toFloatOrNull() ?: 0f
                        onValueChanged(adapterPosition, newMass)

                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }

            takenMassField.addTextChangedListener(watcher)
            currentWatcher = watcher


            suppressUpdate = false

            if (forcedLimiting) {
                residue.visibility = View.VISIBLE

                residue.text = if(isReactant) molecule.residue.toString() else "-"
            } else {
                residue.visibility = View.GONE
            }

        }

        private fun setupClickListeners() {

            takenMassShow.setOnClickListener {

                if(forcedLimiting) {
                    // do not let product editing
                    if(isReactant){
                    takenMassShow.visibility = View.GONE
                    suppressUpdate = true
                    takenMassField.setText(takenMassShow.text)
                    suppressUpdate = false
                    takenMassField.visibility = View.VISIBLE
                    takenMassField.requestFocus()
                    }

                }
                else
                {  takenMassShow.visibility = View.GONE
                    suppressUpdate = true
                    takenMassField.setText(takenMassShow.text)
                    suppressUpdate = false
                    takenMassField.visibility = View.VISIBLE
                    takenMassField.requestFocus()
                }

            }
        }
        private fun newSetupViewWatchers()
        {
           /* takenMolesEditor.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    try {
                        val value = takenMolesEditor.text.toString()
                        takenMolesEditor.visibility = View.GONE
                        takenMolesShow.text = value

                        val position = takenMassField.tag as Int
                        moleculeEntries[position].taken = value.toFloat() * moleculeEntries[position].molar

                        takenMolesShow.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        // Handle error
                    }
                }
            }
*/
            takenMassField.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    try {
                        val value = takenMassField.text.toString()
                        takenMassField.visibility = View.GONE
                        takenMassShow.text = value
                        takenMassShow.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        // Handle error
                    }
                }

            }
        }

    }
}

