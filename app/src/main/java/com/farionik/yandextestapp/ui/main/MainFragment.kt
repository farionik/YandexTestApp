package com.farionik.yandextestapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.KeyboardUtils
import com.farionik.yandextestapp.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)

        initTabLayout()
        initPagerAdapter()
    }

    private fun initPagerAdapter() {
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            with(tab) {
                val customTab: TextView =
                    layoutInflater.inflate(R.layout.menu_tab_item, null, false) as TextView
                customView = customTab
                when (position) {
                    0 -> {
                        customTab.text = getString(R.string.tab_stock)
                        changeDisplay(true)
                    }
                    1 -> {
                        customTab.text = getString(R.string.tab_favourite)
                        tab.changeDisplay(false)
                    }
                }
            }
        }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)


            }
        })
    }

    private fun initTabLayout() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab.changeDisplay(true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab.changeDisplay(false)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        tabLayout.elevation
    }

    private fun TabLayout.Tab?.changeDisplay(asSelected: Boolean) {
        this?.let {
            val textView: TextView? = customView as? TextView
            textView?.let {
                with(it) {
                    // изменили размер текста
                    textSize = if (asSelected) 28f else 18f
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

    private inner class ScreenSlidePagerAdapter(fragment: Fragment) :
        FragmentStateAdapter(fragment) {
        private val mainFragment = StockFragment()
        private val favouriteFragment = FavouriteFragment()

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> mainFragment
            1 -> favouriteFragment
            else -> StockFragment()
        }
    }
}