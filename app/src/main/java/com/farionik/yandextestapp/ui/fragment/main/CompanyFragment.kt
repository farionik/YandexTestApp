package com.farionik.yandextestapp.ui.fragment.main

import androidx.lifecycle.LiveData
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.ui.fragment.BaseListFragment

class CompanyFragment : BaseListFragment() {
    override val dataSource: LiveData<List<CompanyEntity>>
        get() = companyViewModel.companiesLiveData
}