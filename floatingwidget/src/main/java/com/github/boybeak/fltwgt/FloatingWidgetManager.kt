package com.github.boybeak.fltwgt

import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import java.lang.ref.WeakReference

class FloatingWidgetManager(context: Context) {

    private var contextRef = WeakReference(context)
    private val winMan: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val widgets = mutableListOf<FloatingWidget>()

    val size: Int
        get() = widgets.size

    val hasWidgets: Boolean
        get() = widgets.isNotEmpty()

    fun checkPermission(context: Context): Boolean {
        return context.packageManager.checkPermission(
            android.Manifest.permission.SYSTEM_ALERT_WINDOW,
            context.packageName
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}