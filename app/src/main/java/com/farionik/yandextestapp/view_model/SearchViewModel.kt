package com.farionik.yandextestapp.view_model

import androidx.lifecycle.*
import androidx.room.withTransaction
import com.farionik.yandextestapp.repository.SearchRepository
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.repository.database.search.UserSearchEntity
import com.farionik.yandextestapp.repository.network.NetworkState
import com.farionik.yandextestapp.ui.fragment.search.ISearchModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class SearchViewModel(
    private val appDatabase: AppDatabase,
    private val searchRepository: SearchRepository
) : ViewModel(), LifecycleObserver {

    val popularSearch: Flow<List<ISearchModel>> = appDatabase.stockDAO().stocksFlow()
    val userSearch: Flow<List<ISearchModel>> = appDatabase.userSearchDAO().userSearchFlow()
    val searchResult: Flow<List<StockModelRelation>> =
        appDatabase.stockDAO().stocksRelationFlow(true)

    private val _loadingStocksStateLiveData = MutableLiveData<NetworkState>()
    val loadingStocksStateLiveData: LiveData<NetworkState>
        get() = _loadingStocksStateLiveData

    val searchAction = MutableLiveData<ISearchModel>()

    private var loadingJob: Job? = null

    fun searchCompanies(searchRequest: String) {
        /*_loadingStocksStateLiveData.postValue(NetworkState.LOADING("Loading companies..."))
        loadingJob = viewModelScope.launch(Dispatchers.IO) {
            appDatabase.withTransaction {
                appDatabase.stockDAO().updateUserSearch()
                val previousSearch = appDatabase.userSearchDAO().checkUserSearch(searchRequest)
                if (previousSearch == null) {
                    appDatabase.userSearchDAO().insert(UserSearchEntity(title = searchRequest))
                }
            }

            val state = try {
                searchRepository.searchCompanies(searchRequest)
            } catch (e: Exception) {
                if (e is CancellationException) NetworkState.SUCCESS
                else NetworkState.ERROR(Throwable(e.message))
            }
            _loadingStocksStateLiveData.postValue(state)
        }*/
    }

    fun likeStock(symbol: String) {
        searchRepository.likeStock(symbol)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cancelAllJob() {
        loadingJob?.cancel()
    }
}