package com.alexvasilkov.gestures

import android.view.View

abstract class AnimationEngine(private val view: View) : Runnable {
    companion object {
        private const val FRAME_TIME = 10L
    }

    override fun run() {
        if (onStep()) {
            scheduleNextStep()
        }
    }

    abstract fun onStep(): Boolean

    private fun scheduleNextStep() {
        view.removeCallbacks(this)
        view.postOnAnimationDelayed(this, FRAME_TIME)
    }

    fun start() {
        scheduleNextStep()
    }
}
