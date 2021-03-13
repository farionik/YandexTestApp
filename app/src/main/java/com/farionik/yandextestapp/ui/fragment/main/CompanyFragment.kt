package com.farionik.yandextestapp.ui.fragment.main

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.network.NetworkStatus
import com.farionik.yandextestapp.ui.fragment.BaseListFragment
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import timber.log.Timber

class CompanyFragment : BaseListFragment() {
    override val dataSource: LiveData<List<CompanyEntity>>
        get() = companyViewModel.companiesLiveData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val swipe = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
            companyViewModel.fetchCompanies()
        }

        companyViewModel.loadingCompaniesStateLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is NetworkStatus.SUCCESS -> hideProgress()
                is NetworkStatus.LOADING -> showProgress(it.message)
                is NetworkStatus.ERROR -> showError(
                    it.e.message ?: "Error when load companies. Try later"
                )
            }
        })
    }
}