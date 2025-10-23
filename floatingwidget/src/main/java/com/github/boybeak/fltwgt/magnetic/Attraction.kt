package com.github.boybeak.fltwgt.magnetic

import android.util.Log
import android.view.Gravity
import com.github.boybeak.fltwgt.FloatingWidget
import kotlinx.coroutines.GlobalScope

class Attraction(
    val gravity: Int,
    val layoutDirection: Int,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val screenWidth: Int,
    val screenHeight: Int,
    val magneticRadius: Int,
    val side: Side
) {
    companion object {
        private const val TAG = "Attraction"
    }

    var targetX = x
        private set
    var targetY = y
        private set

    constructor(widget: FloatingWidget): this(
        widget.gravity,
        widget.layoutDirection,
        widget.x,
        widget.y,
        widget.width,
        widget.height,
        widget.screenWidth,
        widget.screenHeight,
        widget.magneticRadius,
        widget.magneticSide
    )

    init {
        val xGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection) and Gravity.HORIZONTAL_GRAVITY_MASK
        val yGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK

        var distanceToScreenCenterX = 0
        var distanceToScreenCenterY = 0

        val xMap = HashMap<Side, Int>()
        val yMap = HashMap<Side, Int>()

        when(xGravity) {
            Gravity.LEFT -> {
                Log.d(TAG, "init(LEFT): xGravity = $xGravity")

                xMap[Side.Start] = 0
                xMap[Side.End] = screenWidth - width
            }
            Gravity.RIGHT -> {
                Log.d(TAG, "init(LEFT): xGravity = $xGravity")

                xMap[Side.Start] = screenWidth - width
                xMap[Side.End] = 0
            }
            else -> {
                Log.d(TAG, "init(CENTER): xGravity = $xGravity ${Gravity.CENTER_HORIZONTAL}")
                xMap[Side.Start] = -screenWidth / 2 + width / 2
                xMap[Side.End] = screenWidth / 2 - width / 2
            }
        }
        when(yGravity) {
            Gravity.TOP -> {
                Log.d(TAG, "init(TOP): yGravity = $yGravity")
                yMap[Side.Top] = 0
                yMap[Side.Bottom] = screenHeight - height
            }
            Gravity.BOTTOM -> {
                Log.d(TAG, "init(TOP): yGravity = $yGravity")
                yMap[Side.Top] = screenHeight - height
                yMap[Side.Bottom] = 0
            }
            else -> {
                Log.d(TAG, "init(CENTER): yGravity = $yGravity ${Gravity.CENTER_VERTICAL}")
                yMap[Side.Top] = -screenHeight / 2 + height / 2
                yMap[Side.Bottom] = screenHeight / 2 - height / 2
            }
        }

        val xDirection = side.direction and Side.Horizontal.direction
        val yDirection = side.direction and Side.Vertical.direction
        when (xDirection) {
            Side.Start.direction -> {
                targetX = xMap[Side.Start] ?: x
            }
            Side.End.direction -> {
                targetX = xMap[Side.End] ?: x
            }
            Side.Horizontal.direction -> {
                Log.d(TAG, "init(side.HORIZONTAL): xDirection = $xDirection")
                val screenCenterX = screenWidth / 2
                val anchorX = x + width / 2
                distanceToScreenCenterX = anchorX - screenCenterX
                when(xGravity) {
                    Gravity.LEFT -> {
                        targetX = if (distanceToScreenCenterX >= 0) {
                            xMap[Side.End] ?: x
                        } else {
                            xMap[Side.Start] ?: x
                        }
                    }
                    Gravity.RIGHT -> {
                        targetX = if (distanceToScreenCenterX >= 0) {
                            xMap[Side.Start] ?: x
                        } else {
                            xMap[Side.End] ?: x
                        }
                    }
                    else -> {
                        targetX = if (x >= 0) {
                            xMap[Side.End] ?: x
                        } else {
                            xMap[Side.Start] ?: x
                        }
                    }
                }
            }
            else -> {}
        }
        when (yDirection) {
            Side.Top.direction -> {
                targetY = yMap[Side.Top] ?: y
            }
            Side.Bottom.direction -> {
                targetY = yMap[Side.Bottom] ?: y
            }
            Side.Vertical.direction -> {
                val screenCenterY = screenHeight / 2
                val anchorY = y + height / 2
                distanceToScreenCenterY = anchorY - screenCenterY

                when(yGravity) {
                    Gravity.TOP -> {
                        targetY = if (distanceToScreenCenterY >= 0) {
                            yMap[Side.Bottom] ?: y
                        } else {
                            yMap[Side.Top] ?: y
                        }
                    }
                    Gravity.BOTTOM -> {
                        targetY = if (distanceToScreenCenterY >= 0) {
                            yMap[Side.Top] ?: y
                        } else {
                            yMap[Side.Bottom] ?: y
                        }
                    }
                    else -> {
                        targetY = if (y >= 0) {
                            yMap[Side.Bottom] ?: y
                        } else {
                            yMap[Side.Top] ?: y
                        }
                    }
                }
            }
            else -> {}
        }
    }
}