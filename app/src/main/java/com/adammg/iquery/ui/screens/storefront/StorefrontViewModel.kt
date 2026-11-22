package com.adammg.iquery.ui.screens.storefront

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adammg.iquery.data.model.Country
import com.adammg.iquery.data.repository.StorefrontPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StorefrontUiState(
    val searchQuery: String = "",
    val countries: List<Country> = Country.ALL_COUNTRIES,
    val activeCountry: Country = Country.UNITED_STATES
)

class StorefrontViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val storefrontPreferences = StorefrontPreferences.getInstance(application)

    private val _uiState = MutableStateFlow(StorefrontUiState())
    val uiState: StateFlow<StorefrontUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            storefrontPreferences.selectedCountry.collectLatest { active ->
                _uiState.update { it.copy(activeCountry = active) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            val filtered = if (query.isBlank()) {
                Country.ALL_COUNTRIES
            } else {
                Country.ALL_COUNTRIES.filter { country ->
                    country.name.contains(query, ignoreCase = true) ||
                    country.nativeName.contains(query, ignoreCase = true) ||
                    country.code.contains(query, ignoreCase = true)
                }
            }
            state.copy(searchQuery = query, countries = filtered)
        }
    }

    fun selectCountry(country: Country) {
        storefrontPreferences.setCountry(country)
    }
}
