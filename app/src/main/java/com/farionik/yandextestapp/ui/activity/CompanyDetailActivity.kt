package com.farionik.yandextestapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.view_model.CompanyViewModel
import org.koin.android.viewmodel.ext.android.viewModel

const val DETAIL_COMPANY_SYMBOL = "DETAIL_COMPANY_SYMBOL"

class CompanyDetailActivity : AppCompatActivity() {

    private val companyViewModel by viewModel<CompanyViewModel>()
    private lateinit var companyID: String
    private var companyEntity: CompanyEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_detail)

        companyID = intent.extras?.getString(DETAIL_COMPANY_SYMBOL) ?: ""
        if (companyID.isEmpty()) {
            finish()
            return
        } else {
            companyViewModel.setCompanyDetail(companyID)
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar).apply {
            navigationIcon = ContextCompat.getDrawable(context, R.drawable.ic_back)
        }
        setSupportActionBar(toolbar)

        companyViewModel.companyDetailModelLiveData.observe(this, {
            with(toolbar) {
                companyEntity = it
                findViewById<TextView>(R.id.tvSymbol).text = it.symbol
                findViewById<TextView>(R.id.tvCompanyName).text = it.companyName
                invalidateOptionsMenu()
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

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        companyEntity?.run {
            val findItem = menu?.findItem(R.id.action_favorite)

            findItem?.run {
                setIcon(
                    if (isFavourite) {
                        ContextCompat.getDrawable(
                            this@CompanyDetailActivity,
                            R.drawable.ic_star_gold
                        )
                    } else {
                        ContextCompat.getDrawable(
                            this@CompanyDetailActivity,
                            R.drawable.ic_star_empty
                        )
                    }
                )
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.action_favorite -> {
            companyViewModel.likeCompany(companyID)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}