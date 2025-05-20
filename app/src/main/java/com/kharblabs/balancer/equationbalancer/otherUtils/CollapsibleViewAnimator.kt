package com.kharblabs.balancer.equationbalancer.otherUtils
import android.view.View
import android.view.animation.DecelerateInterpolator

import androidx.core.view.isVisible

class CollapsibleViewAnimator(
    private val duration: Long = 300L
) {
    /**
     * Collapses the view by fading and translating it.
     * Direction can be "up" or "down".
     */
    fun collapse(view: View, direction: String = "down", onEnd: (() -> Unit)? = null) {
        if (!view.isVisible) {
            onEnd?.invoke()
            return
        }

        val translateDistance = view.height.takeIf { it*0.5f > 0 }?.toFloat() ?: 100f

        view.animate()
           // .translationY(if (direction == "down") translateDistance else -translateDistance)
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                view.visibility = View.GONE
                view.translationY = 0f
                view.alpha = 1f
                onEnd?.invoke()
            }
            .start()
    }

    /**
     * Expands the view by making it visible and animating in.
     * Direction can be "up" or "down".
     */
    fun expand(view: View, from: String = "down", onStart: (() -> Unit)? = null, onEnd: (() -> Unit)? = null) {
        if (view.isVisible) {
            onEnd?.invoke()
            return
        }

        val distance = view.height.takeIf { it*0.5f > 0 }?.toFloat() ?: 100f
      //  view.translationY = if (from == "down") distance else -distance
        view.alpha = 0f
        view.visibility = View.VISIBLE

        onStart?.invoke()

        view.animate()
            //.translationY(0f)
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                onEnd?.invoke()
            }
            .start()
    }
}

