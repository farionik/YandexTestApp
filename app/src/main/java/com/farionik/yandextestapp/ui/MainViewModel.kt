package com.farionik.yandextestapp.ui

import androidx.lifecycle.*
import com.farionik.yandextestapp.data.CompanyEntity
import com.farionik.yandextestapp.repository.CompanyRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class MainViewModel(
    private val companyRepository: CompanyRepository
) : ViewModel() {

    var appBarOffsetMutableLiveData: MutableLiveData<Int> = MutableLiveData()

    private val _companiesLiveData: LiveData<List<CompanyEntity>> =
        companyRepository.companiesFlow().asLiveData(viewModelScope.coroutineContext)
    val companiesLiveData: LiveData<List<CompanyEntity>>
        get() = _companiesLiveData

    private val _favouriteCompaniesLiveData: LiveData<List<CompanyEntity>> =
        companyRepository.favouriteCompaniesFlow().asLiveData(viewModelScope.coroutineContext)
    val favouriteCompaniesLiveData: LiveData<List<CompanyEntity>>
        get() = _favouriteCompaniesLiveData

    init {
        viewModelScope.launch {
            // showLoading
            companyRepository.fetchCompanies()
            // hideLoading
        }
    }

    fun likeCompany(companyEntity: CompanyEntity) {
        viewModelScope.launch(IO) {
            companyRepository.likeCompany(companyEntity)
        }
    }
}