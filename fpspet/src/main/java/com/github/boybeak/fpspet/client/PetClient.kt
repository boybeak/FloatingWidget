package com.github.boybeak.fpspet.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.github.boybeak.fpspet.IPetService
import com.github.boybeak.fpspet.service.PetService

object PetClient {

    private var iPetService: IPetService? = null
    private var state: ConnectState = ConnectState.NONE
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            iPetService = IPetService.Stub.asInterface(service)
            state = ConnectState.CONNECTED
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            iPetService = null
            state = ConnectState.DISCONNECTED
        }

        override fun onBindingDied(name: ComponentName?) {
            state = ConnectState.DIED
        }
    }


    fun initialize(context: Context) {
        if (state == ConnectState.CONNECTED || state == ConnectState.CONNECTING) {
            return
        }
        context.applicationContext.bindService(
            Intent(context.applicationContext, PetService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )

        state = ConnectState.CONNECTING
    }

}