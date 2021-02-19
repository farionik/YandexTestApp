package com.farionik.yandextestapp.ui

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.blankj.utilcode.util.KeyboardUtils
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.model.SearchModel
import com.farionik.yandextestapp.ui.main.MainFragment
import com.farionik.yandextestapp.ui.main.SearchState
import com.farionik.yandextestapp.ui.main.initSearchEditText
import com.google.android.material.appbar.AppBarLayout
import org.koin.android.viewmodel.compat.ScopeCompat.viewModel
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), SearchedClickedListener {

    private lateinit var searchEditText: EditText

    private val mainViewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        searchEditText = findViewById(R.id.editText)


        val appBarLayout: AppBarLayout = findViewById(R.id.appBarLayout)
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout?.totalScrollRange ?: 1
            val percent = (verticalOffset * -1F) / totalScrollRange * 100
            mainViewModel.appBarOffsetMutableLiveData.postValue(percent.toInt())
        })


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
        searchEditText.initSearchEditText(lambda)
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
        with(searchEditText) {
            model.name.let {
                setText(it)
                setSelection(it.length)
            }
        }
    }
}