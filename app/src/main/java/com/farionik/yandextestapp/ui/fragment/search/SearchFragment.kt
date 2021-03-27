package com.farionik.yandextestapp.ui.fragment.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.farionik.yandextestapp.databinding.FragmentSearchBinding
import com.farionik.yandextestapp.ui.adapter.list_item_decorator.SearchSpaceItemDecoration
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class SearchFragment : BaseSearchFragment() {

    private lateinit var binding: FragmentSearchBinding

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

        viewLifecycleOwner.lifecycleScope.launch {
            searchViewModel.popularSearch.collect {
                popularAdapter.swapData(it)
            }
        }
        val userAdapter = createAdapter(binding.userRequestRecyclerView)
        viewLifecycleOwner.lifecycleScope.launch {
            searchViewModel.userSearch.collect {
                userAdapter.swapData(it)
            }
        }
    }

    private fun createAdapter(recyclerView: RecyclerView): SearchAdapter {
        val adapter = SearchAdapter(object : SearchAdapter.Interaction {
            override fun onModelClicked(model: ISearchModel) {
                searchViewModel.searchAction.postValue(model)
            }
        })

        with(recyclerView) {
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.HORIZONTAL)
            addItemDecoration(SearchSpaceItemDecoration())
            setAdapter(adapter)
        }
        return adapter
    }
}