package com.farionik.yandextestapp.repository.pagination

import androidx.paging.*
import com.farionik.yandextestapp.repository.StockRepository
import com.farionik.yandextestapp.repository.database.company.StockModelRelation

class StockPagingSource(
    private val stockRepository: StockRepository
) : PagingSource<Int, StockModelRelation>() {

    companion object {
        const val PAGE_SIZE = 20
    }

    override fun getRefreshKey(state: PagingState<Int, StockModelRelation>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StockModelRelation> {
        return try {
            val nextPage = params.key ?: 0
            val result = stockRepository.loadStockPage(nextPage)
            LoadResult.Page(result, null, (nextPage + 1))
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}