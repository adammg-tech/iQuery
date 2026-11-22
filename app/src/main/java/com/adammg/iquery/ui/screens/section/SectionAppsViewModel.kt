package com.adammg.iquery.ui.screens.section

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adammg.iquery.data.model.AppItem
import com.adammg.iquery.data.model.Country
import com.adammg.iquery.data.repository.AppRepository
import com.adammg.iquery.data.repository.StorefrontPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SectionAppsUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val apps: List<AppItem> = emptyList(),
    val activeCountry: Country = Country.UNITED_STATES,
    val errorMessage: String? = null
)

class SectionAppsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = AppRepository()
    private val storefrontPreferences = StorefrontPreferences.getInstance(application)

    private val _uiState = MutableStateFlow(SectionAppsUiState())
    val uiState: StateFlow<SectionAppsUiState> = _uiState.asStateFlow()

    fun loadSectionApps(sectionType: String, isGamesOnly: Boolean) {
        val country = storefrontPreferences.getCountry()
        val titleText = when (sectionType) {
            "top_free" -> if (isGamesOnly) "Top Free Games" else "Top Free Apps"
            "top_paid" -> if (isGamesOnly) "Top Paid Games" else "Top Paid Applications"
            "new_apps" -> if (isGamesOnly) "New & Updated Games" else "New & Updated Applications"
            else -> if (isGamesOnly) "Top Games" else "Top Applications"
        }

        _uiState.update { it.copy(isLoading = true, title = titleText, activeCountry = country, errorMessage = null) }

        viewModelScope.launch {
            val result = when (sectionType) {
                "top_free" -> repository.getTopFreeApps(country.code, isGamesOnly)
                "top_paid" -> repository.getTopPaidApps(country.code, isGamesOnly)
                "new_apps" -> repository.getNewApps(country.code, isGamesOnly)
                else -> repository.getTopFreeApps(country.code, isGamesOnly)
            }

            result.onSuccess { list ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        apps = list,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        apps = emptyList(),
                        errorMessage = error.localizedMessage ?: "Failed to load section apps"
                    )
                }
            }
        }
    }
}
