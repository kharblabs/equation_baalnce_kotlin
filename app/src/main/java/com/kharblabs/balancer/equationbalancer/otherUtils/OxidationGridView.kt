package com.kharblabs.balancer.equationbalancer.otherUtils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView

class OxidationGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GridLayout(context, attrs) {

    init {
        orientation = HORIZONTAL
    }

    fun setData(
        elements: Map<String, Int>,          // e.g. { "K" to 2, "Cr" to 2, "O" to 7 }
        oxidationStates: Map<String, Int>    // e.g. { "K" to +1, "Cr" to +6, "O" to -2 }
    ) {
        removeAllViews()
        columnCount = elements.size

        val keys = elements.keys.toList()

        for ((index, element) in keys.withIndex()) {
            val count = elements[element] ?: 1
            val ox = oxidationStates[element] ?: 0

            val elementText = buildSubscript(element, count)
            val oxidationText = if (ox >= 0) "+$ox" else "$ox"

            val elementView = createCell(elementText, Color.DKGRAY, isElement = true)
            val oxView = createCell(oxidationText, getColorForOxidation(oxidationText), isElement = false)

            val paramsElement = LayoutParams(spec(0), spec(index))
            val paramsOx = LayoutParams(spec(1), spec(index))

            addView(elementView, paramsElement)
            addView(oxView, paramsOx)
        }
    }

    private fun buildSubscript(element: String, count: Int): String {
        return if (count > 1) {
            // Unicode subscript digits start at U+2080, so map digits to subscripts
            val subscriptDigits = count.toString().map { digit ->
                when (digit) {
                    '0' -> '\u2080'
                    '1' -> '\u2081'
                    '2' -> '\u2082'
                    '3' -> '\u2083'
                    '4' -> '\u2084'
                    '5' -> '\u2085'
                    '6' -> '\u2086'
                    '7' -> '\u2087'
                    '8' -> '\u2088'
                    '9' -> '\u2089'
                    else -> digit
                }
            }.joinToString("")

            element + subscriptDigits
        } else element
    }
    private fun createCell(text: String, bgColor: Int, isElement: Boolean): TextView {
        return TextView(context).apply {
            this.text = text
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            textSize = if (isElement) 20f else 16f
            setPadding(24, 24, 24, 24)
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 24f
                setColor(bgColor)
            }
        }
    }

    private fun getColorForOxidation(ox: String): Int {
        return when {
            ox.startsWith("+") -> Color.parseColor("#E53935") // red shades for positive
            ox.startsWith("-") -> Color.parseColor("#1E88E5") // blue shades for negative
            else -> Color.parseColor("#757575") // gray for neutral
        }
    }
}
