package com.github.boybeak.fltwgt

import android.app.Service

abstract class FloatingWidgetService : Service() {

    val floatingWidgetManager by lazy { FloatingWidgetManager(this) }

}