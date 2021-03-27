package com.farionik.yandextestapp.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.workDataOf
import com.farionik.yandextestapp.repository.SearchRepository
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.stock.StockModelRelation
import com.farionik.yandextestapp.repository.work_manager.SearchWorkManager
import com.farionik.yandextestapp.ui.fragment.search.ISearchModel
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class SearchViewModel(
    context: Context,
    appDatabase: AppDatabase,
    private val searchRepository: SearchRepository
) : BaseViewModel(context) {

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