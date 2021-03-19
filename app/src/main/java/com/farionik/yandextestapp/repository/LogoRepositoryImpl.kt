package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.company.CompanyLogoEntity
import com.farionik.yandextestapp.repository.network.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class LogoRepositoryImpl(
    private val api: Api,
    private val appDatabase: AppDatabase
) : LogoRepository {

    override suspend fun loadCompaniesLogo() {
        coroutineScope {
            launch(Dispatchers.IO) {
                val list = appDatabase.stockDAO().stockList()
                list?.run {
                    forEach {
                        launch(Dispatchers.IO) { loadCompanyLogo(it.symbol) }
                    }
                }
            }
        }
    }

    private suspend fun loadCompanyLogo(symbol: String) {
        val logo = appDatabase.companyLogoDAO().companyLogo(symbol)
        if (logo == null) {
            val response = api.loadCompanyLogo(symbol)
            if (response.isSuccessful) {
                val logoUrl = response.body()?.url ?: ""

                if (logoUrl.isNotEmpty()) {
                    val companyLogoEntity = CompanyLogoEntity(symbol, logoUrl, null)
                    appDatabase.companyLogoDAO().insert(companyLogoEntity)
                }
            }
        }
    }
}