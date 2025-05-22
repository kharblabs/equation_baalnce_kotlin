package com.kharblabs.balancer.equationbalancer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min

class ProModeHandler : AppCompatActivity(), PurchasesUpdatedListener {

    private lateinit var billingClient: BillingClient
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var statusTextView: TextView
    private lateinit var monthlySubButton: Button
    private lateinit var yearlySubButton: Button
    private lateinit var lifetimeButton: Button
    private lateinit var monthlySubPrice: TextView
    private lateinit var yearlySubPrice: TextView
    private lateinit var lifetimePrice: TextView
    private val productDetailsList = mutableListOf<ProductDetails>()
    private val handler = Handler(Looper.getMainLooper())
    private var retryDelayMs = 3000L // Initial retry delay
    private val maxRetryDelayMs = 30000L // Max retry delay (30 seconds)
    private val retryBackoffFactor = 1.5 // Exponential backoff factor

    // StateFlow to observe connection state
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.DISCONNECTED)
    private val connectionState: StateFlow<ConnectionState> = _connectionState

    enum class ConnectionState {
        CONNECTING, CONNECTED, DISCONNECTED, FAILED
    }
    private val SUB_MONTHLY = "pro_mode_monthly"
    private val SUB_YEARLY = "pro_mode_new_one_yearly"
    private val IAP_LIFETIME = "inapp_full_further"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pro_mode_handler)

        // Initialize UI components
        loadingProgressBar = findViewById<ProgressBar>(R.id.loadingProgressBar)
        statusTextView = findViewById<TextView>(R.id.statusTextView)
        monthlySubButton = findViewById<Button>(R.id.monthlySubButton)
        yearlySubButton = findViewById<Button>(R.id.yearlySubButton)
        lifetimeButton = findViewById<Button>(R.id.lifetimeButton)
        monthlySubPrice = findViewById(R.id.monthly_price)
        yearlySubPrice =findViewById<TextView>(R.id.yearly_price)
        lifetimePrice =findViewById<TextView>(R.id.lifetime_price)
        // Show loading icon initially
        loadingProgressBar.visibility = View.VISIBLE
        monthlySubButton.isEnabled = false
        yearlySubButton.isEnabled = false
        lifetimeButton.isEnabled = false
        statusTextView.text = "Connecting to Google Play..."

        // Initialize BillingClient
        billingClient = BillingClient.newBuilder(this)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        // Observe connection state
        CoroutineScope(Dispatchers.Main).launch {
            connectionState.collectLatest { state ->
                when (state) {
                    ConnectionState.CONNECTING -> {
                        loadingProgressBar.visibility = View.VISIBLE
                        statusTextView.text = "Connecting to Google Play..."
                        monthlySubButton.isEnabled = false
                        yearlySubButton.isEnabled = false
                        lifetimeButton.isEnabled = false
                    }
                    ConnectionState.CONNECTED -> {
                        statusTextView.text = "Connected to Google Play"
                        // Query product details
                        queryProductDetails()
                    }
                    ConnectionState.DISCONNECTED -> {
                        loadingProgressBar.visibility = View.VISIBLE
                        statusTextView.text = "Disconnected from Google Play. Retrying..."
                        retryConnection()
                    }
                    ConnectionState.FAILED -> {
                        loadingProgressBar.visibility = View.GONE
                        statusTextView.text = "Failed to connect to Google Play. Please check your network."
                        monthlySubButton.isEnabled = false
                        yearlySubButton.isEnabled = false
                        lifetimeButton.isEnabled = false
                    }
                }
            }
        }

        // Start connection to Google Play
        establishConnection()
    }

    private fun establishConnection() {
        _connectionState.value = ConnectionState.CONNECTING
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _connectionState.value = ConnectionState.CONNECTED
                    retryDelayMs = 3000L // Reset retry delay on successful connection
                } else {
                    _connectionState.value = ConnectionState.FAILED
                    retryConnection()
                }
            }

            override fun onBillingServiceDisconnected() {
                _connectionState.value = ConnectionState.DISCONNECTED
            }
        })
    }

    private fun retryConnection() {
        handler.removeCallbacksAndMessages(null) // Clear previous retries
        handler.postDelayed({
            if (_connectionState.value != ConnectionState.CONNECTED) {
                establishConnection()
                // Increase retry delay with exponential backoff, capped at maxRetryDelayMs
                retryDelayMs = min((retryDelayMs * retryBackoffFactor).toLong(), maxRetryDelayMs)
            }
        }, retryDelayMs)
    }

    private suspend fun queryProductDetails() {
        val subProductList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SUB_MONTHLY)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SUB_YEARLY)
                .setProductType(BillingClient.ProductType.SUBS)
                .build())

        val subParams = QueryProductDetailsParams.newBuilder()
            .setProductList(subProductList)
            .build()

        // Query non-consumable in-app product
        val inappProductList = mutableListOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(IAP_LIFETIME)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val inappParams = QueryProductDetailsParams.newBuilder()
            .setProductList(inappProductList)
            .build()

        // Execute both queries
        val subResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(subParams)
        }
        val inappResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(inappParams)
        }

        // Combine results
        val allProductDetails = mutableListOf<ProductDetails>()
        if (subResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            allProductDetails.addAll(subResult.productDetailsList ?: emptyList())
        } else {
            _connectionState.value = ConnectionState.FAILED
            withContext(Dispatchers.Main) {
                statusTextView.text = "Failed to load subscriptions: ${subResult.billingResult.debugMessage}"
            }
            return
        }

        if (inappResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            allProductDetails.addAll(inappResult.productDetailsList ?: emptyList())
        } else {
            _connectionState.value = ConnectionState.FAILED
            withContext(Dispatchers.Main) {
                statusTextView.text = "Failed to load in-app products: ${inappResult.billingResult.debugMessage}"
            }
            return
        }

        productDetailsList.clear()
        productDetailsList.addAll(allProductDetails)
        withContext(Dispatchers.Main) {
            updateUI()
        }
    }

    private fun updateUI() {
        // Hide loading icon and enable buttons
        loadingProgressBar.visibility = View.GONE
        monthlySubButton.isEnabled = true
        yearlySubButton.isEnabled = true
        lifetimeButton.isEnabled = true
        statusTextView.text = "Products loaded successfully"
        // Set product details on buttons
        productDetailsList.forEach { productDetails ->
            when (productDetails.productId) {
                SUB_MONTHLY -> {
                    val offerToken = productDetails.subscriptionOfferDetails?.get(0)?.offerToken
                    monthlySubButton.text = "${productDetails.name}: ${productDetails.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice}"
                    monthlySubPrice.text="${productDetails.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice}"
                    monthlySubButton.setOnClickListener {
                        if (offerToken != null) {
                            launchPurchaseFlow(productDetails, offerToken)
                        }
                    }
                }
                SUB_YEARLY -> {
                    val offerToken = productDetails.subscriptionOfferDetails?.get(0)?.offerToken
                    yearlySubButton.text = "${productDetails.name}: ${productDetails.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice}"
                    yearlySubPrice.text="${productDetails.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice}"
                    yearlySubButton.setOnClickListener {
                        if (offerToken != null) {
                            launchPurchaseFlow(productDetails, offerToken)
                        }
                    }
                }
                IAP_LIFETIME -> {
                    lifetimeButton.text = "${productDetails.name}: ${productDetails.oneTimePurchaseOfferDetails?.formattedPrice}"
                    lifetimePrice.text="${productDetails.oneTimePurchaseOfferDetails?.formattedPrice}"
                    lifetimeButton.setOnClickListener {
                        launchPurchaseFlow(productDetails, null)
                    }
                }
            }
        }

        // Check existing purchases
        checkExistingPurchases()
    }

    private fun launchPurchaseFlow(productDetails: ProductDetails, offerToken: String?) {
        val productDetailsParamsList = if (offerToken != null) {
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(offerToken)
                    .build()
            )
        } else {
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .build()
            )
        }

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient.launchBillingFlow(this, billingFlowParams)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            CoroutineScope(Dispatchers.IO).launch {
                for (purchase in purchases) {
                    handlePurchase(purchase)
                }
            }
        } else {
            Toast.makeText(this, "Purchase failed: ${billingResult.debugMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            val acknowledgeResult = withContext(Dispatchers.IO) {
                billingClient.acknowledgePurchase(acknowledgePurchaseParams)
            }
            if (acknowledgeResult.responseCode == BillingClient.BillingResponseCode.OK) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProModeHandler, "Purchase successful: ${purchase.products.joinToString()}", Toast.LENGTH_LONG).show()
                    updatePurchaseStatus()
                }
            }
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            Toast.makeText(this, "Purchase pending. Please complete the transaction.", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkExistingPurchases() {
        CoroutineScope(Dispatchers.IO).launch {
            val subParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
            val subResult = billingClient.queryPurchasesAsync(subParams)
            if (subResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val activeSubs = subResult.purchasesList.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                withContext(Dispatchers.Main) {
                    if (activeSubs.isNotEmpty()) {
                        statusTextView.text = "Active subscriptions: ${activeSubs.joinToString { it.products.joinToString() }}"
                    }
                }
            }

            val inappParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
            val inappResult = billingClient.queryPurchasesAsync(inappParams)
            if (inappResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val lifetimePurchases = inappResult.purchasesList.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                withContext(Dispatchers.Main) {
                    if (lifetimePurchases.isNotEmpty()) {
                        statusTextView.text = "Lifetime purchase active: ${lifetimePurchases.joinToString { it.products.joinToString() }}"
                        lifetimeButton.isEnabled = false
                    }
                }
            }
        }
    }

    private fun updatePurchaseStatus() {
        checkExistingPurchases()
    }

    override fun onDestroy() {
        super.onDestroy()
        billingClient.endConnection()
        handler.removeCallbacksAndMessages(null)
    }
}