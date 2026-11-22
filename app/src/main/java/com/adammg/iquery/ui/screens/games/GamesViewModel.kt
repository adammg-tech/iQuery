package com.adammg.iquery.ui.screens.games

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

data class GamesUiState(
    val isLoading: Boolean = true,
    val selectedCategory: String = "All Games",
    val topFreeGames: List<AppItem> = emptyList(),
    val topPaidGames: List<AppItem> = emptyList(),
    val newGames: List<AppItem> = emptyList(),
    val activeCountry: Country = Country.UNITED_STATES
)

class GamesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = AppRepository()
    private val storefrontPreferences = StorefrontPreferences.getInstance(application)

    private val _uiState = MutableStateFlow(GamesUiState())
    val uiState: StateFlow<GamesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            storefrontPreferences.selectedCountry.collectLatest { country ->
                _uiState.update { it.copy(activeCountry = country) }
                loadGamesData(country.code)
            }
        }
    }

    fun selectCategory(category: String) {
        if (_uiState.value.selectedCategory.equals(category, ignoreCase = true)) return
        _uiState.update {
            it.copy(
                selectedCategory = category,
                isLoading = true,
                topFreeGames = emptyList(),
                topPaidGames = emptyList(),
                newGames = emptyList()
            )
        }
        loadGamesData(_uiState.value.activeCountry.code)
    }

    fun refresh() {
        _uiState.update {
            it.copy(
                isLoading = true,
                topFreeGames = emptyList(),
                topPaidGames = emptyList(),
                newGames = emptyList()
            )
        }
        loadGamesData(_uiState.value.activeCountry.code)
    }

    private fun loadGamesData(countryCode: String) {
        val category = _uiState.value.selectedCategory
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val genreId = AppGenre.findGenreIdByName(category, isGamesOnly = true)
            val freeResult = repository.getTopFreeByGenre(countryCode, genreId ?: 6014L, isGamesOnly = true)
            val paidResult = repository.getTopPaidByGenre(countryCode, genreId ?: 6014L, isGamesOnly = true)
            val newResult = repository.getNewApps(countryCode, isGamesOnly = true)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    topFreeGames = freeResult.getOrNull() ?: emptyList(),
                    topPaidGames = paidResult.getOrNull() ?: emptyList(),
                    newGames = newResult.getOrNull() ?: emptyList()
                )
            }
        }
    }
}
