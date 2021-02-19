package com.farionik.yandextestapp.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.blankj.utilcode.util.KeyboardUtils
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.databinding.ActivityMainBinding
import com.farionik.yandextestapp.model.SearchModel
import com.farionik.yandextestapp.ui.main.*
import com.farionik.yandextestapp.ui.search.SearchFragment
import com.farionik.yandextestapp.ui.search.SearchResultFragment
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity(), SearchedClickedListener {

    private lateinit var binding: ActivityMainBinding

    private val mainFragment = StockFragment()
    private val favouriteFragment = FavouriteFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lambda = { state: SearchState ->
            /*when (state) {
                SearchState.ACTIVE -> {
                    openScreen(SearchFragment())
                    binding.tabLayout.visibility = View.GONE
                }
                SearchState.NOT_ACTIVE -> {
                    val selectedTabPosition = binding.tabLayout.selectedTabPosition
                    binding.tabLayout.getTabAt(selectedTabPosition)?.selectTab()
                    binding.tabLayout.visibility = View.VISIBLE
                }
                SearchState.SEARCH -> {
                    openScreen(SearchResultFragment())
                    binding.tabLayout.visibility = View.GONE
                }
            }*/
        }
        binding.editText.initSearchEditText(lambda)

        openScreen(MainFragment())
    }



    private fun openScreen(fragment: Fragment) {
        KeyboardUtils.hideSoftInput(this)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view, fragment)
        }
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