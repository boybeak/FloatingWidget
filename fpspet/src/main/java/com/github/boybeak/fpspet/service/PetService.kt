package com.github.boybeak.fpspet.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.github.boybeak.fltwgt.FloatingWidget
import com.github.boybeak.fpspet.widget.FPSWidget

class PetService : Service() {

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    override fun onCreate() {
        super.onCreate()

        showFPSPet()
    }

    private fun showFPSPet() {
        val fpsWidget = FPSWidget(this)
        val widget = FloatingWidget.Builder()
            .setDraggable(true)
            .show(fpsWidget)
    }
}