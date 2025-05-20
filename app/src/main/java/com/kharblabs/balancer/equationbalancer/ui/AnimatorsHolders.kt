package com.kharblabs.balancer.equationbalancer.ui

import android.animation.ValueAnimator
import android.view.View

class AnimatorsHolders {

    fun collapseView(view: View) {
        val anim = ValueAnimator.ofInt(view.measuredHeight, 0)
        anim.addUpdateListener {
            val value = it.animatedValue as Int
            view.layoutParams.height = value
            view.requestLayout()
        }
        anim.duration = 200
        anim.start()
    }

    fun expandView(view: View) {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.UNSPECIFIED
        )
        val targetHeight = view.measuredHeight

        val anim = ValueAnimator.ofInt(0, targetHeight)
        anim.addUpdateListener {
            val value = it.animatedValue as Int
            view.layoutParams.height = value
            view.requestLayout()
        }
        anim.duration = 200
        anim.start()
    }
}