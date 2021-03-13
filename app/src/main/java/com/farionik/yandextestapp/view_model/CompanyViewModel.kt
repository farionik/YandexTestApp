package com.farionik.yandextestapp.view_model

import androidx.lifecycle.*
import com.farionik.yandextestapp.repository.CompanyRepository
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.network.NetworkStatus
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


    private val _loadingCompaniesStateLiveData = MutableLiveData<NetworkStatus>()
    val loadingCompaniesStateLiveData: LiveData<NetworkStatus>
        get() = _loadingCompaniesStateLiveData

    init {
        fetchCompanies()
    }

    // получить список компаний по рейтенгу api
    private fun fetchCompanies() {
        _loadingCompaniesStateLiveData.value = NetworkStatus.LOADING("Loading companies...")
        viewModelScope.launch(IO) {
            val status = try {
                companyRepository.fetchCompanies()
            } catch (e: Exception) {
                NetworkStatus.ERROR(Throwable(e.message))
            }
            _loadingCompaniesStateLiveData.postValue(status)
            companyRepository.loadCompaniesLogo()
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