package com.github.boybeak.fpspet.vm

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainVM : ViewModel() {

    private val _cornerRadius = MutableLiveData(0f)
    val cornerRadius: LiveData<Float> get() = _cornerRadius
    private val _borderWidth = MutableLiveData(0f)
    val borderWidth: LiveData<Float> get() = _borderWidth
    private val _borderColor = MutableLiveData(Color.BLUE)
    val borderColor: LiveData<Int> get() = _borderColor


    fun setCornerRadius(radius: Float) {
        _cornerRadius.value = radius
    }

    fun setBorderWidth(width: Float) {
        _borderWidth.value = width
    }

    fun setBorderColor(color: Int) {
        _borderColor.value = color
    }

}