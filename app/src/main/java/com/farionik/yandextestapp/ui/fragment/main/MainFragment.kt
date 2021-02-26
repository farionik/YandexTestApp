package com.farionik.yandextestapp.ui.fragment.main

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
import com.farionik.yandextestapp.ui.util.TweakableOutlineProvider
import com.farionik.yandextestapp.view_model.CompanyViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.viewmodel.ext.android.sharedViewModel
import kotlin.math.roundToInt

class MainFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private val mainViewModel by sharedViewModel<CompanyViewModel>()

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

        mainViewModel.appBarOffsetMutableLiveData.observe(viewLifecycleOwner, { changeShadow(it) })
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
    }


    private lateinit var outlineProvider: TweakableOutlineProvider

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

        val cornerRadius = resources.getDimensionPixelSize(R.dimen._2sdp).toFloat()
        outlineProvider = TweakableOutlineProvider(
            cornerRadius = cornerRadius,
            scaleX = 1f,
            scaleY = 1f,
            yShift = 0
        )
        tabLayout.outlineProvider = outlineProvider
    }

    private fun changeShadow(percent: Int) {
        val elevationPixel = 8 * resources.displayMetrics.density
        tabLayout.elevation = elevationPixel
        outlineProvider.scaleY = percent * 0.0085f
        val adjustedShiftYPixel = percent / 20 * resources.displayMetrics.density
        outlineProvider.yShift = adjustedShiftYPixel.roundToInt()
        tabLayout.invalidateOutline()
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

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> CompanyFragment()
            1 -> FavouriteFragment()
            else -> throw IllegalStateException()
        }
    }
}