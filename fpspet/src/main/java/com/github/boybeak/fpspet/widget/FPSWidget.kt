package com.github.boybeak.fpspet.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.boybeak.fpspet.FPS
import com.github.boybeak.fpspet.R

class FPSWidget : LinearLayout {

    private val fps = object : FPS() {
        private var counter = 0
        private var frameIndex = 0
        override fun onFrame(fps: Float) {
            if (counter == 0) {
                frameIndex = (frameIndex + 1) % images.size
                fpsImage.setImageResource(images[frameIndex])
                fpsText.text = "%.2f".format(fps)
            }
            counter++
            if (counter == 10) {
                counter = 0
            }
        }
    }

    private val images = listOf(
        R.drawable.fa_ducky_walk_000,
        R.drawable.fa_ducky_walk_001,
        R.drawable.fa_ducky_walk_002,
        R.drawable.fa_ducky_walk_003,
        R.drawable.fa_ducky_walk_004,
        R.drawable.fa_ducky_walk_005,
        R.drawable.fa_ducky_walk_006,
        R.drawable.fa_ducky_walk_007,
        R.drawable.fa_ducky_walk_008,
        R.drawable.fa_ducky_walk_009,
        R.drawable.fa_ducky_walk_010,
        R.drawable.fa_ducky_walk_011,
    )

    private val fpsText by lazy { TextView(context) }
    private val fpsImage by lazy { ImageView(context) }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        addView(fpsText)
        addView(fpsImage)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        fps.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        fps.stop()
    }

}