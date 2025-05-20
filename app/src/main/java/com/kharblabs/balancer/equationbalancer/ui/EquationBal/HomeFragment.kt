package com.kharblabs.balancer.equationbalancer.ui.EquationBal

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kharblabs.balancer.equationbalancer.R
import com.kharblabs.balancer.equationbalancer.dataManagers.AssetFileReader
import com.kharblabs.balancer.equationbalancer.dataManagers.MoleculeAdapter
import com.kharblabs.balancer.equationbalancer.dataManagers.MoleculeFragementListner
import com.kharblabs.balancer.equationbalancer.dataManagers.TextHistoryManager
import com.kharblabs.balancer.equationbalancer.databinding.FragmentHomeBinding
import com.kharblabs.balancer.equationbalancer.otherUtils.Animators
import com.kharblabs.balancer.equationbalancer.otherUtils.CollapsibleViewAnimator
import com.kharblabs.balancer.equationbalancer.ui.AnimatorsHolders
import katex.hourglass.`in`.mathlib.MathView
import kotlinx.coroutines.launch

class HomeFragment : Fragment(),MoleculeFragementListner {
    private var previousScrollY = 0
    private var isEquationVisible = true
    private var _binding: FragmentHomeBinding? = null
    private var   anim : Animation=  AlphaAnimation(1.0f, 0.0f);
    lateinit private var  solverResults: FloatArray
    private lateinit var moleculeAdapter: MoleculeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var moleculeAdapter2: MoleculeAdapter
    private lateinit var recyclerViewProducts: RecyclerView
    private var isBalanced: Boolean = false
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val fileReader by lazy { AssetFileReader(requireContext()) }
    val animatorsHolders= AnimatorsHolders()
    val aninamtors = Animators()
    lateinit var homeViewModel: HomeViewModel

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root



        val mathView : MathView= binding.textLatex
        homeViewModel.displayLatex.observe(viewLifecycleOwner){mathView.setDisplayText(it)}
        val editText: AutoCompleteTextView = binding.editText

        lifecycleScope.launch {
                val fileContent = fileReader.readCsvFile("rtherm.csv")
                homeViewModel.csvData = fileContent as MutableList<Array<String>>
            }


        editText.addTextChangedListener(object :TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
               homeViewModel.input_equation.value=s?.toString()
            }
        })

        //Clear button
        editText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = editText.compoundDrawables[2] // Get the drawable at the end
                if (drawable != null && event.x >= editText.width - editText.paddingRight - drawable.intrinsicWidth) {
                    // Clear the text
                    editText.text.clear()
                    return@setOnTouchListener true
                }
            }
            false
        }
        homeViewModel.displayErrorString.observe(viewLifecycleOwner){editText.error=it}
        anim.setDuration(300);
        val main_button : Button = binding.mainButton
        main_button.setOnClickListener{homeViewModel.onButtonClick()}
        return root


    }
    fun getAllMolecules(): List<String>
    {
        val reactants = homeViewModel.reactantList.value?.map { it.name } ?: emptyList()
        val products = homeViewModel.productList.value?.map { it.name } ?: emptyList()
        return reactants + products

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    var isLimitChecked= false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tipCard= binding.tipperCard
        val inputCard = binding.inputCard
        val outputCard = binding.outputHodler
        val outputINputShow=binding.outputInputShow
        val limitCheckBox=binding.limitChecker
        val limitingReagentName=binding.limitingReagentName
        val residueHeader =binding.residueHeader
        val thermoData = binding.thermoData
        recyclerView=binding.moleculeRecycler
        recyclerViewProducts=binding.productRecycler
      /*  val parentLayout = binding.root.findViewById<LinearLayout>(R.id.buttContainer)
        for (i in 0 until parentLayout.childCount) {
            val child = parentLayout.getChildAt(i)
            if (child is Button) {
                child.setOnClickListener {
                    homeViewModel.editClickAdd(child.text.toString())
                }
            }
        }*/

        addClickListeners()
        homeViewModel.input_equation.observe(viewLifecycleOwner) {
            outputINputShow.text = it.replace("+"," + ").replace("="," = ")

            outputINputShow.post {
                val layout = outputINputShow.layout ?: return@post

                if (layout.lineCount == 1) {
                    // It fits in one line, nothing to do.
                    return@post
                }
                if (outputINputShow.lineCount > 1) {
                    it?.let {

                        // textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, originalTextSize)
                        outputINputShow.text = it.replace("+"," + ").replace("="," = \r\n ")
                    }
                }
            }
        }
        homeViewModel.textToInsert.observe(viewLifecycleOwner) { textToInsert ->
            val editText = binding.editText
            val start = editText.selectionStart
            val end = editText.selectionEnd
            editText.text.replace(minOf(start, end), maxOf(start, end), textToInsert)
            editText.setSelection(minOf(start, end) + textToInsert.length)
        }

        homeViewModel.balancedAllGood.observe(viewLifecycleOwner) {
            if(it) {
                tipCard.animation=anim
                tipCard.visibility = View.GONE
                TextHistoryManager.addToHistory(requireContext(),"equations",homeViewModel.input_equation.value.toString())
                Toast.makeText(requireContext(),"Balanced!",Toast.LENGTH_SHORT).show()
                showOutput()
            }
            else
            {
                tipCard.visibility =View.VISIBLE
                showInput()
            }
        }

        outputINputShow.setOnClickListener { homeViewModel.onEquationInputClicked() }

        setupStochRecyclers()
        outputCard.setOnClickListener { homeViewModel.checkClicked() }
        limitCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
           if(isChecked!=isLimitChecked) {
               isLimitChecked = isChecked
               setupStochRecyclers()
               if(isChecked) {
                   limitingReagentName.visibility = View.VISIBLE
                   residueHeader.visibility= View.VISIBLE
               }
               else
               {
                   limitingReagentName.visibility = View.GONE
                   residueHeader.visibility= View.GONE
                   limitingReagentName.text =""
               }

           }
        }
        binding.resultTexter.setOnClickListener {
        showReactionBottomSheet(requireContext(),homeViewModel.displayText.toString(),getAllMolecules(),false)
        }
        homeViewModel.limitingName.observe(viewLifecycleOwner) { it->
            limitingReagentName.text =
                "Limiting Reagent : " + homeViewModel.limitingName.value
        }
        homeViewModel.thermalText.observe(viewLifecycleOwner) {it->
            thermoData.text=it
        }
        binding.textViewExample.setOnClickListener { showExamples() }

        homeViewModel.showOtherElements.observe(viewLifecycleOwner) { shouldShow ->
            if (shouldShow) {
                // Make the other UI elements visible here
                binding.scrollView2.visibility = View.VISIBLE
                aninamtors.animateItemsInLinearLayout(binding.scrollView2,binding.linearLayout2)

            }
            else
            {
                aninamtors.animateItemsOutLinearLayout(binding.linearLayout2)
            }
        }
        val textView:TextView = binding.resultTexter


        homeViewModel.displayText.observe(viewLifecycleOwner) {

            aninamtors.animateReadySpannable(textView, it) {
              homeViewModel.setOtherElementsVisibility(true)

            }

          /*  //textView.setText(it,BufferType.SPANNABLE)
            textView.post {
                val layout = textView.layout ?: return@post
                homeViewModel.setOtherElementsVisibility(true)
                if (layout.lineCount == 1) {
                    // It fits in one line, nothing to do.

                    return@post
                }
                if (textView.lineCount > 1) {
                    it?.let {
                        val splitText = homeViewModel.splitTextIfNeeded(it)
                       // textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, originalTextSize)

                        aninamtors.animateReadySpannable(textView, splitText){homeViewModel.setOtherElementsVisibility(true)}
                    }
                }
            }*/
        }
        homeViewModel.extaradata.observe(viewLifecycleOwner) {
            binding.extraInfoHandler.text=it
        }
        homeViewModel.isLoading.observe(viewLifecycleOwner) { it ->
            if (it)
                binding.hinter.text = "running in background"
            else
                binding.hinter.text = "run complete"
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Your custom logic here
                if (homeViewModel.balancedAllGood.value ==true) {
                    // Custom logic (e.g., update UI or show dialog)
                    homeViewModel.onEquationInputClicked()

                } else {
                    // Disable this callback and propagate the back event to system

                    this.isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                    this.isEnabled = true
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


    private var isTitleVisible = true
    private var currentlyEditingIndex: Int? = null
    fun setupStochRecyclers()
    {
        moleculeAdapter= MoleculeAdapter(isLimitChecked,true,this,     onValueChanged = { index, newTakenMass ->
                                                                            currentlyEditingIndex = index
                                                                             homeViewModel.updateTakenMass(index,true, newTakenMass,isLimitChecked)
                                                                                },
                                             getCurrentlyEditingIndex = { currentlyEditingIndex })
        recyclerView.apply { layoutManager= LinearLayoutManager(requireContext())
            adapter=moleculeAdapter
        }
        homeViewModel.reactantList.observe(viewLifecycleOwner) { molecules ->
            try{
                moleculeAdapter.submitList(molecules)
                ractantSize=molecules.size
            }
            catch(e: Exception)
            {}
        }
        moleculeAdapter2= MoleculeAdapter(isLimitChecked,false,this,     onValueChanged = { index, newTakenMass ->
                                          currentlyEditingIndex = index
                                         homeViewModel.updateTakenMass(index,false, newTakenMass,isLimitChecked)
                                              },
                                            getCurrentlyEditingIndex = { currentlyEditingIndex })
        recyclerViewProducts.apply { layoutManager= LinearLayoutManager(requireContext())
            adapter=moleculeAdapter2
        }
        homeViewModel.productList.observe(viewLifecycleOwner) { molecules ->
           try{
               moleculeAdapter2.submitList(molecules)
           }

            catch(e: Exception)
            {

            }

        }



    }

    private fun showReactionBottomSheet(
        context: Context,
        react: String,
        molecules: List<String>,
        isPro: Boolean,
        copyLabel: String = "Copy Reaction",
        balLabel: String = "Balance & Send"
    ) {

        val dialog = BottomSheetDialog(context)
        val sheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_home_reaction, null)

        val container = sheetView.findViewById<LinearLayout>(R.id.moleculeListContainer)
        val btnClose = sheetView.findViewById<Button>(R.id.btnClose)
        molecules.forEach { molecule ->

            val itemView = if(isPro ) LayoutInflater.from(context).inflate(R.layout.bottom_sheet_molecule_inset_pro, container, false) else LayoutInflater.from(context).inflate(R.layout.bottom_sheet_molecule_inset, container, false)

            val txtName = itemView.findViewById<TextView>(R.id.textMoleculeName)
            val btnCopy = itemView.findViewById<TextView>(R.id.btnCopy)
            val btnDetails = itemView.findViewById<TextView>(R.id.btnDetails)

            txtName.text = molecule
            if(isPro) {
                btnCopy.setOnClickListener {
                    val clipboard =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Molecule", molecule)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Copied: $molecule", Toast.LENGTH_SHORT).show()
                }

                btnDetails.setOnClickListener {
                    Toast.makeText(requireContext(), "Details for $molecule", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            else{
                btnCopy.setOnClickListener {
                 processInApp()
                }

                btnDetails.setOnClickListener {
                    processInApp()
                }
            }
            container.addView(itemView)
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(sheetView)
        dialog.show()
    }
    fun onButtonClickedAdd(view: View) { val buttonText = (view as? Button)?.text?.toString() ?: return
        homeViewModel.editClickAdd(buttonText)
    }

    fun processInApp()
    {
        Toast.makeText(requireContext(),"Processing in App",Toast.LENGTH_SHORT).show()
    }
    // Animate from InputCard → OutputCard
    fun showOutput() {
        animator.collapse(binding.inputCard, direction = "up") {
            animator.expand(binding.outputHodler, from = "down")
        }
    }

    fun showInput() {
        animator.collapse(binding.outputHodler, direction = "down") {
            animator.expand(binding.inputCard, from = "up")
        }
    }
    val animator = CollapsibleViewAnimator()
    private var ractantSize = 0
    fun addClickListeners()
    {
        val editText = binding.editText
        val gridLayout = binding.gridviewButts

        for (i in 0 until gridLayout.childCount) {
            val child = gridLayout.getChildAt(i)
            if (child is Button) {
                child.setOnClickListener { btn ->
                    buttonTextToEditText(btn as Button, editText)
                }
            }
        }
       /* binding.buttoneq.setOnClickListener { buttonTextToEditText(it as Button, editText) }
        binding.buttonadd.setOnClickListener { buttonTextToEditText(it as Button, editText) }
        binding.buttonBracketOpen.setOnClickListener { buttonTextToEditText(it as Button, editText) }
        binding.buttonBracketClose.setOnClickListener { buttonTextToEditText(it as Button, editText) }
        *///binding.eq.setOnClickListener { buttonTextToEditText(it as Button, editText) }
        binding.btnHistorEqny.setOnClickListener { showTextHistoryDialog(editText, "equations") }
        binding.btnClr.setOnClickListener { binding.editText.setText(" ")}
    }
    fun Fragment.showTextHistoryDialog(
        editText: AutoCompleteTextView,
        historyType: String
    ) {
        val history = TextHistoryManager.getHistory(requireContext(), historyType)
        if (history.isEmpty()) {
            Toast.makeText(requireContext(), "No history", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Select from history")
            .setItems(history.toTypedArray()) { _, which ->
                editText.setText(history[which])
                editText.setSelection(editText.text.length) // move cursor to end
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Clear All") { _, _ ->
                TextHistoryManager.clearHistory(requireContext(), historyType)
            }
            .show()
    }
    fun buttonTextToEditText(btn : Button, editText: AutoCompleteTextView)
    {
        val buttonText = btn.text.toString()
        val start = editText.selectionStart.coerceAtLeast(0)
        val end = editText.selectionEnd.coerceAtLeast(0)

        val text = editText.text
        text.replace(minOf(start, end), maxOf(start, end), buttonText, 0, buttonText.length)

        val newCursorPos = minOf(start, end) + buttonText.length
        editText.setSelection(newCursorPos)
    }
    fun showExamples() {
        val limitCheckBox=binding.limitChecker
        val inputTextField=binding.editText
        try {

            if (limitCheckBox.isChecked) {
                limitCheckBox.isChecked = false
                isLimitChecked=false
            }

            val elsa = arrayOf(
                "Ag+HNO3=AgNO3+NO2+H2O",
                "Al+Fe2O3=Al2O3+Fe",
                "C2H5OH+O2=CO2+H2O",
                "H2.5+O2=H2O",
                "NaOH+H2SO4=Na2SO4+H2O",
                "FeSO4+KMnO4+H2SO4=K2SO4+MnSO4+Fe2(SO4)3+H2O",
                "K4Fe(CN)6 + KMnO4 + H2SO4 = KHSO4 + Fe2(SO4)3 + MnSO4 + HNO3 + CO2 + H2O",
                "KMnO4+H2C2O4+H2SO4=K2SO4+MnSO4+H2O+CO2"
            )

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Example Equation :")

            builder.setItems(elsa) { _, i ->
                try {
                    inputTextField.setText(elsa[i])
                    val bundle = Bundle().apply {
                        putString("eq", elsa[i])
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            builder.create().show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun scanTaken():FloatArray {
        TODO("Not yet implemented")
        // we need only reactants in this
        val taken = FloatArray(ractantSize)
        val numviewsAvail =ractantSize
        for (i in 0 until numviewsAvail) {
            val holder = recyclerView.findViewHolderForAdapterPosition(i)
            val thenEdited = holder?.itemView?.findViewById<TextView>(R.id.takenMassShow)
            taken[i] = thenEdited?.text.toString().toFloat()
        }
        return taken
    }
    override fun setMolar (pos:Int,mass :Float)
    {
        homeViewModel.updateTakenMass(pos,false,mass,false)

    }

    override fun takenEdited(editPos: Int): Int {
        homeViewModel
        TODO("Not yet implemented")
    }

    private fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard?.setPrimaryClip(clip)
    }
}