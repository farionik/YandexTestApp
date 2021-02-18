package com.farionik.yandextestapp.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.blankj.utilcode.util.KeyboardUtils
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.databinding.ActivityMainBinding
import com.farionik.yandextestapp.model.SearchModel
import com.farionik.yandextestapp.ui.main.SearchState
import com.farionik.yandextestapp.ui.main.initSearchEditText
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity(), SearchedClickedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initTabLayout()

        val lambda = { state: SearchState ->
            when (state) {
                SearchState.ACTIVE -> {
                    openScreen(R.id.searchFragment)
                    binding.tabLayout.visibility = View.GONE
                }
                SearchState.NOT_ACTIVE -> {
                    openScreen(R.id.mainFragment)
                    binding.tabLayout.visibility = View.VISIBLE
                }
                SearchState.SEARCH -> {
                    openScreen(R.id.searchResultFragment)
                    binding.tabLayout.visibility = View.GONE
                }
            }
        }
        binding.editText.initSearchEditText(lambda)
    }

    private fun initTabLayout() {

        binding.tabLayout.getTabAt(0)

        val stockTab: TabLayout.Tab? = binding.tabLayout.getTabAt(0)
        stockTab?.let {
            it.setTabText("Stock")
            it.selectTab()
        }

        val favouriteTab: TabLayout.Tab? = binding.tabLayout.getTabAt(1)
        favouriteTab?.let {
            it.setTabText("Favourite")
            it.unselectTab()
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.selectTab()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.unselectTab()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    private fun TabLayout.Tab.setTabText(title: String) {
        val textView: TextView = customView as TextView
        textView.text = title
    }

    private fun TabLayout.Tab.selectTab() {
        val textView: TextView = customView as TextView
        with(textView) {
            textSize = 28f
            setTextColor(ContextCompat.getColor(this@MainActivity, R.color.color_black))
        }
        when (textView.text) {
            "Stock" -> openScreen(R.id.mainFragment)
            "Favourite" -> openScreen(R.id.favouriteFragment)
        }
        KeyboardUtils.hideSoftInput(this@MainActivity)
    }

    private fun TabLayout.Tab.unselectTab() {
        val textView: TextView = customView as TextView
        with(textView) {
            textSize = 18f
            setTextColor(ContextCompat.getColor(this@MainActivity, R.color.color_gray))
        }
    }

    private fun openScreen(destination: Int) {
        KeyboardUtils.hideSoftInput(this)
        findNavController(R.id.nav_host_fragment).navigate(destination)
    }

    override fun searchModelClicked(model: SearchModel) {
        with(binding.editText) {
            model.name.let {
                setText(it)
                setSelection(it.length)
            }
        }
    }
}