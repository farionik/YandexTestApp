package com.farionik.yandextestapp.ui

import androidx.lifecycle.*
import com.farionik.yandextestapp.data.CompanyEntity
import com.farionik.yandextestapp.repository.CompanyRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class MainViewModel(
    private val companyRepository: CompanyRepository
) : ViewModel() {

    // величина скрола toolBar
    var appBarOffsetMutableLiveData: MutableLiveData<Int> = MutableLiveData()

    // все компании
    private val _companiesLiveData: LiveData<List<CompanyEntity>> =
        companyRepository.companiesFlow().asLiveData(viewModelScope.coroutineContext)
    val companiesLiveData: LiveData<List<CompanyEntity>>
        get() = _companiesLiveData

    // избранные компании
    private val _favouriteCompaniesLiveData: LiveData<List<CompanyEntity>> =
        companyRepository.favouriteCompaniesFlow().asLiveData(viewModelScope.coroutineContext)
    val favouriteCompaniesLiveData: LiveData<List<CompanyEntity>>
        get() = _favouriteCompaniesLiveData

    // выбранная компания на просмотр
    private val _companyDetailModelLiveData = MutableLiveData<CompanyEntity>()
    val companyDetailModelLiveData: LiveData<CompanyEntity>
        get() = _companyDetailModelLiveData


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

    fun setCompanyDetail(companyEntity: CompanyEntity) {
        _companyDetailModelLiveData.value = companyEntity
    }
}