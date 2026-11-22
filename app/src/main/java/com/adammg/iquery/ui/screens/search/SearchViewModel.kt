package com.adammg.iquery.ui.screens.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adammg.iquery.data.model.AppItem
import com.adammg.iquery.data.model.Country
import com.adammg.iquery.data.repository.AppRepository
import com.adammg.iquery.data.repository.StorefrontPreferences
import com.adammg.iquery.util.DeviceClassifier
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<AppItem> = emptyList(),
    val activeCountry: Country = Country.UNITED_STATES,
    val errorMessage: String? = null,
    val isIdle: Boolean = true,
    val recentSearches: List<String> = listOf("ChatGPT", "Instagram", "Minecraft", "WhatsApp", "Procreate", "Spotify")
)

@OptIn(FlowPreview::class)
class SearchViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = AppRepository()
    private val storefrontPreferences = StorefrontPreferences.getInstance(application)

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _queryFlow = MutableStateFlow("")

    init {
        // Observe country changes and fetch actual live mixed trending apps & games
        viewModelScope.launch {
            storefrontPreferences.selectedCountry.collectLatest { country ->
                _uiState.update { it.copy(activeCountry = country) }
                fetchLiveTrendingSearches(country.code)
                if (_queryFlow.value.isNotBlank()) {
                    performSearch(_queryFlow.value, country.code)
                }
            }
        }

        // 400ms Debouncing search query stream
        viewModelScope.launch {
            _queryFlow
                .debounce(400L)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                results = emptyList(),
                                isIdle = true,
                                errorMessage = null
                            )
                        }
                    } else {
                        performSearch(query, _uiState.value.activeCountry.code)
                    }
                }
        }
    }

    private fun fetchLiveTrendingSearches(countryCode: String) {
        viewModelScope.launch {
            val appsResult = repository.getTopFreeApps(countryCode, isGamesOnly = false)
            val gamesResult = repository.getTopFreeApps(countryCode, isGamesOnly = true)

            val mixedApps = (appsResult.getOrDefault(emptyList()) + gamesResult.getOrDefault(emptyList()))
                .filter { DeviceClassifier.supportsIPhone(it.supportedDevices) }

            val cleanNames = mixedApps
                .map { name ->
                    name.trackName
                        .replace(Regex("(?i)^top\\s+(free\\s+)?"), "")
                        .trim()
                }
                .filter { it.isNotBlank() }
                .distinct()
                .take(8)

            if (cleanNames.isNotEmpty()) {
                _uiState.update { it.copy(recentSearches = cleanNames) }
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        _queryFlow.value = newQuery.trim()
    }

    fun clearQuery() {
        _uiState.update { it.copy(query = "", results = emptyList(), isIdle = true, errorMessage = null) }
        _queryFlow.value = ""
    }

    fun retry() {
        if (_queryFlow.value.isNotBlank()) {
            performSearch(_queryFlow.value, _uiState.value.activeCountry.code)
        }
    }

    private fun performSearch(query: String, countryCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isIdle = false, errorMessage = null) }
            val result = repository.searchApps(query, countryCode)
            result.onSuccess { apps ->
                val iPhoneApps = apps.filter { DeviceClassifier.supportsIPhone(it.supportedDevices) }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        results = iPhoneApps,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        results = emptyList(),
                        errorMessage = error.localizedMessage ?: "Search failed. Please try again."
                    )
                }
            }
        }
    }
}
