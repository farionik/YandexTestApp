package com.farionik.yandextestapp.view_model

import android.content.Context
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.farionik.yandextestapp.repository.SearchRepository
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.stock.StockModelRelation
import com.farionik.yandextestapp.repository.work_manager.SearchWorkManager
import com.farionik.yandextestapp.ui.fragment.search.ISearchModel
import kotlinx.coroutines.flow.Flow

class SearchViewModel(
    context: Context,
    appDatabase: AppDatabase,
    private val searchRepository: SearchRepository
) : ViewModel(), LifecycleObserver {

    private val workManager = WorkManager.getInstance(context)
    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val popularSearch: Flow<List<ISearchModel>> = appDatabase.stockDAO().stocksFlow()
    val userSearch: Flow<List<ISearchModel>> = appDatabase.userSearchDAO().userSearchFlow()
    val searchResult: Flow<List<StockModelRelation>> =
        appDatabase.stockDAO().stocksRelationFlow(true)

    private val _searchLoadingState = MutableLiveData<WorkInfo>()
    val searchLoadingState: LiveData<WorkInfo>
        get() = _searchLoadingState

    val searchAction = MutableLiveData<ISearchModel>()

    fun searchCompanies(searchRequest: String) {

        val data = workDataOf(
            "request" to searchRequest,
        )

        val searchWorkRequest = OneTimeWorkRequestBuilder<SearchWorkManager>()
            .setConstraints(constraints)
            .setInputData(data)
            .build()
        workManager
            .enqueueUniqueWork("search_worker", ExistingWorkPolicy.REPLACE, searchWorkRequest)

        workManager.getWorkInfoByIdLiveData(searchWorkRequest.id)
            .observeForever { _searchLoadingState.postValue(it) }
    }

    fun likeStock(symbol: String) {
        searchRepository.likeStock(symbol)
    }
}