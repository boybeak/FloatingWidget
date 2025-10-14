package com.github.boybeak.fpspet.service

import com.github.boybeak.fltwgt.FloatingWidget
import com.github.boybeak.fpspet.IPetService
import com.github.boybeak.fpspet.view.FPSView

class PetBinder(private val service: PetService) : IPetService.Stub() {

    private val fpsWidget by lazy { FPSView(service) }
    private val floatingWidget by lazy {
        FloatingWidget.Builder()
            .setDraggable(true)
            .create(fpsWidget)
    }

    override fun showPet() {
        floatingWidget.show()
    }

    override fun dismissPet() {
        floatingWidget.dismiss()
    }

    override fun isPetShowing(): Boolean {
        return floatingWidget.isShowing
    }
}