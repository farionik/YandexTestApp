package com.farionik.yandextestapp.ui.util

import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.KeyboardUtils
import com.farionik.yandextestapp.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

enum class TabLayoutScreenType {
    MAIN_SCREEN, DETAIL_SCREEN
}

fun initTabLayoutMediator(
    tabLayout: TabLayout,
    viewPager: ViewPager2,
    tabLayoutScreenType: TabLayoutScreenType,
    titles: List<String>
) {
    TabLayoutMediator(tabLayout, viewPager) { tab, position ->
        with(tab) {

            val context = tabLayout.context
            val layoutInflater = LayoutInflater.from(context)

            val customTab: TextView =
                layoutInflater.inflate(R.layout.menu_tab_item, null, false) as TextView
            customView = customTab
            customTab.text = titles[position]

            when (position) {
                0 -> tab.changeDisplay(true, tabLayoutScreenType)
                else -> tab.changeDisplay(false, tabLayoutScreenType)
            }
        }
    }.attach()
}

fun TabLayout.addTabChangeListener(tabLayoutScreenType: TabLayoutScreenType) {
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab.changeDisplay(true, tabLayoutScreenType)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            tab.changeDisplay(false, tabLayoutScreenType)
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {

        }
    })
}

private fun TabLayout.Tab?.changeDisplay(
    asSelected: Boolean,
    tabLayoutScreenType: TabLayoutScreenType
) {

    val selectSize = when (tabLayoutScreenType) {
        TabLayoutScreenType.MAIN_SCREEN -> 28f
        TabLayoutScreenType.DETAIL_SCREEN -> 18f
    }

    val defaultSize = when (tabLayoutScreenType) {
        TabLayoutScreenType.MAIN_SCREEN -> 18f
        TabLayoutScreenType.DETAIL_SCREEN -> 14f
    }

    this?.let {
        val textView: TextView? = customView as? TextView
        textView?.let {
            with(it) {
                // изменили размер текста
                textSize = if (asSelected) selectSize else defaultSize
                // поменяли цвет
                setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (asSelected) R.color.color_black else R.color.color_gray
                    )
                )
            }
            KeyboardUtils.hideSoftInput(it)
        }
    }
}