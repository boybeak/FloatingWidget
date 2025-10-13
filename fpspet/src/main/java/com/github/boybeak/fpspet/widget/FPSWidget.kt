package com.github.boybeak.fpspet.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.res.ResourcesCompat
import com.github.boybeak.fpspet.FPS
import com.github.boybeak.fpspet.R

class FPSWidget : SurfaceView {

    private val fps = object : FPS() {
        private var counter = 0
        private var frameIndex = 0
        override fun onFrame(fps: Float) {
            if (counter == 0) {
                frameIndex = (frameIndex + 1) % images.size
                currentImage = ResourcesCompat.getDrawable(resources, images[frameIndex], null)
                fpsText = "$fps"
                draw()
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

    private var currentImage = ResourcesCompat.getDrawable(resources, images[0], null)
    private var fpsText: String = ""

    private val paint = Paint().apply {
        color = Color.BLUE
        textSize = 30f
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.TRANSLUCENT)
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                Log.d("FPSWidget", "surfaceCreated: $holder")
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                Log.d("FPSWidget", "surfaceChanged: $holder, $format, $width, $height")
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                Log.d("FPSWidget", "surfaceDestroyed: $holder")
            }
        })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        fps.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        fps.stop()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widSpec = MeasureSpec.makeMeasureSpec(200, MeasureSpec.EXACTLY)
        val heiSpec = MeasureSpec.makeMeasureSpec(200, MeasureSpec.EXACTLY)
        super.onMeasure(widSpec, heiSpec)
    }

    private fun draw() {
        val canvas = holder.lockCanvas()
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        canvas.drawText(fpsText, 100f, 100f, paint)
        holder.unlockCanvasAndPost(canvas)
    }

}