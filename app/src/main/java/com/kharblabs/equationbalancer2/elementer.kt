package com.kharblabs.equationbalancer2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kharblabs.equationbalancer2.chemicalPlant.ConstantValues
import com.kharblabs.equationbalancer2.dataManagers.CSVFile
import com.squareup.picasso.Picasso

class elementer : AppCompatActivity() {

    private lateinit var elementProperties: List<Any>
    private lateinit var wikiprops: List<Any>
    private lateinit var container: View
    private lateinit var elementHandler: Array<TextView?>
    private lateinit var elementImage: ImageView
    private lateinit var elementHandlerLayout: Array<LinearLayout?>
  //  private lateinit var mFirebaseAnalytics: FirebaseAnalytics
 //   private var mAdView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
        setContentView(R.layout.activity_elementer)

     //   val toolbar = findViewById<Toolbar>(R.id.toolbar2)
        //setSupportActionBar(toolbar)
/*
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adRequest = AdRequest.Builder().build()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
*/
        val inputStream = resources.openRawResource(R.raw.elements)
        elementHandler = arrayOfNulls(64)
        elementHandlerLayout = arrayOfNulls(64)
        var csvFile = CSVFile(inputStream)
        elementImage = findViewById(R.id.elementImage)
        elementProperties = csvFile.read() as List<Any>
        initialise()

        val wikiInputStream = resources.openRawResource(R.raw.wikidata)
        csvFile = CSVFile(wikiInputStream)
        wikiprops = csvFile.read() as List<Any>

        val message = intent.extras?.getString("Element") ?: return
        getCurrentElement(message)
        container = findViewById(R.id.container)
        LoadData(message)

        val bundle = Bundle().apply {
            putString("element", message)
        }
        //mFirebaseAnalytics.logEvent("Element", bundle)

        elementHandler[50]?.text = if (elementHandler[50]?.text == "0") "No" else "Yes"
        elementHandler[51]?.text = if (elementHandler[51]?.text == "0") "No" else "Yes"

        elementHandlerLayout[27]?.visibility = View.GONE
        elementHandler[10]?.append("  kJ/mol")
        elementHandler[11]?.append("  kJ/mol")
        elementHandler[19]?.append("  J/(g mol)")
        elementHandler[13]?.append("  A")
        elementHandler[42]?.append("  mg/kg")
        elementHandler[43]?.append("  mg/kg")
        elementHandler[2]?.append("  cm3/mol")
        elementHandler[15]?.append("  K")
        elementHandler[4]?.append("  K")
        elementHandler[8]?.append("  eV")
        elementHandler[45]?.append("  eV")
        elementHandler[26]?.append("  eV")
        elementHandler[21]?.append("  W/(m K)")
        elementHandler[30]?.append("  kJ/mol")
        elementHandler[29]?.append("  kJ/mol")
        elementHandler[31]?.append("  kJ/mol")
    }

    private fun getCurrentElement(symbol: String) {
        val asString = elementProperties[getAtomicNumber(symbol)].toString()
    }

    private fun getAtomicNumber(symbol: String): Int {
        val elements = consts.elements//resources.getStringArray(R.array.Elements)
        return elements.indexOfFirst { it == symbol }
    }
    private val consts= ConstantValues()
    private fun LoadData(symbol: String) {
        val wikiProp = wikiprops[getAtomicNumber(symbol)] as Array<String>
        if (wikiProp[0].isNotEmpty()) {
            Picasso.get().load(wikiProp[0]).into(elementImage)
        } else {
            elementImage.visibility = View.GONE
        }

        val wikiname = findViewById<TextView>(R.id.wikiname)
        wikiname.text = symbol
        val link = wikiProp[1]
        wikiname.setOnClickListener {
            val uri = Uri.parse(link)
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        val aProps = elementProperties[getAtomicNumber(symbol) + 1] as Array<String>
        for (i in 0..63) {
            try {
                if (aProps[i].isEmpty()) {
                    elementHandlerLayout[i]?.visibility = View.GONE
                } else {
                    elementHandler[i]?.text = aProps[i]
                }
            } catch (e: Exception) {
                Log.i("nully", "$i")
            }
        }
    }

    fun toggle_contents(view: View) {
        container.visibility = if (container.isShown) View.GONE else View.VISIBLE
        container.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_down))
    }

    fun toggle_contents_phy(view: View) {
        toggleViewVisibility(R.id.phyContainer)
    }

    fun toggle_contents_discover(view: View) {
        toggleViewVisibility(R.id.discoveryContainer)
    }

    fun toggle_contents_thermal(view: View) {
        toggleViewVisibility(R.id.heatContainer)
    }

    fun toggle_contents_radius(view: View) {
        toggleViewVisibility(R.id.radiusContainer)
    }

    fun toggle_contents_elec(view: View) {
        toggleViewVisibility(R.id.electroContainer)
    }

    fun toggle_contents_lat(view: View) {
        toggleViewVisibility(R.id.latticeContainer)
    }

    private fun toggleViewVisibility(containerId: Int) {
        val container = findViewById<LinearLayout>(containerId)
        container.visibility = if (container.isShown) View.GONE else View.VISIBLE
        container.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_down))
    }

    private fun initialise() {
        for (i in 0 until 64) {
            val handlerId = resources.getIdentifier("elementHandler$i", "id", packageName)
            elementHandler[i] = findViewById(handlerId)
        }
        for (i in 1 until 64) {
            val layoutId = resources.getIdentifier("elementHandler1Layout$i", "id", packageName)
            elementHandlerLayout[i] = findViewById(layoutId)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
