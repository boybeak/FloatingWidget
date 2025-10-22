package com.github.boybeak.fltwgt

import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import com.github.boybeak.fltwgt.magnetic.Attraction
import com.github.boybeak.fltwgt.magnetic.Side
import kotlin.math.abs

class FloatingWidget private constructor(val view: View) {

    companion object {
        private const val TAG = "FloatingWidget"
    }

    val id = View.generateViewId()

    private val windowManager by lazy {
        view.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private val dragBehavior = object : View.OnTouchListener {

        private var initialX = 0
        private var initialY = 0
        private var initialTouchX = 0f
        private var initialTouchY = 0f
        private var isDragging = false

        private var xMoveDirectionFactor: Int = 0

        private var yMoveDirectionFactor: Int = 0

        private val params by lazy { view.layoutParams as WindowManager.LayoutParams }

        override fun onTouch(
            v: View,
            event: MotionEvent
        ): Boolean {
            return when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 记录初始位置
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY

                    // 计算移动方向
                    xMoveDirectionFactor = when (gravity) {
                        Gravity.LEFT -> 1
                        Gravity.RIGHT -> -1
                        else -> 0
                    }

                    // 计算移动方向
                    yMoveDirectionFactor = when (gravity) {
                        Gravity.TOP -> 1
                        Gravity.BOTTOM -> -1
                        else -> 0
                    }

                    isDragging = false
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    // 计算移动距离
                    val deltaX = event.rawX - initialTouchX
                    val deltaY = event.rawY - initialTouchY

                    // 更新窗口位置
                    params.x = initialX + deltaX.toInt() * xMoveDirectionFactor
                    params.y = initialY + deltaY.toInt() * yMoveDirectionFactor

                    // 更新视图布局
                    windowManager.updateViewLayout(view, params)

                    // 标记为正在拖动
                    if (abs(deltaX) > 5 || abs(deltaY) > 5) {
                        isDragging = true
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    // 如果是拖动操作，不触发点击事件
                    if (isDragging) {
                        magneticAttraction()
                        true
                    } else {
                        // 处理点击事件
                        v.performClick()
                        false
                    }
                }

                else -> false
            }
        }
    }

    val isClickThrough: Boolean
        get() = layoutParams {
            flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE != 0
        } ?: false

    var isDraggable: Boolean = false
        private set

    var isMagnetic: Boolean = false
        private set
    var magneticRadius: Int = -1
        private set
    var magneticSide: Side = Side.All
        private set

    val isShowing: Boolean
        get() = view.parent != null

    val gravity: Int
        get() = layoutParams {
            gravity
        } ?: Gravity.NO_GRAVITY

    val x: Int
        get() = layoutParams {
            x
        } ?: 0

    val y: Int
        get() = layoutParams {
            y
        } ?: 0

    val width: Int
        get() = layoutParams {
            width
        } ?: 0

    val height: Int
        get() = layoutParams {
            height
        } ?: 0

    val screenWidth: Int
        get() {
            val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                view.context.display
            } else {
                windowManager.defaultDisplay
            }
            return display.width
        }

     val screenHeight: Int
        get() {
            val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                view.context.display
            } else {
                windowManager.defaultDisplay
            }
            return display.height
        }

    val layoutDirection: Int get() = view.layoutDirection

    fun setSize(width: Int, height: Int) = update {
        this.width = width
        this.height = height
    }

    fun setGravity(gravity: Int) = update {
        this.gravity = gravity
    }

    fun setClickThrough(enabled: Boolean) = update {
        this.setClickThrough(enabled)
    }

    fun setDraggable(draggable: Boolean) {
        view.setOnTouchListener(if (draggable) dragBehavior else null)
        isDraggable = draggable
    }

    fun setMagnetic(enable: Boolean) {
        this.isMagnetic = enable
        if (isShowing) {
            magneticAttraction()
        }
    }

    fun setMagneticRadius(radius: Int) {
        this.magneticRadius = radius
    }

    fun setMagneticSide(side: Side) {
        this.magneticSide = side
    }

    private fun show(layoutParams: WindowManager.LayoutParams) {
        windowManager.addView(view, layoutParams)
    }

    fun show() {
        windowManager.addView(view, view.layoutParams as WindowManager.LayoutParams)
    }

    fun dismiss() {
        windowManager.removeView(view)
    }

    private fun magneticAttraction() {
        if (!isShowing) return
        if (!isMagnetic) return
        val attraction = Attraction(this)
        val winLayoutParams = view.layoutParams as? WindowManager.LayoutParams ?: return
        /*val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.context.display
        } else {
            windowManager.defaultDisplay
        }
        val screenWidth = display.width
        val screenHeight = display.height
        val screenCenterX = screenWidth / 2f
        val screenCenterY = screenHeight / 2f
        Log.d("FloatingWidget", "magneticAttraction: $screenWidth, $screenHeight")
        val widgetWidth = winLayoutParams.width
        val widgetHeight = winLayoutParams.height
        val currentX = winLayoutParams.x.toFloat()
        val currentY = winLayoutParams.y.toFloat()

        val centerX = currentX + widgetWidth / 2
        val centerY = currentY + widgetHeight / 2

        var targetX = currentX
        var targetY = currentY*/

        fun animateMagneticAttraction(targetX: Int, targetY: Int) {
            // 添加属性动画
            if (targetX != x || targetY != y) {
                ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = 200
                    interpolator = LinearInterpolator()
                    addUpdateListener { animation ->
                        val fraction = animation.animatedValue as Float
                        winLayoutParams.x = (x + (targetX - x) * fraction).toInt()
                        winLayoutParams.y = (y + (targetY - y) * fraction).toInt()
                        if (isShowing) {
                            windowManager.updateViewLayout(view, winLayoutParams)
                        }
                    }
                    start()
                }
            }
        }

        animateMagneticAttraction(attraction.targetX, attraction.targetY)

        // 计算目标位置（保留现有位置判断逻辑）
        /*if (currentX < 0) {
            targetX = 0f
        } else if (currentX + widgetWidth > screenWidth) {
            targetX = (screenWidth - widgetWidth).toFloat()
        }
        if (currentY < 0) {
            targetY = 0f
        } else if (currentY + widgetHeight > screenHeight) {
            targetY = (screenHeight - widgetHeight).toFloat()
        }*/

    }

    private fun update(block: WindowManager.LayoutParams.() -> Unit) {
        val winLayoutParams = view.layoutParams as? WindowManager.LayoutParams ?: return
        block(winLayoutParams)
        if (isShowing) {
            windowManager.updateViewLayout(view, winLayoutParams)
        }
    }

    private fun <T> layoutParams(block: WindowManager.LayoutParams.() -> T): T? {
        return (view.layoutParams as? WindowManager.LayoutParams)?.run(block)
    }

    class Builder(type: Int) {

        private val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            android.graphics.PixelFormat.TRANSLUCENT
        )

        private var draggable = false
        private var magnetic = false
        private var magneticRadius = -1
        private var magneticSide: Side = Side.Horizontal

        constructor() : this(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE
        )

        fun setSize(width: Int, height: Int): Builder {
            layoutParams.width = width
            layoutParams.height = height
            return this
        }

        fun setGravity(gravity: Int): Builder {
            layoutParams.gravity = gravity
            return this
        }

        fun setClickThrough(enabled: Boolean): Builder {
            layoutParams.setClickThrough(enabled)
            return this
        }

        fun setDraggable(draggable: Boolean): Builder {
            this.draggable = draggable
            return this
        }

        fun setMagnetic(enable: Boolean): Builder {
            this.magnetic = enable
            return this
        }

        fun setMagneticRadius(radius: Int): Builder {
            this.magneticRadius = radius
            return this
        }

        fun setMagneticSide(side: Side): Builder {
            this.magneticSide = side
            return this
        }

        fun create(view: View): FloatingWidget {
            return FloatingWidget(view).apply {
                setDraggable(this@Builder.draggable)

                setMagneticRadius(this@Builder.magneticRadius)
                setMagneticSide(this@Builder.magneticSide)
                setMagnetic(this@Builder.magnetic)

                view.layoutParams = layoutParams
            }
        }

        fun create(context: Context, layout: Int): FloatingWidget {
            return create(LayoutInflater.from(context).inflate(layout, null))
        }

        fun show(view: View): FloatingWidget {
            return create(view).apply { show(layoutParams) }
        }

        fun show(context: Context, layout: Int): FloatingWidget {
            return create(context, layout).apply { show(layoutParams) }
        }
    }

}

private fun WindowManager.LayoutParams.setClickThrough(enabled: Boolean) {
    this.flags = if (enabled) {
        this.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
        this.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    } else {
        this.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()
        this.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    }
}
