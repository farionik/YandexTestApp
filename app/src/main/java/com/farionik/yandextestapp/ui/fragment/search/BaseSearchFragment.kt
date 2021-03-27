package com.farionik.yandextestapp.ui.fragment.search

import androidx.fragment.app.Fragment
import com.farionik.yandextestapp.di.Containers
import com.farionik.yandextestapp.di.LocalCiceroneHolder
import com.farionik.yandextestapp.ui.fragment.BackButtonListener
import com.farionik.yandextestapp.view_model.CompanyViewModel
import com.farionik.yandextestapp.view_model.SearchViewModel
import com.github.terrakok.cicerone.Router
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
open class BaseSearchFragment : Fragment(), BackButtonListener {

    private val localCiceroneHolder by inject<LocalCiceroneHolder>()
    val searchViewModel by sharedViewModel<SearchViewModel>()
    val companyViewModel by sharedViewModel<CompanyViewModel>()

    private val cicerone = localCiceroneHolder.getCicerone(Containers.MAIN_FRAGMENT_CONTAINER.name)

    private val router: Router
        get() = cicerone.router

    override fun onBackPressed(): Boolean {
        router.exit()
        return true
    }
}