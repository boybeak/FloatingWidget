package com.github.boybeak.fltwgt

import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView

object FloatingWidgetManager {

    private lateinit var winMan: WindowManager

    fun init(context: Context) {
        winMan = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun checkPermission(context: Context): Boolean {
        return context.packageManager.checkPermission(
            android.Manifest.permission.SYSTEM_ALERT_WINDOW,
            context.packageName
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    fun createWidget(context: Context) {
        val view = TextView(context)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            android.graphics.PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.LEFT
        winMan.addView(view, params)
    }
}