package com.github.boybeak.fltwgt.app

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.boybeak.fltwgt.FloatingWidget

class MainActivity : AppCompatActivity() {

    private val widget: FloatingWidget by lazy {
        FloatingWidget.Builder(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            .setDraggable(true)
            .setClickThrough(false)
            .create(this, R.layout.widget_simple)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.toggleBtn).setOnClickListener {
            if (widget.isShowing) {
                widget.dismiss()
            } else {
                widget.show()
            }
        }
        findViewById<Button>(R.id.clickThroughBtn).setOnClickListener {
            widget.run {
                setClickThrough(!isClickThrough)
            }
        }
        findViewById<Button>(R.id.checkParentBtn).setOnClickListener {
            Log.d("MainActivity", "checkParent: ${widget.view.parent}")
        }

        findViewById<RadioGroup>(R.id.gravityGroup).setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.topLeft -> {
                    widget?.setGravity(Gravity.TOP or Gravity.LEFT)
                }
                R.id.topRight -> {
                    widget?.setGravity(Gravity.TOP or Gravity.RIGHT)
                }
                R.id.bottomLeft -> {
                    widget?.setGravity(Gravity.BOTTOM or Gravity.LEFT)
                }
                R.id.bottomRight -> {
                    widget?.setGravity(Gravity.BOTTOM or Gravity.RIGHT)
                }
                R.id.center -> {
                    widget?.setGravity(Gravity.CENTER)
                }
            }
        }
        findViewById<Button>(R.id.changeBtn).setOnClickListener {
            widget.run {
                view.findViewById<ImageView>(R.id.image).setImageResource(android.R.drawable.ic_menu_view)
            }
        }
    }
}