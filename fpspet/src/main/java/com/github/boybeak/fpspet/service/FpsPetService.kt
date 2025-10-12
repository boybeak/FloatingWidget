package com.github.boybeak.fpspet.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.github.boybeak.fltwgt.FloatingWidgetService

class FpsPetService : FloatingWidgetService() {

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }
}