package com.farionik.yandextestapp.repository.database.company

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.farionik.yandextestapp.repository.database.company.CompanyConverter

@Entity(tableName = "CompanyTable")
@TypeConverters(CompanyConverter::class)
data class CompanyEntity(
    @PrimaryKey
    val symbol: String,
    var companyName: String?,
    val exchange: String?,
    val industry: String?,
    val website: String?,
    val description: String?,
    val CEO: String?,
    val securityName: String?,
    val issueType: String?,
    val sector: String?,
    val primarySicCode: Int?,
    val employees: Int?,
    val tags: List<String>?,
    val address: String?,
    val address2: String?,
    val state: String?,
    val city: String?,
    val zip: String?,
    val country: String?,
    val phone: String?,
    var logo: String?,

    var price: Double?,
    var change: Double?,
    var changePercent: Double?,

    var isFavourite: Boolean = false
)
