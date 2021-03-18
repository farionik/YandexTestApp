package com.farionik.yandextestapp.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.farionik.yandextestapp.repository.CompanyRepository
import com.farionik.yandextestapp.repository.database.company.CompanyEntity

class SearchViewModel(private val repository: CompanyRepository) : ViewModel() {

    val popularCompanies: LiveData<List<CompanyEntity>> =
        repository.popularCompaniesFlow().asLiveData(viewModelScope.coroutineContext)

    val userCompanies: LiveData<List<CompanyEntity>> =
        repository.userCompaniesFlow().asLiveData(viewModelScope.coroutineContext)
}