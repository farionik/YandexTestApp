package com.farionik.yandextestapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.blankj.utilcode.util.KeyboardUtils
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.di.Containers.MAIN_FRAGMENT_CONTAINER
import com.farionik.yandextestapp.di.LocalCiceroneHolder
import com.farionik.yandextestapp.ui.AppScreens
import com.farionik.yandextestapp.ui.fragment.search.SearchViewManager
import com.farionik.yandextestapp.view_model.SearchViewModel
import com.farionik.yandextestapp.view_model.StockViewModel
import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.google.android.material.appbar.AppBarLayout
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

class MainFragment : Fragment(), BackButtonListener {

    private lateinit var searchEditText: EditText
    private lateinit var searchViewManager: SearchViewManager

    private val localCiceroneHolder by inject<LocalCiceroneHolder>()
    private val searchViewModel by sharedViewModel<SearchViewModel>()
    private val stockViewModel by sharedViewModel<StockViewModel>()

    private val cicerone = localCiceroneHolder.getCicerone(MAIN_FRAGMENT_CONTAINER.name)

    private lateinit var navigator: AppNavigator

    private val router: Router
        get() = cicerone.router

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigator = object : AppNavigator(requireActivity(), R.id.container, childFragmentManager) {
            override fun setupFragmentTransaction(
                fragmentTransaction: FragmentTransaction,
                currentFragment: Fragment?,
                nextFragment: Fragment?
            ) {
                fragmentTransaction.setCustomAnimations(
                    R.anim.enter,
                    R.anim.exit,
                    R.anim.pop_enter,
                    R.anim.pop_exit
                )
                fragmentTransaction.setReorderingAllowed(true)
                super.setupFragmentTransaction(fragmentTransaction, currentFragment, nextFragment)
            }

            override fun applyCommands(commands: Array<out Command>) {
                KeyboardUtils.hideSoftInput(activity)
                super.applyCommands(commands)
            }
        }
        router.replaceScreen(AppScreens.stockScreen())

        searchEditText = view.findViewById(R.id.editText)

        val appBar: AppBarLayout = view.findViewById(R.id.appBarLayout)
        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout?.totalScrollRange ?: 1
            val percent = (verticalOffset * -1F) / totalScrollRange * 100
            stockViewModel.appBarOffsetMutableLiveData.postValue(percent.toInt())
        })

        searchViewManager = SearchViewManager(searchEditText, router, searchViewModel)
        searchViewModel.searchAction.observe(
            viewLifecycleOwner, {
                val symbol = it.title()
                searchEditText.setText(symbol)
                searchEditText.setSelection(symbol.length)
            }
        )
    }


    override fun onResume() {
        super.onResume()
        cicerone.getNavigatorHolder().setNavigator(navigator)
    }

    override fun onPause() {
        cicerone.getNavigatorHolder().removeNavigator()
        super.onPause()
    }

    override fun onBackPressed(): Boolean {
        if (searchViewManager.systemBackClicked()) {
            return true
        }

        val fragment = childFragmentManager.findFragmentById(R.id.container)
        return (fragment != null && fragment is BackButtonListener
                && (fragment as BackButtonListener).onBackPressed())
    }
}