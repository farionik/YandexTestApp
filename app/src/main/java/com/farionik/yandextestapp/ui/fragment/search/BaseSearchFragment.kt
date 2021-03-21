package com.farionik.yandextestapp.ui.fragment.search

import androidx.fragment.app.Fragment
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.di.Containers
import com.farionik.yandextestapp.di.LocalCiceroneHolder
import com.farionik.yandextestapp.ui.fragment.BackButtonListener
import com.farionik.yandextestapp.view_model.SearchViewModel
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

open class BaseSearchFragment : Fragment(), BackButtonListener {

    private val localCiceroneHolder by inject<LocalCiceroneHolder>()

    private val cicerone = localCiceroneHolder.getCicerone(Containers.MAIN_FRAGMENT_CONTAINER.name)
    private val navigator: Navigator by lazy {
        AppNavigator(requireActivity(), R.id.container, childFragmentManager)
    }
    private val router: Router
        get() = cicerone.router

    val searchViewModel by sharedViewModel<SearchViewModel>()

    override fun onBackPressed(): Boolean {
        router.exit()
        return true
    }
}