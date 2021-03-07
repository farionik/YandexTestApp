package com.farionik.yandextestapp.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.databinding.ActivityCompanyDetailBinding
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.ui.fragment.detail.*
import com.farionik.yandextestapp.ui.fragment.detail.chart.ChartFragment
import com.farionik.yandextestapp.ui.util.*
import com.farionik.yandextestapp.view_model.CompanyDetailViewModel
import org.koin.android.viewmodel.ext.android.viewModel

const val DETAIL_COMPANY_SYMBOL = "DETAIL_COMPANY_SYMBOL"

class CompanyDetailActivity : AppCompatActivity() {

    private val viewModel by viewModel<CompanyDetailViewModel>()

    private lateinit var companyID: String
    private var companyEntity: CompanyEntity? = null

    private lateinit var binding: ActivityCompanyDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompanyDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        companyID = intent.extras?.getString(DETAIL_COMPANY_SYMBOL) ?: ""
        if (companyID.isEmpty()) {
            finish()
            return
        } else {
            viewModel.setCompanyDetail(companyID)
        }

        initViews()
        subscribeUI()
    }

    private fun initViews() {
        binding.toolbar.run {
            navigationIcon = ContextCompat.getDrawable(context, R.drawable.ic_back)
            setSupportActionBar(this)
        }

        initPagerAdapter()
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
            binding.viewPager.adapter = ScreenPagerAdapter(this@CompanyDetailActivity, fragments)
            initTabLayoutMediator(
                this,
                binding.viewPager,
                TabLayoutScreenType.MAIN_SCREEN,
                tabTitles
            )
        }
    }

    private fun subscribeUI() {
        viewModel.companyDetailModelLiveData.observe(this, {
            with(binding) {
                companyEntity = it
                tvSymbol.text = it.symbol
                tvCompanyName.text = it.companyName
                invalidateOptionsMenu()
            }
        })
    }

    override fun onNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_company_detail, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        companyEntity?.run {
            val findItem = menu?.findItem(R.id.action_favorite)

            findItem?.run {
                setIcon(
                    if (isFavourite) {
                        ContextCompat.getDrawable(
                            this@CompanyDetailActivity,
                            R.drawable.ic_star_gold
                        )
                    } else {
                        ContextCompat.getDrawable(
                            this@CompanyDetailActivity,
                            R.drawable.ic_star_empty
                        )
                    }
                )
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.action_favorite -> {
            viewModel.likeCompany(companyID)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    inner class ScreenPagerAdapter(
        activity: FragmentActivity,
        private val fragments: List<Fragment>
    ) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }
}