package com.farionik.yandextestapp.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.databinding.FragmentMainBinding
import com.farionik.yandextestapp.ui.util.*
import com.farionik.yandextestapp.view_model.CompanyViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import kotlin.math.roundToInt

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var outlineProvider: TweakableOutlineProvider

    private val mainViewModel by sharedViewModel<CompanyViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPagerAdapter()

        mainViewModel.appBarOffsetMutableLiveData.observe(viewLifecycleOwner, { changeShadow(it) })
    }

    private fun initPagerAdapter() {
        binding.tabLayout.let {
            it.addTabChangeListener(TabLayoutScreenType.MAIN_SCREEN)
            val cornerRadius = resources.getDimensionPixelSize(R.dimen._2sdp).toFloat()
            outlineProvider = TweakableOutlineProvider(
                cornerRadius = cornerRadius,
                scaleX = 1f,
                scaleY = 1f,
                yShift = 0
            )
            it.outlineProvider = outlineProvider

            val tabTitles = listOf(getString(R.string.tab_stock), getString(R.string.tab_favourite))
            val fragments = listOf(CompanyFragment(), FavouriteFragment())

            binding.viewPager.adapter = ScreenPagerAdapter(this@MainFragment, fragments)

            initTabLayoutMediator(
                it,
                binding.viewPager,
                TabLayoutScreenType.MAIN_SCREEN,
                tabTitles
            )
        }
    }

    private fun changeShadow(percent: Int) {
        val elevationPixel = 8 * resources.displayMetrics.density
        binding.tabLayout.elevation = elevationPixel
        outlineProvider.scaleY = percent * 0.0085f
        val adjustedShiftYPixel = percent / 20 * resources.displayMetrics.density
        outlineProvider.yShift = adjustedShiftYPixel.roundToInt()
        binding.tabLayout.invalidateOutline()
    }

    inner class ScreenPagerAdapter(fragment: Fragment, private val fragments: List<Fragment>) :
        FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }
}