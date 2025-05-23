package com.kharblabs.balancer.equationbalancer.dataManagers

import com.kharblabs.balancer.equationbalancer.R


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * Kotlin conversion of the search_recycler_adaptor.
 * Displays search results for chemical reactions or molecules.
 */
class SearchRecyclerAdapter : RecyclerView.Adapter<SearchRecyclerAdapter.SearchViewHolder> {

    // Properties corresponding to original Java fields
    var reactionList: MutableList<CharSequence> = mutableListOf() // Replaces charSeqSearchin_c for reactions
    var moleculeNameList: List<CharSequence>? = null // Replaces charSeqSearchin_c_names
    var moleculeMassList: List<CharSequence>? = null // Replaces charSeqSearchin_c_masses
    var queriedString : MutableList<String> =mutableListOf()
    private var isSparseLayout: Boolean // Replaces z (used for layout choice)
    private var isMoleculeSearch: Boolean = false
    private var isOxidationSearch: Boolean = false
    private var purchased: Boolean

    // --- Constructors ---

    // Constructor equivalent to public search_recycler_adaptor(boolean purchased)
    // Uses default value for isSparseLayout (false)
    constructor(purchased: Boolean) : this(false, purchased)

    // Constructor equivalent to public search_recycler_adaptor(boolean z, boolean haspurchased)
    // Primary constructor logic for reaction search
    constructor(isSparseLayout: Boolean, purchased: Boolean) {
        this.isSparseLayout = isSparseLayout
        this.purchased = purchased
        this.isMoleculeSearch = false
        // reactionList is initialized above
    }

    constructor(
        purchased: Boolean, // 'z' in Java constructor was used for purchased status here
        moleculeList: List<CharSequence>,
        moleculeNameList: List<CharSequence>,
        moleculeMassList: List<CharSequence>,
        isOxidationSearch: Boolean
    ) {
        this.reactionList = moleculeList.toMutableList() // Use reactionList to hold the primary molecule formula
        this.moleculeNameList = moleculeNameList
        this.moleculeMassList = moleculeMassList
        this.purchased = purchased
        this.isSparseLayout = purchased // Original Java used 'z' for both purchased and layout flag here
        this.isMoleculeSearch = true
        this.isOxidationSearch=isOxidationSearch
    }

    // --- ViewHolder ---
    /**
     * ViewHolder class for the search result items.
     */
    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TextView for the main reaction or molecule formula
        val textviewSearch: TextView = itemView.findViewById(R.id.text_data_entry_row)
        // TextView for molecule name (only used if isMoleculeSearch is true)
        val textviewSearchNames: TextView? = itemView.findViewById(R.id.text_data_entry_row_name)
        // TextView for molecule mass (only used if isMoleculeSearch is true)
        val textviewSearchMasses: TextView? = itemView.findViewById(R.id.text_data_entry_row_mass)
    }

    // --- Adapter Methods ---

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = if (isMoleculeSearch) {
            inflater.inflate(R.layout.list_molecule_search, parent, false)
        } else {
            // Choose layout based on isSparseLayout flag
            val layoutId = if (isSparseLayout) R.layout.recycleview_text_list_row_sparse else R.layout.recycleview_text_list_row
            inflater.inflate(layoutId, parent, false)
        }
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        // Get the data for the current position safely
        val mainText = reactionList.getOrNull(position) ?: return // Exit if position is invalid

        holder.textviewSearch.setText(ReactionTextFormatter(mainText.toString()), TextView.BufferType.SPANNABLE)

        // Handle Molecule Display (name, mass)
        if (isMoleculeSearch) {
            holder.textviewSearchNames?.text = moleculeNameList?.getOrNull(position)
            holder.textviewSearchMasses?.text =
                if (purchased)
                    if(isOxidationSearch) " "
                    else
                        "${moleculeMassList?.getOrNull(position)} gm/mol"
                else "Mass View only in pro mode"

            // Handle click for molecules
            holder.itemView.setOnClickListener { view ->
                val mass = holder.textviewSearchMasses?.text.toString()
                val name = holder.textviewSearchNames?.text.toString()
                val formula = holder.textviewSearch.text.toString()

                showMassBottomSheet(
                    context = view.context,
                    mass = mass,
                    name = name,
                    formula = formula,
                    isPro = purchased
                )
            }

        } else {
            // Handle click for reactions
            holder.textviewSearch.setOnClickListener { view ->
                val rawText = holder.textviewSearch.text.toString()
                val cleanedText = rawText.replace(Regex("(?<=[\\+ =])([0-9])(?=[^a-z])"), "")

                showReactionBottomSheet(
                    context = view.context,
                    react = cleanedText,
                    isPro = purchased,
                    copyLabel = "Copy Reaction",
                    balLabel = "Balance Equation"
                )
            }
        }
    }
    private fun showReactionBottomSheet(
        context: Context,
        react: String,
        isPro: Boolean,
        copyLabel: String = "Copy Reaction",
        balLabel: String = "Balance & Send"
    ) {
        val dialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_reaction, null)
        dialog.setContentView(view)
        val reactionView = view.findViewById<TextView>(R.id.sheetReaction)
        reactionView.setText(react)
        val copyReaction = view.findViewById<TextView>(R.id.copyReaction)
        val balanceReaction = view.findViewById<TextView>(R.id.balanceReaction)

        if (!isPro) {
            // Non-Pro: disable options and prompt upgrade
            copyReaction.isEnabled = false
            balanceReaction.isEnabled = false
            copyReaction.alpha = 0.5f
            balanceReaction.alpha = 0.5f

            view.setOnClickListener {
//                if (context is Searcher_Activity) {
//                    context.inapp_prompt()
//                }
                dialog.dismiss()
            }

        } else {
            // Set dynamic labels if needed
            copyReaction.text = copyLabel
            balanceReaction.text = balLabel

            copyReaction.setOnClickListener {
                copyToClipboard(context, "Reaction", react)
//                if (context is Searcher_Activity) {
//                    context.log_tap_pro("copies", react)
//                }
                dialog.dismiss()
            }

            balanceReaction.setOnClickListener {
                try {
                    val strC = react.toCharArray()
                    var i = 0
                    while (i < strC.size && !strC[i].isLetter()) {
                        strC[i] = ' '
                        i++
                    }
                    val cleaned = String(strC)

                    val data = Intent().apply {
                        putExtra("eqn", cleaned)
                    }
//                    (context as? Activity)?.setResult(Activity.RESULT_OK, data)
//                    context.finish()

                } catch (_: Exception) {
                    Toast.makeText(context, "Failed to send reaction", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showMassBottomSheet(
        context: Context,
        mass: String,
        name: String,
        formula: String,
        isPro: Boolean
    ) {
        val dialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_mass, null)
        dialog.setContentView(view)

        val copyMass = view.findViewById<TextView>(R.id.copyMass)
        val copyFormulaMass = view.findViewById<TextView>(R.id.copyFormulaMass)
        val copyName = view.findViewById<TextView>(R.id.copyName)

        if (!isPro) {
            // Disable all options and show prompt if not Pro
            copyMass.isEnabled = false
            copyFormulaMass.isEnabled = false
            copyName.isEnabled = false

            copyMass.alpha = 0.5f
            copyFormulaMass.alpha = 0.5f
            copyName.alpha = 0.5f

            view.setOnClickListener {
//                if (context is mass_finder) {
//                    context.inapp_prompt()
//                }
                dialog.dismiss()
            }

        } else {
            // Pro users: set click listeners
            copyMass.setOnClickListener {
                copyToClipboard(context, "Mass", formula)
                Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            copyFormulaMass.setOnClickListener {
                copyToClipboard(context, "Mass", "$formula : $mass")
                Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()

                dialog.dismiss()
            }

            copyName.setOnClickListener {
                copyToClipboard(context, "Name", name)
                Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        dialog.show()
    }
    private fun ReactionTextFormatter(equation: String): SpannableString
    {   val stringMakers= StringMakers()
        val spannable = SpannableString(equation)
        stringMakers.applySubscriptSpans(equation, spannable)
        stringMakers.highlightDownArrow(equation, spannable, Color.BLUE)
        stringMakers.highlightSearchMatches(equation, spannable,
            queriedString, SearchInEnum.BY_REAGENTS, "#FFA500".toColorInt())
        stringMakers.highlightCoefficients(equation, spannable, Color.RED)
        return spannable
    }
    override fun getItemCount(): Int {
        return reactionList.size
    }

    // --- Click Listener Logic ---

    /**
     * Sets up the click listener for reaction search items.
     *//*
    private fun setupReactionItemClickListener(holder: SearchViewHolder, reactionText: String) {
        holder.textviewSearch.setOnClickListener { view ->
            val context = view.context ?: return@setOnClickListener
            val currentPosition = holder.adapterPosition // Use adapterPosition for correct index
            if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            // Log tap if context is Searcher_Activity
            if (context is Searcher_Activity) {
                context.log_tap()
            }

            // Original regex seemed to remove leading numbers, ensure this logic is correct
            // Example: "2H2 + O2 = 2H2O" -> "H2 + O2 = H2O" ?
            // This regex removes single digits following +, space, or = if not followed by a lowercase letter
            val cleanedReaction = reactionText.replace(Regex("(?<=[\\+ \\=])([0-9])(?![a-z])"), "")

            val popup = PopupMenu(context, view)

            if (purchased) {
                popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.only_copy -> {
                            copyToClipboard(context, "Equation", cleanedReaction)
                            if (context is Searcher_Activity) {
                                context.log_tap_pro("copies", cleanedReaction)
                            }
                            Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                            true // Indicate item was handled
                        }
                        R.id.cop_bal -> {
                            try {
                                if (context is Activity) { // Check if context is an Activity
                                    if (context is S) {
                                        context.log_tap_pro("stoichio", cleanedReaction)
                                    }
                                    // Original logic to remove leading non-letters (often coefficients)
                                    var send = cleanedReaction
                                    val firstLetterIndex = send.indexOfFirst { it.isLetter() }
                                    if (firstLetterIndex > 0) {
                                        // Replace leading non-letters with spaces? Or just substring?
                                        // Substring is likely cleaner:
                                        send = send.substring(firstLetterIndex)
                                        // Original logic replaced with spaces:
                                        // val strC = send.toCharArray()
                                        // for(i in 0 until firstLetterIndex) { strC[i] = ' ' }
                                        // send = String(strC)
                                    }

                                    val data = Intent()
                                    data.putExtra("eqn", send)
                                    context.setResult(Activity.RESULT_OK, data)
                                    context.finish()
                                } else {
                                    Toast.makeText(context, "Action not supported here", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error performing action", Toast.LENGTH_SHORT).show()
                                // Log exception e
                            }
                            true // Indicate item was handled
                        }
                        else -> false
                    }
                }
            } else { // Not purchased
                popup.menuInflater.inflate(R.menu.popup_menu_no_paid, popup.menu)
                popup.setOnMenuItemClickListener {
                    if (context is Searcher_Activity) {
                        context.inapp_prompt()
                    }
                    true // Indicate item was handled
                }
            }
            popup.show()
        }
    }
*/
    /**
     * Sets up the click listener for molecule search items.
     */
    /*
    private fun setupMoleculeItemClickListener(holder: SearchViewHolder) {
        holder.itemView.setOnClickListener { view -> // Listen on the whole item view
            val context = view.context ?: return@setOnClickListener
            val currentPosition = holder.adapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            // Trigger update in mass_finder activity if applicable
            if (context is mass_finder) {
                context.UpdateMass()
            }

            val popup = PopupMenu(context, view)

            if (purchased) {
                // Get data safely using currentPosition
                val mass = holder.textviewSearchMasses?.text?.toString() ?: "" // Already includes " gm/mol"
                val name = holder.textviewSearchNames?.text?.toString() ?: ""
                val formula = holder.textviewSearch.text.toString()

                popup.menuInflater.inflate(R.menu.popup_mass_paid, popup.menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.only_copy_mass -> {
                            copyToClipboard(context, "Mass", mass)
                            Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                            true
                        }
                        R.id.only_copy_mass_form -> {
                            copyToClipboard(context, "Mass", "$formula : $mass")
                            Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                            true
                        }
                        R.id.only_copy_name -> {
                            copyToClipboard(context, "Name", name)
                            Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                            true
                        }
                        else -> false
                    }
                }
            } else { // Not purchased
                popup.menuInflater.inflate(R.menu.popup_mass, popup.menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.only_pro_mass, R.id.only_pro_name, R.id.only_pro -> {
                            if (context is mass_finder) {
                                context.inapp_prompt()
                            }
                            true
                        }
                        else -> false
                    }
                }
            }
            popup.show()
        }
    }
*/
    /**
     * Helper function to copy text to the clipboard.
     */
    private fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard?.setPrimaryClip(clip)
    }

    // --- Optional: Method to update data ---
    fun updateReactions(newReactions: List<CharSequence>) {
        this.reactionList.clear()
        this.reactionList.addAll(newReactions)
        this.isMoleculeSearch = false // Assume updating reactions means it's not molecule search
        this.moleculeNameList = null
        this.moleculeMassList = null
        notifyDataSetChanged() // Consider using DiffUtil for better performance
    }

    fun updateMolecules(
        molecules: List<CharSequence>,
        names: List<CharSequence>,
        masses: List<CharSequence>
    ) {
        this.reactionList.clear()
        this.reactionList.addAll(molecules)
        this.moleculeNameList = names
        this.moleculeMassList = masses
        this.isMoleculeSearch = true
        notifyDataSetChanged() // Consider using DiffUtil for better performance
    }
}
