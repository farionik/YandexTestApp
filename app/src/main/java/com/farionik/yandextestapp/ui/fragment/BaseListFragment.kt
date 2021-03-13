package com.farionik.yandextestapp.ui.fragment

import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.ui.fragment.main.CompanyAdapter
import com.farionik.yandextestapp.ui.fragment.main.CompanyFragment
import com.farionik.yandextestapp.ui.fragment.list_item_decorator.CompanySpaceItemDecoration

abstract class BaseListFragment : BaseFragment() {

    abstract val dataSource: LiveData<List<CompanyEntity>>

    lateinit var recyclerView: RecyclerView
    lateinit var loadingView: View
    lateinit var adapter: CompanyAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        loadingView = view.findViewById(R.id.loadingView)

        createAdapter()

        dataSource.observe(viewLifecycleOwner, { adapter.swapData(it) })
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
        layoutManager = LinearLayoutManager(context)
        recyclerView.hasFixedSize()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(CompanySpaceItemDecoration())
    }

    protected fun showProgress(message: String) {
        recyclerView.visibility = View.GONE
        loadingView.visibility = View.VISIBLE

        loadingView.findViewById<TextView>(R.id.progressMessage).text = message
    }

    protected fun hideProgress() {
        recyclerView.visibility = View.VISIBLE
        loadingView.visibility = View.GONE
    }

    protected fun showError(message: String) {
        hideProgress()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}