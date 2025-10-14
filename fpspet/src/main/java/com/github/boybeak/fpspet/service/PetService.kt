package com.github.boybeak.fpspet.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class PetService : Service() {

    override fun onBind(intent: Intent): IBinder {
        return PetBinder(this)
    }
}