package com.kharblabs.equationbalancer2.otherUtils

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.CharacterStyle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.TextView.BufferType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Animators {
    fun animateItemsInLinearLayout(scrollView: ScrollView, container: LinearLayout, duration: Long = 100L) {
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            child.alpha = 0f
            child.translationY = 50f
            child.visibility = View.INVISIBLE
        }

        scrollView.post {
            for (i in 0 until container.childCount) {
                val child = container.getChildAt(i)
                child.visibility = View.VISIBLE
                child.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(i * duration) // Staggered delay
                    .setDuration(duration*2)
                    .start()
            }
        }
    }
    fun animateItemsOutLinearLayout(container: LinearLayout, onEnd: (() -> Unit)? = null) {
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            child.animate()
                .alpha(0f)
                .translationY(-50f)
                .setStartDelay(i * 100L) // Optional staggered delay
                .setDuration(300)
                .withEndAction {
                    child.visibility = View.INVISIBLE
                    if (i == container.childCount - 1) {
                        onEnd?.invoke()
                    }
                }
                .start()
        }
    }
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

    fun applyFadeInAnimation(view: View, delay: Long = 0L) {
        view.alpha = 0f
        view.animate()
            .alpha(1f)
            .setStartDelay(delay)
            .setDuration(400)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }
}