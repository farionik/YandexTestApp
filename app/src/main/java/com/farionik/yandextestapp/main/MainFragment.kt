package com.farionik.yandextestapp.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.farionik.yandextestapp.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var adapter: SnippetAdapter
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }*/

        createAdapter()
    }

    private fun createAdapter() {
        adapter = SnippetAdapter(object : SnippetAdapter.Interaction {})
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(SpaceItemDecoration())

        val data = mutableListOf<SnippetEntity>().apply {
            for (i in 0..30) {
                add(SnippetEntity("Ticker $i", "name $i"))
            }
        }
        adapter.swapData(data)
    }
}