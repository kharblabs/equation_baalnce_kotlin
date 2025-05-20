package com.kharblabs.balancer.equationbalancer.pro_mode

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*

class BillingHelper(
    private val context: Context,
    private val listener: BillingUpdatesListener
) : PurchasesUpdatedListener {

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    init {
        startBillingConnection()
    }

    private fun startBillingConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    listener.onBillingReady()
                } else {
                    listener.onBillingError("Billing setup failed: ${result.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                listener.onBillingError("Billing service disconnected")
            }
        })
    }

    fun queryAllProducts(subIds: List<String>, inAppIds: List<String>) {
        val allProducts = (subIds.map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        } + inAppIds.map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        })

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(allProducts)
            .build()

        billingClient.queryProductDetailsAsync(params) { result, products ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                listener.onProductsFound(products)
            } else {
                listener.onBillingError("Query failed: ${result.debugMessage}")
            }
        }
    }

    fun launchPurchase(activity: Activity, product: ProductDetails, offerToken: String? = null) {
        val builder = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(product)

        offerToken?.let { builder.setOfferToken(it) }

        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(builder.build()))
            .build()

        billingClient.launchBillingFlow(activity, params)
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    acknowledgePurchaseIfNeeded(purchase)
                    listener.onPurchaseCompleted(purchase)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                listener.onPurchaseCancelled()
            }
            else -> {
                listener.onBillingError("Purchase failed: ${result.debugMessage}")
            }
        }
    }

    private fun acknowledgePurchaseIfNeeded(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient.acknowledgePurchase(params) { result ->
                if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                    listener.onBillingError("Acknowledge failed: ${result.debugMessage}")
                }
            }
        }
    }

    interface BillingUpdatesListener {
        fun onBillingReady()
        fun onProductsFound(productDetails: List<ProductDetails>)
        fun onPurchaseCompleted(purchase: Purchase)
        fun onPurchaseCancelled()
        fun onBillingError(errorMessage: String)
    }
}
