package com.farionik.yandextestapp.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.ui.AppScreens
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Replace
import com.github.terrakok.cicerone.androidx.AppNavigator
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    /*private lateinit var searchEditText: EditText
    private lateinit var searchViewManager: SearchViewManager

    private val companyViewModel by viewModel<StockViewModel>()*/
    private val navigatorHolder: NavigatorHolder by inject()
    private val navigator = AppNavigator(this, R.id.container)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigator.applyCommands(arrayOf(Replace(AppScreens.startScreen())))

        /*searchEditText = findViewById(R.id.editText)

        val appBar: AppBarLayout = findViewById(R.id.appBarLayout)
        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout?.totalScrollRange ?: 1
            val percent = (verticalOffset * -1F) / totalScrollRange * 100
            companyViewModel.appBarOffsetMutableLiveData.postValue(percent.toInt())
        })

        searchViewManager = SearchViewManager(searchEditText, this)*/


    }

/*    override fun searchModelClicked(model: SearchModel) {
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
        Timber.d("")
        *//*findNavController(R.id.nav_host_fragment)
            .navigate(R.id.action_mainFragment_to_searchFragment)*//*


        *//*KeyboardUtils.hideSoftInput(this)

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
            }*//*
    }

    override fun searchAction(request: String) {
        //companyViewModel.searchCompanies(request)
    }

    override fun backClicked() {
        searchEditText.visibility = View.VISIBLE
        *//*lifecycleScope.launch(Dispatchers.Main) {
            delay(200)
            searchEditText.visibility = View.VISIBLE
        }
        supportFragmentManager.popBackStack()*//*
    }*/

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

}