package com.adammg.iquery.ui.screens.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adammg.iquery.data.model.AppItem
import com.adammg.iquery.data.model.Country
import com.adammg.iquery.data.model.ReviewEntry
import com.adammg.iquery.data.repository.AppRepository
import com.adammg.iquery.data.repository.StorefrontPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppDetailsUiState(
    val isLoading: Boolean = true,
    val appItem: AppItem? = null,
    val reviews: List<ReviewEntry> = emptyList(),
    val activeCountry: Country = Country.UNITED_STATES,
    val errorMessage: String? = null,
    val isDescriptionExpanded: Boolean = false
)

class AppDetailsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = AppRepository()
    private val storefrontPreferences = StorefrontPreferences.getInstance(application)

    private val _uiState = MutableStateFlow(AppDetailsUiState())
    val uiState: StateFlow<AppDetailsUiState> = _uiState.asStateFlow()

    fun loadAppDetails(trackId: Long) {
        val currentCountry = storefrontPreferences.getCountry()
        _uiState.update { it.copy(isLoading = true, activeCountry = currentCountry, errorMessage = null) }

        viewModelScope.launch {
            // Launch app lookup and reviews fetch concurrently or sequentially
            val appResult = repository.lookupApp(trackId, currentCountry.code)
            val reviewsResult = repository.getCustomerReviews(trackId, currentCountry.code)

            appResult.onSuccess { item ->
                if (item != null) {
                    val reviews = reviewsResult.getOrDefault(emptyList())
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            appItem = item,
                            reviews = reviews,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "App details not found.") }
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Failed to load app details"
                    )
                }
            }
        }
    }

    fun toggleDescriptionExpanded() {
        _uiState.update { it.copy(isDescriptionExpanded = !it.isDescriptionExpanded) }
    }
}
