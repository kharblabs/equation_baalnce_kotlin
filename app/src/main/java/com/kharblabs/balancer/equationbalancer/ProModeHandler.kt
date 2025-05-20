package com.kharblabs.balancer.equationbalancer

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.kharblabs.balancer.equationbalancer.pro_mode.BillingHelper
import com.kharblabs.balancer.equationbalancer.pro_mode.SubscriptionItemView

class ProModeHandler : AppCompatActivity() {
    private lateinit var billingHelper: BillingHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pro_mode_handler)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val container = findViewById<LinearLayout>(R.id.subscriptionList)

        billingHelper = BillingHelper(this, object : BillingHelper.BillingUpdatesListener {
            override fun onBillingReady() {
                billingHelper.queryAllProducts(
                    subIds = listOf("quarterly_plan", "yearly_plan"),
                    inAppIds = listOf("lifetime_access")
                )
            }

            override fun onProductsFound(productDetails: List<ProductDetails>) {
                container.removeAllViews()

                productDetails.forEach { product ->
                    val itemView = SubscriptionItemView(this@ProModeHandler)
                    itemView.bind(product) { selected, offerToken ->
                        billingHelper.launchPurchase(this@ProModeHandler, selected, offerToken)
                    }
                    container.addView(itemView)
                }
            }

            override fun onPurchaseCompleted(purchase: Purchase) {
                Toast.makeText(this@ProModeHandler, "Purchase successful!", Toast.LENGTH_SHORT).show()
            }

            override fun onPurchaseCancelled() {
                Toast.makeText(this@ProModeHandler, "Purchase cancelled", Toast.LENGTH_SHORT).show()
            }

            override fun onBillingError(errorMessage: String) {
                Toast.makeText(this@ProModeHandler, "Error: $errorMessage", Toast.LENGTH_LONG).show()
            }
        })
    }
}