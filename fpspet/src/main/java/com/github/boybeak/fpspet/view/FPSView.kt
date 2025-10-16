package com.github.boybeak.fpspet.view

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.res.ResourcesCompat
import com.github.boybeak.fpspet.R
import com.github.boybeak.fpspet.utils.FPS
import kotlin.getValue
import kotlin.math.roundToInt

class FPSView : SurfaceView {

    companion object {
        private const val TAG = "FPSView"
    }

    private val fps = object : FPS() {
        private var counter = 0
        private var frameIndex = 0
        override fun onFrame(fps: Float) {
            if (counter == 0) {
                frameIndex = (frameIndex + 1) % images.size
                currentImage = BitmapFactory.decodeResource(resources, images[frameIndex])
                fpsText = "${fps.roundToInt()}"
                draw()
            }
            counter++
            if (counter == 6) {
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

    private val _srcRect = Rect(0, 0, 0, 0)
    private val srcRect: Rect get() {
        _srcRect.set(0, 0, currentImage.width, currentImage.height)
        return _srcRect
    }
    private val _dstRect = Rect(0, 0, 0, 0)
    private val dstRect: Rect get() {
        _dstRect.set(0, 0, height, height)
        return _dstRect
    }
    private var currentImage = BitmapFactory.decodeResource(resources, images[0])
    private var fpsText: String = ""

    private var showBorder = true
    private val borderPaint by lazy {
        Paint().apply {
            color = Color.DKGRAY
            style = Paint.Style.STROKE
            strokeWidth = 0f
            isAntiAlias = true
        }
    }
    private var cornerRadius = 0f

    private val bgPaint by lazy {
        Paint().apply {
            color = Color.DKGRAY
            style = Paint.Style.FILL
            isAntiAlias = true
        }
    }

    private val font by lazy {
        ResourcesCompat.getFont(context, R.font.number)
    }
    private val textPaint = Paint().apply {
        color = Color.BLUE
        textSize = 60f
        typeface = font
        isAntiAlias = true
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

    private fun draw() {
        val canvas = holder.lockCanvas()
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        if (showBorder) {
            val borderLeft = borderPaint.strokeWidth / 2
            val borderTop = borderPaint.strokeWidth / 2
            val borderRight = width.toFloat() - borderPaint.strokeWidth / 2
            val borderBottom = height.toFloat() - borderPaint.strokeWidth / 2
            canvas.drawRoundRect(borderLeft, borderTop, borderRight, borderBottom, cornerRadius, cornerRadius, borderPaint)
        }

        val bgLeft = borderPaint.strokeWidth
        val bgTop = borderPaint.strokeWidth
        val bgRight = width.toFloat() - borderPaint.strokeWidth
        val bgBottom = height.toFloat() - borderPaint.strokeWidth
        val innerCornerRadius = cornerRadius - borderPaint.strokeWidth / 2
        canvas.drawRoundRect(bgLeft, bgTop, bgRight, bgBottom, innerCornerRadius, innerCornerRadius, bgPaint)

        canvas.drawBitmap(currentImage, srcRect, dstRect, null)
        val textWidth = textPaint.measureText(fpsText)
        val textX = dstRect.right.toFloat() + (width - dstRect.width()) / 2 - textWidth / 2
        val textY = canvas.height / 2F + textPaint.textSize / 2
        canvas.drawText(fpsText, textX, textY, textPaint)
        holder.unlockCanvasAndPost(canvas)
    }

    fun setShowBorder(show: Boolean) {
        this.showBorder = show
    }

    fun setCornerRadius(radius: Float) {
        this.cornerRadius = radius
    }

    fun setBorderWidth(width: Float) {
        borderPaint.strokeWidth = width
    }

    fun setBorderColor(color: Int) {
        borderPaint.color = color
    }

    fun setBorderAlpha(alpha: Int) {
        borderPaint.alpha = alpha
    }

    override fun setBackgroundColor(color: Int) {
        bgPaint.color = color
    }

    fun setBackgroundColorAlpha(alpha: Int) {
        bgPaint.alpha = alpha
    }

    fun setTextColor(color: Int) {
        textPaint.color = color
    }

    fun setTextSize(size: Float) {
        textPaint.textSize = size
    }

}