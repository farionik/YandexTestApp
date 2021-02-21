package com.farionik.yandextestapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.ui.main.CompanyAdapter
import com.farionik.yandextestapp.ui.main.SpaceItemDecoration
import org.koin.android.viewmodel.ext.android.viewModel

abstract class BaseFragment : Fragment() {

    val mainViewModel by viewModel<MainViewModel>()


    lateinit var adapter: CompanyAdapter
    lateinit var layoutManager: LinearLayoutManager

    private lateinit var recyclerView: RecyclerView

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
        adapter = CompanyAdapter(object : CompanyAdapter.Interaction {})
        layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SpaceItemDecoration())

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                val range =
                    adapter.currentList.subList(firstVisibleItemPosition, lastVisibleItemPosition)

                Log.i(
                    "TAG",
                    "onScrolled: " +
                            "firstVisible = $firstVisibleItemPosition " +
                            "lastVisible = $lastVisibleItemPosition " +
                            "range =  ${range.size}"
                )
            }
        })
    }
}