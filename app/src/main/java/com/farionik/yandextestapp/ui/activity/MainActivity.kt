package com.farionik.yandextestapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.KeyboardUtils
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.ui.model.SearchModel
import com.farionik.yandextestapp.view_model.CompanyViewModel
import com.farionik.yandextestapp.ui.fragment.search.SearchedClickedListener
import com.farionik.yandextestapp.ui.fragment.detail.CompanyDetailFragment
import com.farionik.yandextestapp.ui.fragment.main.MainFragment
import com.farionik.yandextestapp.ui.fragment.search.SearchViewManager
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), SearchedClickedListener, MainActivityListener {

    private lateinit var searchEditText: EditText
    private lateinit var searchViewManager: SearchViewManager

    private val companyViewModel by viewModel<CompanyViewModel>()

    private val mainFragment = MainFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        searchEditText = findViewById(R.id.editText)

        val appBar: AppBarLayout = findViewById(R.id.appBarLayout)
        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout?.totalScrollRange ?: 1
            val percent = (verticalOffset * -1F) / totalScrollRange * 100
            companyViewModel.appBarOffsetMutableLiveData.postValue(percent.toInt())
        })

        searchViewManager = SearchViewManager(searchEditText, this)
        openScreen(mainFragment, false)
    }

    override fun searchModelClicked(model: SearchModel) {
        with(searchEditText) {
            model.name.let {
                setText(it)
                setSelection(it.length)
            }
        }
    }

    override fun openDetailScreen(symbol: String) {
        val intent = Intent(this, CompanyDetailActivity::class.java).apply {
            putExtra(DETAIL_COMPANY_SYMBOL, symbol)
        }
        startActivity(intent)
    }

    override fun openScreen(fragment: Fragment, addToBackStack: Boolean) {
        KeyboardUtils.hideSoftInput(this)

        supportFragmentManager.commit {
            setReorderingAllowed(true)

            if (addToBackStack) {
                addToBackStack(null)
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
            }
            replace(R.id.fragment_container_view, fragment)
        }

        if (fragment is CompanyDetailFragment) {
            lifecycleScope.launch(Dispatchers.Main) {
                delay(200)
                searchEditText.visibility = View.GONE
            }
        }
    }

    override fun backClicked() {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(200)
            searchEditText.visibility = View.VISIBLE
        }
        supportFragmentManager.popBackStack()
    }

    override fun onBackPressed() {
        searchEditText.visibility = View.VISIBLE
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