package com.farionik.yandextestapp.view_model

import androidx.lifecycle.*
import androidx.room.withTransaction
import com.farionik.yandextestapp.repository.SearchRepository
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.repository.database.search.UserSearchEntity
import com.farionik.yandextestapp.ui.fragment.search.ISearchModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.launch

class SearchViewModel(
    private val appDatabase: AppDatabase,
    private val searchRepository: SearchRepository
) : ViewModel() {

    val popularSearch: Flow<List<ISearchModel>> = appDatabase.stockDAO().stocksFlow()
    val userSearch: Flow<List<ISearchModel>> = appDatabase.userSearchDAO().userSearchFlow()

    // поисковый результат
    private val _searchedStocksLiveData = MutableLiveData<List<StockModelRelation>>()
    val searchedStocksLiveData: LiveData<List<StockModelRelation>>
        get() = _searchedStocksLiveData

    fun searchCompanies(searchRequest: String) {
        viewModelScope.launch(Dispatchers.IO) {
            appDatabase.withTransaction {
                appDatabase.userSearchDAO().insert(UserSearchEntity(title = searchRequest))
            }
            searchRepository.searchCompanies(searchRequest)
        }
    }
}