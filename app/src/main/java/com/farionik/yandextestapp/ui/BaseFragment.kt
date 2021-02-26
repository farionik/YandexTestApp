package com.farionik.yandextestapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.data.CompanyEntity
import com.farionik.yandextestapp.ui.main.CompanyAdapter
import com.farionik.yandextestapp.ui.main.CompanyFragment
import com.farionik.yandextestapp.ui.main.FavouriteFragment
import com.farionik.yandextestapp.ui.main.SpaceItemDecoration
import org.koin.android.viewmodel.ext.android.sharedViewModel

abstract class BaseFragment : Fragment() {

    val mainViewModel by sharedViewModel<MainViewModel>()

    lateinit var adapter: CompanyAdapter
    lateinit var layoutManager: LinearLayoutManager

    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_stock, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        createAdapter()
    }

    private fun createAdapter() {
        adapter = CompanyAdapter(interaction = object : CompanyAdapter.Interaction {
            override fun likeCompany(companyEntity: CompanyEntity, position: Int) {
                mainViewModel.likeCompany(companyEntity)
                if (this@BaseFragment is CompanyFragment) {
                    adapter.notifyItemChanged(position, companyEntity)
                }
            }
        })
        layoutManager = LinearLayoutManager(context)
        recyclerView.hasFixedSize()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SpaceItemDecoration())
    }
}