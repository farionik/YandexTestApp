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
import com.farionik.yandextestapp.ui.main.SearchViewManager
import com.farionik.yandextestapp.ui.search.SearchFragment
import com.farionik.yandextestapp.ui.search.SearchResultFragment
import com.google.android.material.appbar.AppBarLayout
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), SearchedClickedListener, MainActivityListener {

    private lateinit var searchEditText: EditText
    private lateinit var searchViewManager: SearchViewManager

    private val mainViewModel by viewModel<MainViewModel>()

    private val mainFragment = MainFragment()

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

        searchViewManager = SearchViewManager(searchEditText, this)
        openScreen(mainFragment)
    }

    override fun searchModelClicked(model: SearchModel) {
        with(searchEditText) {
            model.name.let {
                setText(it)
                setSelection(it.length)
            }
        }
    }

    override fun openScreen(fragment: Fragment, addToBackStack: Boolean) {
        KeyboardUtils.hideSoftInput(this)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            if (addToBackStack) addToBackStack(null)
            replace(R.id.fragment_container_view, fragment)
        }
    }

    override fun backClicked() {
        supportFragmentManager.popBackStack()
    }

    override fun onBackPressed() {
        val backStackEntryCount = supportFragmentManager.backStackEntryCount
        if (backStackEntryCount > 0) {
            if (!searchViewManager.systemBackClicked()) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }
}