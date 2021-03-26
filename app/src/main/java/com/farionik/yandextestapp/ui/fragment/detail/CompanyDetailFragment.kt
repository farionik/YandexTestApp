package com.farionik.yandextestapp.ui.fragment.detail

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.databinding.FragmentCompanyDetailBinding
import com.farionik.yandextestapp.ui.fragment.BackButtonListener
import com.farionik.yandextestapp.ui.fragment.BaseFragment
import com.farionik.yandextestapp.ui.fragment.detail.chart.ChartFragment
import com.farionik.yandextestapp.ui.fragment.detail.news.NewsFragment
import com.farionik.yandextestapp.ui.util.TabLayoutScreenType
import com.farionik.yandextestapp.ui.util.addTabChangeListener
import com.farionik.yandextestapp.ui.util.initTabLayoutMediator
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class CompanyDetailFragment : BaseFragment(), BackButtonListener {

    private lateinit var binding: FragmentCompanyDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompanyDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolBar()
        initPagerAdapter()
        subscribe()
    }

    private fun initToolBar() {
        binding.btnBack.setOnClickListener { onBackPressed() }
        binding.btnFavourite.setOnClickListener {
            val symbol = binding.tvSymbol.text.toString()
            companyViewModel.likeCompany(symbol)
        }
    }

    override fun onBackPressed(): Boolean {
        companyViewModel.backClick()
        return true
    }

    private fun initPagerAdapter() {

        val tabTitles = listOf(
            getString(R.string.tab_chart),
            getString(R.string.tab_summary),
            getString(R.string.tab_news),
            getString(R.string.tab_forecast),
            getString(R.string.tab_ideas)
        )
        val fragments = listOf(
            ChartFragment(),
            SummaryFragment(),
            NewsFragment(),
            ForecastFragment(),
            IdeasFragment()
        )

        for (name in tabTitles) {
            binding.tabLayout.run {
                val tab = newTab()
                val customTab: TextView =
                    layoutInflater.inflate(R.layout.menu_tab_item, null, false) as TextView
                customTab.text = name
                tab.customView = customTab
                addTab(tab)
            }
        }

        binding.tabLayout.run {
            addTabChangeListener(TabLayoutScreenType.DETAIL_SCREEN)
            binding.viewPager.adapter = ScreenPagerAdapter(this@CompanyDetailFragment, fragments)
            initTabLayoutMediator(
                this,
                binding.viewPager,
                TabLayoutScreenType.MAIN_SCREEN,
                tabTitles
            )
        }
    }

    private fun subscribe() {
        companyViewModel.selectedStock.observe(viewLifecycleOwner, {
            it.stock.run {
                with(binding) {
                    tvSymbol.text = symbol
                    tvCompanyName.text = companyName
                    btnFavourite.setImageResource(
                        if (isFavourite) R.drawable.ic_star_gold
                        else R.drawable.ic_star_empty
                    )
                }
            }
        })
    }

    inner class ScreenPagerAdapter(
        fragment: Fragment, private val fragments: List<Fragment>
    ) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }
}