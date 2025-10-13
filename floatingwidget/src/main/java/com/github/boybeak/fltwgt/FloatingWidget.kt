package com.github.boybeak.fltwgt

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import kotlin.math.abs

class FloatingWidget private constructor(val view: View) {

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
                    isDragging = false
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    // 计算移动距离
                    val deltaX = event.rawX - initialTouchX
                    val deltaY = event.rawY - initialTouchY

                    // 更新窗口位置
                    params.x = initialX + deltaX.toInt()
                    params.y = initialY + deltaY.toInt()

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

    val isShowing: Boolean
        get() = view.parent != null

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

    fun setAutoSnap(autoSnap: Boolean) {
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

    private fun update(block: WindowManager.LayoutParams.() -> Unit) {
        val winLayoutParams = view.layoutParams as? WindowManager.LayoutParams ?: return
        block(winLayoutParams)
        windowManager.updateViewLayout(view, winLayoutParams)
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

        constructor() : this(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE
        )

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

        fun create(view: View): FloatingWidget {
            return FloatingWidget(view).apply {
                setDraggable(draggable)
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
