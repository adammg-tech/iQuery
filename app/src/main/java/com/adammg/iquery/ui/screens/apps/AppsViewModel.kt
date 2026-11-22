package com.adammg.iquery.ui.screens.apps

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adammg.iquery.data.model.AppGenre
import com.adammg.iquery.data.model.AppItem
import com.adammg.iquery.data.model.Country
import com.adammg.iquery.data.repository.AppRepository
import com.adammg.iquery.data.repository.StorefrontPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppsUiState(
    val isLoading: Boolean = true,
    val selectedCategory: String = "All Apps",
    val topFreeApps: List<AppItem> = emptyList(),
    val topPaidApps: List<AppItem> = emptyList(),
    val newApps: List<AppItem> = emptyList(),
    val activeCountry: Country = Country.UNITED_STATES
)

class AppsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = AppRepository()
    private val storefrontPreferences = StorefrontPreferences.getInstance(application)

    private val _uiState = MutableStateFlow(AppsUiState())
    val uiState: StateFlow<AppsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            storefrontPreferences.selectedCountry.collectLatest { country ->
                _uiState.update { it.copy(activeCountry = country) }
                loadAppsData(country.code)
            }
        }
    }

    fun selectCategory(category: String) {
        if (_uiState.value.selectedCategory.equals(category, ignoreCase = true)) return
        _uiState.update {
            it.copy(
                selectedCategory = category,
                isLoading = true,
                topFreeApps = emptyList(),
                topPaidApps = emptyList(),
                newApps = emptyList()
            )
        }
        loadAppsData(_uiState.value.activeCountry.code)
    }

    fun refresh() {
        _uiState.update {
            it.copy(
                isLoading = true,
                topFreeApps = emptyList(),
                topPaidApps = emptyList(),
                newApps = emptyList()
            )
        }
        loadAppsData(_uiState.value.activeCountry.code)
    }

    private fun loadAppsData(countryCode: String) {
        val category = _uiState.value.selectedCategory
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val genreId = AppGenre.findGenreIdByName(category, isGamesOnly = false)
            val freeResult = if (genreId == null) {
                repository.getTopFreeApps(countryCode, isGamesOnly = false)
            } else {
                repository.getTopFreeByGenre(countryCode, genreId, isGamesOnly = false)
            }
            val paidResult = if (genreId == null) {
                repository.getTopPaidApps(countryCode, isGamesOnly = false)
            } else {
                repository.getTopPaidByGenre(countryCode, genreId, isGamesOnly = false)
            }
            val newResult = repository.getNewApps(countryCode, isGamesOnly = false)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    topFreeApps = freeResult.getOrNull() ?: emptyList<AppItem>(),
                    topPaidApps = paidResult.getOrNull() ?: emptyList<AppItem>(),
                    newApps = newResult.getOrNull() ?: emptyList<AppItem>()
                )
            }
        }
    }
}
