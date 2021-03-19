package com.farionik.yandextestapp.ui.fragment.main

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.ui.activity.MainActivityListener
import com.farionik.yandextestapp.ui.fragment.list_item_decorator.CompanySpaceItemDecoration
import com.farionik.yandextestapp.ui.fragment.main.StockAdapter
import com.farionik.yandextestapp.ui.fragment.main.CompanyFragment
import com.farionik.yandextestapp.view_model.StockViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

abstract class BaseListFragment : Fragment(R.layout.fragment_company) {

    abstract val dataSource: LiveData<List<StockModelRelation>>

    val stockViewModel by sharedViewModel<StockViewModel>()

    lateinit var adapter: StockAdapter

    lateinit var recyclerView: RecyclerView
    lateinit var loadingView: View
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    protected var mainActivityListener: MainActivityListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.addObserver(stockViewModel)

        recyclerView = view.findViewById(R.id.recyclerView)
        loadingView = view.findViewById(R.id.loadingView)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            if (this is CompanyFragment) {
                stockViewModel.fetchCompanies()
            }
        }

        createAdapter()

        dataSource.observe(viewLifecycleOwner, {
            adapter.swapData(it)
            adapter.notifyDataSetChanged()
        })
    }

    private fun createAdapter() {
        adapter = StockAdapter(interaction = object : StockAdapter.Interaction {
            override fun likeCompany(stockModelRelation: StockModelRelation, position: Int) {
                stockViewModel.likeStock(stockModelRelation.stock.symbol)
                if (this@BaseListFragment is CompanyFragment) {
                    adapter.notifyItemChanged(position, stockModelRelation)
                }
            }

            override fun openCompanyDetail(stockModelRelation: StockModelRelation) {


                //mainActivityListener?.openDetailScreen(companyEntity.symbol)


            }
        })
        val layoutManager = LinearLayoutManager(context)
        recyclerView.hasFixedSize()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(CompanySpaceItemDecoration())
    }

    protected fun showProgress(message: String) {
        loadingView.visibility = View.VISIBLE
        loadingView.findViewById<TextView>(R.id.progressMessage).text = message
    }

    protected fun hideProgress() {
        loadingView.visibility = View.GONE
    }

    protected fun showError(message: String) {
        hideProgress()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivityListener) {
            mainActivityListener = context
        }
    }
}