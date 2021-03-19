package com.farionik.yandextestapp.view_model

import androidx.lifecycle.*
import com.farionik.yandextestapp.repository.SearchRepository
import com.farionik.yandextestapp.repository.StockRepository
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class SearchViewModel(
    appDatabase: AppDatabase,
    private val searchRepository: SearchRepository
) : ViewModel() {

    val popularCompanies: LiveData<List<StockModelRelation>> =
        appDatabase.stockDAO().stocksFlow().take(10).asLiveData(viewModelScope.coroutineContext)

    val userCompanies: LiveData<List<StockModelRelation>> =
        appDatabase.stockDAO().stocksFlow().take(10).asLiveData(viewModelScope.coroutineContext)

    // поисковый результат
    private val _searchedStocksLiveData = MutableLiveData<List<StockModelRelation>>()
    val searchedStocksLiveData: LiveData<List<StockModelRelation>>
        get() = _searchedStocksLiveData

    fun searchCompanies(searchRequest: String) {
        viewModelScope.launch(Dispatchers.IO) {
            searchRepository.searchCompanies(searchRequest)
        }
    }
}