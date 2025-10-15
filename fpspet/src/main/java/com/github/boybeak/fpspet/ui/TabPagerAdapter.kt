package com.github.boybeak.fpspet.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.github.boybeak.fpspet.ui.fragment.AnimationFragment
import com.github.boybeak.fpspet.ui.fragment.BgFragment
import com.github.boybeak.fpspet.ui.fragment.BorderFragment
import com.github.boybeak.fpspet.ui.fragment.FontFragment
import com.github.boybeak.fpspet.ui.fragment.GeneralFragment
import com.github.boybeak.fpspet.ui.fragment.PermissionsFragment

class TabPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragments = listOf(
        GeneralFragment(),
        BgFragment(),
        BorderFragment(),
        FontFragment(),
        AnimationFragment(),
        PermissionsFragment(),
    )

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return Tab.entries.size
    }
}