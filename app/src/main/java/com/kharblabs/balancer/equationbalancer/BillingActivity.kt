package com.kharblabs.balancer.equationbalancer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.android.billingclient.api.*

class BillingActivity : AppCompatActivity(), PurchasesUpdatedListener {

    private lateinit var billingClient: BillingClient
    private lateinit var productsContainer: LinearLayout
    private lateinit var debugText: TextView
    // Your product IDs (replace with your actual IDs)
    private val SUB_MONTHLY = "pro_mode_new_one_yearly"
    private val SUB_YEARLY = "pro_mode_new_ones"
    private val IAP_LIFETIME = "inapp_full_further"

    private val skuDetailsList = mutableListOf<SkuDetails>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billing)

        productsContainer = findViewById(R.id.products_container)
        debugText = findViewById<TextView>(R.id.billingDebugger)
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this)
            .build()

        startBillingConnection()
    }
    fun updateDebugText(s: String)
    {
        debugText.text = debugText.text.toString()+" :-: "+ s
    }
    private fun startBillingConnection() {
        updateDebugText("starting connection")
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    updateDebugText("ok connection")

                    Toast.makeText(this@BillingActivity, "Billing Setup  ${billingResult.responseCode}", Toast.LENGTH_LONG).show()
                    queryAllProducts()
                } else {
                    updateDebugText("Billing Setup Failed: ${billingResult.debugMessage}")
                    Toast.makeText(this@BillingActivity, "Billing Setup Failed: ${billingResult.debugMessage}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onBillingServiceDisconnected() {
                Toast.makeText(this@BillingActivity, "Billing Service Disconnected", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun queryAllProducts() {
        updateDebugText("query connection")

        val subsList = listOf(SUB_MONTHLY, SUB_YEARLY)
        val subsParams = SkuDetailsParams.newBuilder()
            .setSkusList(subsList)
            .setType(BillingClient.SkuType.SUBS)
            .build()

        billingClient.querySkuDetailsAsync(subsParams) { billingResult, subsSkuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && subsSkuDetailsList != null) {
                skuDetailsList.addAll(subsSkuDetailsList)

                val iapParams = SkuDetailsParams.newBuilder()
                    .setSkusList(listOf(IAP_LIFETIME))
                    .setType(BillingClient.SkuType.INAPP)
                    .build()

                billingClient.querySkuDetailsAsync(iapParams) { iapBillingResult, iapSkuDetailsList ->
                    if (iapBillingResult.responseCode == BillingClient.BillingResponseCode.OK && iapSkuDetailsList != null) {
                        skuDetailsList.addAll(iapSkuDetailsList)

                        runOnUiThread {
                            showProducts()
                        }
                    }
                }
            }
        }
    }

    private fun showProducts() {

        updateDebugText("Show prodcuts")
        productsContainer.removeAllViews()

        for (skuDetails in skuDetailsList) {
            val button = Button(this).apply {
                text = "${skuDetails.title} - ${skuDetails.price}"
                setOnClickListener {
                    launchPurchase(skuDetails)
                }
            }
            productsContainer.addView(button)
        }
    }

    private fun launchPurchase(skuDetails: SkuDetails) {
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()
        val billingResult = billingClient.launchBillingFlow(this, flowParams)
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            Toast.makeText(this, "Error launching purchase flow: ${billingResult.debugMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(this, "Purchase canceled", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error: ${billingResult.debugMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(acknowledgeParams) { ackResult ->
                    if (ackResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        Toast.makeText(this, "Purchase acknowledged! Unlocking content...", Toast.LENGTH_SHORT).show()
                        // Unlock features here
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        billingClient.endConnection()
    }
}