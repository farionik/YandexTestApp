package com.farionik.yandextestapp.ui.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.farionik.yandextestapp.databinding.FragmentSearchBinding
import com.farionik.yandextestapp.model.SearchModel
import com.farionik.yandextestapp.ui.SearchedClickedListener


class SearchFragment : Fragment() {

    val defaultList = listOf(
        "Apple",
        "Amazon",
        "Google",
        "Tesla",
        "Microsoft",
        "First Solar",
        "Alibaba",
        "Facebook",
        "MasterCard",
        "Nvidia",
        "Nokia",
        "Yandex",
        "GM",
        "Baidu",
        "Intel",
        "AMD",
        "Visa",
        "Bank of America"
    )

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

        createPopularAdapter()
        createUserAdapter()
    }

    private fun createPopularAdapter() {
        val adapter = SearchAdapter(object : SearchAdapter.Interaction {
            override fun onModelClicked(model: SearchModel) {
                searchedClickedListener?.searchModelClicked(model)
            }
        })
        val data: List<SearchModel> = defaultList.map { SearchModel(it) }
        adapter.swapData(data)
        with(binding.popularRecyclerView) {
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.HORIZONTAL)
            addItemDecoration(SearchSpaceItemDecoration())
            setAdapter(adapter)
        }
    }

    private fun createUserAdapter() {
        val adapter = SearchAdapter(object : SearchAdapter.Interaction {
            override fun onModelClicked(model: SearchModel) {
                searchedClickedListener?.searchModelClicked(model)
            }
        })
        val data: List<SearchModel> = defaultList.map { SearchModel(it) }
        adapter.swapData(data)
        with(binding.userRequestRecyclerView) {
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.HORIZONTAL)
            addItemDecoration(SearchSpaceItemDecoration())
            setAdapter(adapter)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SearchedClickedListener) {
            searchedClickedListener = context
        }
    }

}