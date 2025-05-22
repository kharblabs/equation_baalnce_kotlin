package com.kharblabs.balancer.equationbalancer.pro_mode

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.ProductDetails
import com.kharblabs.balancer.equationbalancer.R

class ProductsAdapter(
    private val products: List<ProductDetails>,
    private val onClick: (ProductDetails) -> Unit
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {


    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_product_title)
        val price: TextView = itemView.findViewById(R.id.tv_product_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        val oneTimePurchase = product.oneTimePurchaseOfferDetails
        val subOffer = product.subscriptionOfferDetails?.firstOrNull()

        holder.title.text = product.title
        holder.price.text = oneTimePurchase?.formattedPrice ?: subOffer?.pricingPhases
            ?.pricingPhaseList?.firstOrNull()?.formattedPrice ?: "N/A"

        holder.itemView.setOnClickListener { onClick(product) }
    }

    override fun getItemCount() = products.size
}