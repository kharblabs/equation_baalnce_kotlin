package com.kharblabs.balancer.equationbalancer.pro_mode
import android.content.Context
import android.util.AttributeSet
import android.widget.*
import android.view.LayoutInflater
import com.android.billingclient.api.*
import com.kharblabs.balancer.equationbalancer.R

class SubscriptionItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val title: TextView
    private val description: TextView
    private val price: TextView
    private val button: Button

    init {
        LayoutInflater.from(context).inflate(R.layout.view_subscription_item, this, true)
        orientation = VERTICAL
        title = findViewById(R.id.tvSubscriptionTitle)
        description = findViewById(R.id.tvSubscriptionDescription)
        price = findViewById(R.id.tvSubscriptionPrice)
        button = findViewById(R.id.btnSubscribe)
    }

    fun bind(
        product: ProductDetails,
        onClick: (ProductDetails, String?) -> Unit
    ) {
        title.text = product.name
        description.text = product.description

        val priceText = when (product.productType) {
            BillingClient.ProductType.SUBS -> product.subscriptionOfferDetails
                ?.firstOrNull()
                ?.pricingPhases
                ?.pricingPhaseList
                ?.firstOrNull()
                ?.formattedPrice
            BillingClient.ProductType.INAPP -> product.oneTimePurchaseOfferDetails?.formattedPrice
            else -> "N/A"
        }

        price.text = priceText ?: "N/A"

        button.text = if (product.productType == BillingClient.ProductType.INAPP) "Buy" else "Subscribe"

        val offerToken = product.subscriptionOfferDetails?.firstOrNull()?.offerToken

        button.setOnClickListener {
            onClick(product, offerToken)
        }
    }
}
