package com.kharblabs.equationbalancer2.ui.EquationBal

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.CharacterStyle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.TextView.BufferType
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kharblabs.equationbalancer2.R
import com.kharblabs.equationbalancer2.dataManagers.AssetFileReader
import com.kharblabs.equationbalancer2.dataManagers.MoleculeAdapter
import com.kharblabs.equationbalancer2.dataManagers.MoleculeFragementListner
import com.kharblabs.equationbalancer2.databinding.FragmentHomeBinding
import com.kharblabs.equationbalancer2.ui.AnimatorsHolders
import katex.hourglass.`in`.mathlib.MathView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

        val main_button : Button = binding.mainButton
        main_button.setOnClickListener{homeViewModel.onButtonClick()}
        return root
        anim.setDuration(300);

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
                inputCard.visibility =View.GONE
                outputCard.visibility = View.VISIBLE

            }
            else
            {
                tipCard.visibility =View.VISIBLE
                inputCard.visibility= View.VISIBLE
                outputCard.visibility= View.GONE
                binding.scrollView2.visibility= View.GONE
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

            }
        }
        val textView:TextView = binding.resultTexter


        homeViewModel.displayText.observe(viewLifecycleOwner) {

            animateReadySpannable(textView, it) {                  binding.scrollView2.visibility = View.VISIBLE           }
            //textView.setText(it,BufferType.SPANNABLE)
            val originalTextSize = textView.textSize
            textView.post {
                val layout = textView.layout ?: return@post

                if (layout.lineCount == 1) {
                    // It fits in one line, nothing to do.
                    return@post
                }
                if (textView.lineCount > 1) {
                    it?.let {
                        val splitText = homeViewModel.splitTextIfNeeded(it)
                       // textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, originalTextSize)

                        animateReadySpannable(textView, splitText){homeViewModel.setOtherElementsVisibility(true)}
                    }
                }
            }
        }
        homeViewModel.extaradata.observe(viewLifecycleOwner) {
            binding.extraInfoHandler.text=it
        }
        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            it->
            if(it)
                binding.hinter.text="running in background"
            else
                binding.hinter.text="run complete"
        }
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
    fun onButtonClickedAdd(view: View) { val buttonText = (view as? Button)?.text?.toString() ?: return
        homeViewModel.editClickAdd(buttonText)}


    private var ractantSize = 0
    fun animateReadySpannable(
        textView: TextView,
        fullText: SpannableStringBuilder?,
        delayMillis: Long = 20L,
        onAnimationComplete: () -> Unit
    ) {
        fullText?.let {
            CoroutineScope(Dispatchers.Main).launch {
                val displayed = SpannableStringBuilder()
                for (i in 0 until fullText.length) {
                    displayed.append(fullText[i])

                    val spans = fullText.getSpans(0, i + 1, CharacterStyle::class.java)
                    for (span in spans) {
                        val start = fullText.getSpanStart(span)
                        val end = fullText.getSpanEnd(span)
                        if (start <= i) {
                            displayed.setSpan(
                                span,
                                start,
                                minOf(end, i + 1),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    }
                    textView.setText(displayed, BufferType.SPANNABLE)

                    delay(delayMillis)
                }
                onAnimationComplete()
            }
        }
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


}