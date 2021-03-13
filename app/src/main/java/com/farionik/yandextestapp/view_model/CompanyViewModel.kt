package com.farionik.yandextestapp.view_model

import androidx.lifecycle.*
import com.farionik.yandextestapp.repository.CompanyRepository
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

open class CompanyViewModel(
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

    // поисковый результат
    private val _searchedCompaniesLiveData = MutableLiveData<List<CompanyEntity>>()
    val searchedCompaniesLiveData: LiveData<List<CompanyEntity>>
        get() = _searchedCompaniesLiveData


    init {
        viewModelScope.launch {
            // showLoading
            companyRepository.fetchCompanies()
            // hideLoading
        }

        // запуск sse соединения
        // много кредитов использует
        /*val webServicesProvider = WebServicesProvider()
        viewModelScope.launch(IO) {
            try {
                webServicesProvider.startSocket().consumeEach {
                    Log.i("TAG", ": ${it.toString()}")
                }
            } catch (ex: Exception) {

            }
        }*/
    }

    fun fetchCompany(symbol: String) {
        viewModelScope.launch {
            companyRepository.updateCompany(symbol)
        }
    }

    fun likeCompany(symbol: String) {
        viewModelScope.launch(IO) {
            companyRepository.likeCompany(symbol)
        }
    }

    fun searchCompanies(searchRequest: String) {
        viewModelScope.launch(IO) {
            companyRepository.searchCompanies(searchRequest)
        }
    }
}