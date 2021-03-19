package com.farionik.yandextestapp.ui.fragment.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.farionik.yandextestapp.databinding.FragmentSearchBinding
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.ui.fragment.list_item_decorator.SearchSpaceItemDecoration

class SearchFragment : BaseSearchFragment() {

    private lateinit var binding: FragmentSearchBinding
    private var searchedClickedListener: SearchedClickedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val popularAdapter = createAdapter(binding.popularRecyclerView)
        searchViewModel.popularCompanies.observe(
            viewLifecycleOwner, { popularAdapter.swapData(it) }
        )

        val userAdapter = createAdapter(binding.userRequestRecyclerView)
        searchViewModel.userCompanies.observe(
            viewLifecycleOwner, { userAdapter.swapData(it) }
        )
    }

    private fun createAdapter(recyclerView: RecyclerView): SearchAdapter {
        val adapter = SearchAdapter(object : SearchAdapter.Interaction {
            override fun onModelClicked(model: StockModelRelation) {
                //searchedClickedListener?.searchModelClicked(model)
                // через ViewModel выполнить навигацию в результат fragment
            }
        })

        with(recyclerView) {
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.HORIZONTAL)
            addItemDecoration(SearchSpaceItemDecoration())
            setAdapter(adapter)
        }
        return adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SearchedClickedListener) {
            searchedClickedListener = context
        }
    }

}