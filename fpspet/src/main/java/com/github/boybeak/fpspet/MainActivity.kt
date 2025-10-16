package com.github.boybeak.fpspet

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.net.toUri
import androidx.viewpager2.widget.ViewPager2
import com.github.boybeak.fpspet.service.PetService
import com.github.boybeak.fpspet.ui.Tab
import com.github.boybeak.fpspet.ui.TabPagerAdapter
import com.github.boybeak.fpspet.view.FPSView
import com.github.boybeak.fpspet.vm.MainVM
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val mainVM: MainVM by viewModels()

    private val fpsView by lazy {
        findViewById<FPSView>(R.id.fpsView)
    }
    private val tabLayout by lazy {
        findViewById<TabLayout>(R.id.tabLayout)
    }
    private val viewPager by lazy {
        findViewById<ViewPager2>(R.id.viewPager)
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

        // 常规：尺寸、圆角、内边距、排列方式、是否吸附到边缘
        // 背景：颜色、透明度
        // 描边：颜色、透明度、粗细
        // 动画：选择不同动画
        // 字体：大小、颜色、斜体、加粗、字体粗细？、字体类型？
        // 权限：悬浮窗、前台通知、自启动
        /*Tab.entries.forEach { tab ->
            tabLayout.addTab(tabLayout.newTab().setText(tab.titleId))
        }*/

        viewPager.adapter = TabPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setText(Tab.entries[position].titleId)
        }.attach()

        mainVM.cornerRadius.observe(this) { radius ->
            fpsView.setCornerRadius(radius)
        }
        mainVM.borderWidth.observe(this) {
            fpsView.setBorderWidth(it)
        }
        mainVM.borderColor.observe(this) {
            fpsView.setBorderColor(it)
        }
        mainVM.borderAlpha.observe(this) {
            fpsView.setBorderAlpha(it)
        }

        checkOverlayPermission()
    }

    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            // 跳转到悬浮窗权限设置页面
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:$packageName".toUri()
            )
            startActivityForResult(intent, 100)
        } else {
            startPetService()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            // 再次检查权限是否被授予
            if (Settings.canDrawOverlays(this)) {
                startPetService()
            } else {
            }
        }
    }

    private fun startPetService() {
        val intent = Intent(this, PetService::class.java)
        startService(intent)
    }
}