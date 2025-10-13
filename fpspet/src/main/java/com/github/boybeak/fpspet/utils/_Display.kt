package com.github.boybeak.fpspet.utils

import android.content.Context
import android.os.Build
import android.view.WindowManager
import kotlin.math.roundToInt

private const val TAG = "_Display"

val Context.isLTPOSupported: Boolean
    get() {
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.display
        } else {
            (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        }
        val refreshRates = display.supportedRefreshRates

        return refreshRates.any { it.roundToInt() <= 10 }
    }
