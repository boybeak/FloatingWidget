package com.github.boybeak.fltwgt.magnetic

import android.animation.ValueAnimator
import android.util.Log
import android.view.Gravity
import android.view.animation.LinearInterpolator
import com.github.boybeak.fltwgt.FloatingWidget

class Attraction(
    val widget: FloatingWidget
) {
    companion object {
        private const val TAG = "Attraction"
    }

    private val x get() = widget.x
    private val y get() = widget.y

    private val gravity get() = widget.gravity
    private val layoutDirection get() = widget.layoutDirection
    private val magneticStrategy get() = widget.magneticStrategy
    private val magneticRadius get() = widget.magneticRadius
    private val sideMargins get() = widget.sideMargins
    private val side get() = widget.magneticSide

    private val screenWidth get() = widget.screenWidth
    private val screenHeight get() = widget.screenHeight
    private val width get() = widget.width
    private val height get() = widget.height

    private val marginOfStartSide: Int get() {
        return sideMargins[Side.Start] ?: sideMargins[Side.Horizontal] ?: 0
    }
    private val marginOfEndSide: Int get() {
        return sideMargins[Side.End] ?: sideMargins[Side.Horizontal] ?: 0
    }
    private val marginOfTopSide: Int get() {
        return sideMargins[Side.Top] ?: sideMargins[Side.Vertical] ?: 0
    }
    private val marginOfBottomSide: Int get() {
        return sideMargins[Side.Bottom] ?: sideMargins[Side.Vertical] ?: 0
    }

    private val isCloseEnoughStrategy: Boolean get() {
        return magneticStrategy == MagneticStrategy.CLOSE_ENOUGH
    }

    fun animate() {
        val xGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection) and Gravity.HORIZONTAL_GRAVITY_MASK
        val yGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK

        var distanceToScreenCenterX = 0
        var distanceToScreenCenterY = 0

        val xMap = HashMap<Side, Int>()
        val yMap = HashMap<Side, Int>()

        var targetX = widget.x
        var targetY = widget.y

        when(xGravity) {
            Gravity.LEFT -> {
                val xTargetStart = marginOfStartSide
                val xTargetEnd = screenWidth - width - marginOfEndSide

                val shouldSnapStart = if (isCloseEnoughStrategy) x <= xTargetStart + magneticRadius else true
                val shouldSnapEnd = if (isCloseEnoughStrategy) x >= xTargetEnd - magneticRadius else true

                xMap[Side.Start] = if (shouldSnapStart) xTargetStart else x
                xMap[Side.End] = if (shouldSnapEnd) xTargetEnd else x
            }
            Gravity.RIGHT -> {
                val xTargetStart = screenWidth - width - marginOfEndSide
                val xTargetEnd = marginOfStartSide

                val shouldSnapStart = if (isCloseEnoughStrategy) x >= xTargetStart - magneticRadius else true
                val shouldSnapEnd = if (isCloseEnoughStrategy) x <= xTargetEnd + magneticRadius else true

                xMap[Side.Start] = if (shouldSnapStart) xTargetStart else x
                xMap[Side.End] = if (shouldSnapEnd) xTargetEnd else x
            }
            else -> {
                val xTargetStart = -screenWidth / 2 + width / 2 + marginOfStartSide
                val xTargetEnd = screenWidth / 2 - width / 2 - marginOfEndSide

                val shouldSnapStart = if (isCloseEnoughStrategy) x <= xTargetStart + magneticRadius else true
                val shouldSnapEnd = if (isCloseEnoughStrategy) x >= xTargetEnd - magneticRadius else true

                xMap[Side.Start] = if (shouldSnapStart) xTargetStart else x
                xMap[Side.End] = if (shouldSnapEnd) xTargetEnd else x
            }
        }
        when(yGravity) {
            Gravity.TOP -> {
                val yTargetTop = marginOfTopSide
                val yTargetBottom = screenHeight - height - marginOfBottomSide

                val shouldSnapTop = if (isCloseEnoughStrategy) y <= yTargetTop + magneticRadius else true
                val shouldSnapBottom = if (isCloseEnoughStrategy) y >= yTargetBottom - magneticRadius else true

                yMap[Side.Top] = if (shouldSnapTop) yTargetTop else y
                yMap[Side.Bottom] = if (shouldSnapBottom) yTargetBottom else y
            }
            Gravity.BOTTOM -> {
                val yTargetTop = screenHeight - height - marginOfBottomSide
                val yTargetBottom = marginOfTopSide

                val shouldSnapTop = if (isCloseEnoughStrategy) y >= yTargetTop - magneticRadius else true
                val shouldSnapBottom = if (isCloseEnoughStrategy) y <= yTargetBottom + magneticRadius else true

                yMap[Side.Top] = if (shouldSnapTop) yTargetTop else y
                yMap[Side.Bottom] = if (shouldSnapBottom) yTargetBottom else y
            }
            else -> {
                val yTargetTop = -screenHeight / 2 + height / 2 + marginOfTopSide
                val yTargetBottom = screenHeight / 2 - height / 2 - marginOfBottomSide

                val shouldSnapTop = if (isCloseEnoughStrategy) y <= yTargetTop + magneticRadius else true
                val shouldSnapBottom = if (isCloseEnoughStrategy) y >= yTargetBottom - magneticRadius else true

                yMap[Side.Top] = if (shouldSnapTop) yTargetTop else y
                yMap[Side.Bottom] = if (shouldSnapBottom) yTargetBottom else y
            }
        }

        val xDirection = side.direction and Side.Horizontal.direction
        val yDirection = side.direction and Side.Vertical.direction

        when (xDirection) {
            Side.Start.direction -> {
                targetX = (xMap[Side.Start] ?: x)
            }
            Side.End.direction -> {
                targetX = (xMap[Side.End] ?: x)
            }
            Side.Horizontal.direction -> {
                Log.d(TAG, "init(side.HORIZONTAL): xDirection = $xDirection")
                val screenCenterX = screenWidth / 2
                val anchorX = x + width / 2
                distanceToScreenCenterX = anchorX - screenCenterX
                when(xGravity) {
                    Gravity.LEFT -> {
                        targetX = if (distanceToScreenCenterX >= 0) {
                            (xMap[Side.End] ?: x)
                        } else {
                            (xMap[Side.Start] ?: x)
                        }
                    }
                    Gravity.RIGHT -> {
                        targetX = if (distanceToScreenCenterX >= 0) {
                            (xMap[Side.Start] ?: x)
                        } else {
                            (xMap[Side.End] ?: x)
                        }
                        Log.d(TAG, "init(side.HORIZONTAL): xGravity=right targetX=$targetX")
                    }
                    else -> {
                        targetX = if (x >= 0) {
                            (xMap[Side.End] ?: x)
                        } else {
                            (xMap[Side.Start] ?: x)
                        }
                    }
                }
            }
            else -> {}
        }
        when (yDirection) {
            Side.Top.direction -> {
                targetY = (yMap[Side.Top] ?: y)
            }
            Side.Bottom.direction -> {
                targetY = (yMap[Side.Bottom] ?: y)
            }
            Side.Vertical.direction -> {
                val screenCenterY = screenHeight / 2
                val anchorY = y + height / 2
                distanceToScreenCenterY = anchorY - screenCenterY

                when(yGravity) {
                    Gravity.TOP -> {
                        targetY = if (distanceToScreenCenterY >= 0) {
                            (yMap[Side.Bottom] ?: y)
                        } else {
                            (yMap[Side.Top] ?: y)
                        }
                    }
                    Gravity.BOTTOM -> {
                        targetY = if (distanceToScreenCenterY >= 0) {
                            (yMap[Side.Top] ?: y)
                        } else {
                            (yMap[Side.Bottom] ?: y)
                        }
                    }
                    else -> {
                        targetY = if (y >= 0) {
                            (yMap[Side.Bottom] ?: y)
                        } else {
                            (yMap[Side.Top] ?: y)
                        }
                    }
                }
            }
            else -> {}
        }

        animateMagneticAttraction(targetX, targetY)
    }

    private fun animateMagneticAttraction(targetX: Int, targetY: Int) {

        val x = this@Attraction.x
        val y = this@Attraction.y

        Log.d(TAG, "animateMagneticAttraction targetX=$targetX targetY=$targetY")

        // 添加属性动画
        if (targetX != x || targetY != y) {
            ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 200
                interpolator = LinearInterpolator()
                addUpdateListener { animation ->
                    val fraction = animation.animatedValue as Float
                    if (widget.isShowing) {
                        widget.update {
                            this.x = (x + (targetX - x) * fraction).toInt()
                            this.y = (y + (targetY - y) * fraction).toInt()
                        }
                    }
                }
                start()
            }
        }
    }

}