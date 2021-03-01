package com.farionik.yandextestapp.view_model

import androidx.lifecycle.*
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.CompanyRepository
import com.farionik.yandextestapp.repository.database.AppDatabase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class CompanyViewModel(
    private val companyRepository: CompanyRepository,
    private val appDatabase: AppDatabase
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
    private val _companyDetailSymbolLiveData = MutableLiveData<String>()
    val companyDetailModelLiveData: LiveData<CompanyEntity>
        get() = Transformations.switchMap(_companyDetailSymbolLiveData) {
            if (it.isNullOrEmpty()) {
                return@switchMap null
            }
            return@switchMap appDatabase.companyDAO().companyEntityLiveData(it)
        }


    init {
        viewModelScope.launch {
            // showLoading
            companyRepository.fetchCompanies()
            // hideLoading
        }
    }

    fun likeCompany(symbol: String) {
        viewModelScope.launch(IO) {
            val companyEntity = appDatabase.companyDAO().companyEntity(symbol)
            companyEntity?.let {
                companyRepository.likeCompany(companyEntity)
            }
        }
    }

    fun setCompanyDetail(symbol: String) {
        _companyDetailSymbolLiveData.value = symbol
    }
}