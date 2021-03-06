package com.farionik.yandextestapp.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.farionik.yandextestapp.repository.CompanyRepository
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class CompanyDetailViewModel(
    private val companyRepository: CompanyRepository,
    private val appDatabase: AppDatabase
) : CompanyViewModel(companyRepository, appDatabase) {

    // выбранная компания на просмотр
    private val _companyDetailSymbolLiveData = MutableLiveData<String>()
    val companyDetailModelLiveData: LiveData<CompanyEntity>
        get() = Transformations.switchMap(_companyDetailSymbolLiveData) {
            if (it.isNullOrEmpty()) {
                return@switchMap null
            }
            return@switchMap appDatabase.companyDAO().companyEntityLiveData(it)
        }

    fun setCompanyDetail(symbol: String) {
        _companyDetailSymbolLiveData.value = symbol
        viewModelScope.launch(IO) {
            companyRepository.loadCompanyCharts(symbol)
        }
    }
}