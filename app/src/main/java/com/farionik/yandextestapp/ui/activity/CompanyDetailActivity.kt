package com.farionik.yandextestapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.view_model.CompanyViewModel
import org.koin.android.viewmodel.ext.android.viewModel

const val DETAIL_COMPANY_SYMBOL = "DETAIL_COMPANY_SYMBOL"

class CompanyDetailActivity : AppCompatActivity() {

    private val companyViewModel by viewModel<CompanyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_detail)

        val companyID = intent.extras?.getString(DETAIL_COMPANY_SYMBOL)
        if (companyID == null) {
            finish()
        } else {
            companyViewModel.setCompanyDetail(companyID)
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar).apply {
            navigationIcon = ContextCompat.getDrawable(context, R.drawable.ic_back)
        }
        setSupportActionBar(toolbar)

        companyViewModel.companyDetailModelLiveData.observe(this, {
            with(toolbar) {
                findViewById<TextView>(R.id.tvSymbol).text = it.symbol
                findViewById<TextView>(R.id.tvCompanyName).text = it.companyName
            }
        })
    }

    override fun onNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_company_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.action_favorite -> {
            Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}