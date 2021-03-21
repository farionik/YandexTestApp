package com.farionik.yandextestapp.ui.fragment.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.ui.fragment.BackButtonListener
import com.farionik.yandextestapp.ui.fragment.BaseFragment
import com.farionik.yandextestapp.view_model.CompanyViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class CompanyDetailFragment : BaseFragment(), BackButtonListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_company_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onBackPressed(): Boolean {
        companyViewModel.backClick()
        return true
    }


}