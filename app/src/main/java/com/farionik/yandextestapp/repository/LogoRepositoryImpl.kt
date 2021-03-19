package com.farionik.yandextestapp.repository

import android.content.Context
import com.blankj.utilcode.util.FileUtils
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.company.CompanyLogoEntity
import com.farionik.yandextestapp.repository.network.Api
import com.farionik.yandextestapp.repository.network.NetworkStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.*

class LogoRepositoryImpl(
    private val context: Context,
    private val api: Api,
    private val appDatabase: AppDatabase
) : LogoRepository {

    override suspend fun loadCompaniesLogo() {
        coroutineScope {
            appDatabase.stockDAO().stockList()?.forEach {
                launch(IO) {
                    loadCompanyLogo(it.symbol)
                }
            }
        }
    }

    private suspend fun loadCompanyLogo(symbol: String): NetworkStatus {
        val logo = appDatabase.companyLogoDAO().companyLogo(symbol)
        if (logo == null) {
            val response = api.loadCompanyLogoURL(symbol)
            if (response.isSuccessful) {
                val logoUrl = response.body()?.url ?: ""

                if (logoUrl.isNotEmpty()) {
                    try {
                        val responseBody: ResponseBody = api.loadCompanyLogoFile(logoUrl)
                        val fileName = "${symbol}_logo"
                        val filePath = context.filesDir.absolutePath + File.pathSeparator + fileName
                        val file = File(filePath)
                        copyStreamToFile(responseBody.byteStream(), file)
                        val companyLogoEntity = CompanyLogoEntity(symbol, logoUrl, filePath)
                        appDatabase.companyLogoDAO().insert(companyLogoEntity)
                    } catch (e: Exception) {
                        Timber.d("")
                    }
                }
            }
        }
        return NetworkStatus.SUCCESS
    }

    private fun copyStreamToFile(inputStream: InputStream, outputFile: File) {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024)
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }
}