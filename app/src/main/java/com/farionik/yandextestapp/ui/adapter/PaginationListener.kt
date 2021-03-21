package com.farionik.yandextestapp.ui.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


abstract class PaginationListener : RecyclerView.OnScrollListener() {

    companion object {
        const val PAGE_SIZE = 20
    }

    protected abstract fun loadMoreItems(totalCount: Int)

    abstract fun isLoading(): Boolean

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val layoutManager = recyclerView.layoutManager as LinearLayoutManager?

        if (layoutManager != null) {
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            if (!isLoading()) {
                if (visibleItemCount + firstVisibleItemPosition >= (totalItemCount - PAGE_SIZE) &&
                    firstVisibleItemPosition >= 0 &&
                    totalItemCount >= PAGE_SIZE
                ) {
                    loadMoreItems(totalItemCount)
                }
            }
        }
    }
}