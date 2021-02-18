package com.farionik.yandextestapp

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.KeyboardUtils
import com.farionik.yandextestapp.databinding.ActivityMainBinding
import com.farionik.yandextestapp.main.initSearchEditText
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initTabLayout()
        binding.editText.initSearchEditText()
    }

    private fun initTabLayout() {

        binding.tabLayout.getTabAt(0)

        val stockTab: TabLayout.Tab? = binding.tabLayout.getTabAt(0)
        stockTab?.let {
            it.selectTab()
            it.setTabText("Stock")
        }

        val favouriteTab: TabLayout.Tab? = binding.tabLayout.getTabAt(1)
        favouriteTab?.let {
            it.unselectTab()
            it.setTabText("Favourite")
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

        KeyboardUtils.hideSoftInput(this@MainActivity)
    }

    private fun TabLayout.Tab.unselectTab() {
        val textView: TextView = customView as TextView
        with(textView) {
            textSize = 18f
            setTextColor(ContextCompat.getColor(this@MainActivity, R.color.color_gray))
        }
    }
}