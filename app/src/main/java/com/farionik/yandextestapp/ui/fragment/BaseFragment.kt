package com.farionik.yandextestapp.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.view_model.CompanyViewModel
import com.farionik.yandextestapp.ui.activity.MainActivityListener
import com.farionik.yandextestapp.view_model.CompanyDetailViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

abstract class BaseFragment : Fragment() {

    val companyViewModel by sharedViewModel<CompanyViewModel>()
    val companyDetailViewModel by sharedViewModel<CompanyDetailViewModel>()

    lateinit var layoutManager: LinearLayoutManager

    protected var mainActivityListener: MainActivityListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_company, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivityListener) {
            mainActivityListener = context
        }
    }
}