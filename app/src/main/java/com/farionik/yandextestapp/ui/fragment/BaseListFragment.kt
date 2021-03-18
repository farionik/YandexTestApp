package com.farionik.yandextestapp.ui.fragment

import android.content.Context
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.ui.activity.MainActivityListener
import com.farionik.yandextestapp.ui.fragment.main.CompanyAdapter
import com.farionik.yandextestapp.ui.fragment.main.CompanyFragment
import com.farionik.yandextestapp.ui.fragment.list_item_decorator.CompanySpaceItemDecoration
import com.farionik.yandextestapp.view_model.CompanyViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

abstract class BaseListFragment : Fragment(R.layout.fragment_company) {

    abstract val dataSource: LiveData<List<CompanyEntity>>

    val companyViewModel by sharedViewModel<CompanyViewModel>()

    lateinit var adapter: CompanyAdapter

    lateinit var recyclerView: RecyclerView
    lateinit var loadingView: View
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    protected var mainActivityListener: MainActivityListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(companyViewModel)

        recyclerView = view.findViewById(R.id.recyclerView)
        loadingView = view.findViewById(R.id.loadingView)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            if (this is CompanyFragment) {
                companyViewModel.fetchCompanies()
            }
        }

        createAdapter()

        dataSource.observe(viewLifecycleOwner, {
            adapter.swapData(it)
            adapter.notifyDataSetChanged()
        })
    }

    private fun createAdapter() {
        adapter = CompanyAdapter(interaction = object : CompanyAdapter.Interaction {
            override fun likeCompany(companyEntity: CompanyEntity, position: Int) {
                companyViewModel.likeCompany(companyEntity.symbol)
                if (this@BaseListFragment is CompanyFragment) {
                    adapter.notifyItemChanged(position, companyEntity)
                }
            }

            override fun openCompanyDetail(companyEntity: CompanyEntity) {
                mainActivityListener?.openDetailScreen(companyEntity.symbol)
            }
        })
        val layoutManager = LinearLayoutManager(context)
        recyclerView.hasFixedSize()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        val itemAnimator = recyclerView.itemAnimator
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