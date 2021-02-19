package com.farionik.yandextestapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.ui.main.SnippetAdapter
import com.farionik.yandextestapp.ui.main.SpaceItemDecoration

abstract class BaseFragment : Fragment() {

    lateinit var adapter: SnippetAdapter
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
        adapter = SnippetAdapter(object : SnippetAdapter.Interaction {})
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SpaceItemDecoration())
    }
}