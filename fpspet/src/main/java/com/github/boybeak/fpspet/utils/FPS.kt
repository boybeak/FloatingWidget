package com.github.boybeak.fpspet.utils

import android.view.Choreographer

abstract class FPS {

    companion object {
        const val ONE_SECOND_NANOS = 1e9f
    }

    private val fpsCallback = object : Choreographer.FrameCallback {

        private var lastFrameTime: Long = 0

        override fun doFrame(frameTimeNanos: Long) {
            val currentTime = System.nanoTime()

            val deltaTime = currentTime - lastFrameTime
            val fps = ONE_SECOND_NANOS / deltaTime

            onFrame(fps)

            lastFrameTime = currentTime
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    var isRunning: Boolean = false
        private set

    fun start() {
        Choreographer.getInstance().postFrameCallback(fpsCallback)
        isRunning = true
    }

    fun stop() {
        Choreographer.getInstance().removeFrameCallback(fpsCallback)
        isRunning = false
    }

    abstract fun onFrame(fps: Float)

}