package com.adammg.iquery.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.adammg.iquery.data.model.Country
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StorefrontPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _selectedCountry = MutableStateFlow(loadInitialCountry())
    val selectedCountry: StateFlow<Country> = _selectedCountry.asStateFlow()

    private fun loadInitialCountry(): Country {
        val savedCode = prefs.getString(KEY_COUNTRY_CODE, Country.UNITED_STATES.code) ?: Country.UNITED_STATES.code
        return Country.fromCode(savedCode)
    }

    fun setCountry(country: Country) {
        prefs.edit().putString(KEY_COUNTRY_CODE, country.code).apply()
        _selectedCountry.value = country
    }

    fun getCountry(): Country = _selectedCountry.value

    companion object {
        private const val PREFS_NAME = "iquery_storefront_prefs"
        private const val KEY_COUNTRY_CODE = "key_country_code"

        @Volatile
        private var INSTANCE: StorefrontPreferences? = null

        fun getInstance(context: Context): StorefrontPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: StorefrontPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
