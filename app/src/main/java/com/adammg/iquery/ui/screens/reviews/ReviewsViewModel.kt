package com.adammg.iquery.ui.screens.reviews

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adammg.iquery.data.model.ReviewEntry
import com.adammg.iquery.data.repository.AppRepository
import com.adammg.iquery.data.repository.StorefrontPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReviewsUiState(
    val isLoading: Boolean = true,
    val appId: Long = 0L,
    val appName: String = "",
    val reviews: List<ReviewEntry> = emptyList(),
    val currentPage: Int = 1,
    val isPageLoading: Boolean = false,
    val errorMessage: String? = null
)

class ReviewsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = AppRepository()
    private val storefrontPreferences = StorefrontPreferences.getInstance(application)

    private val _uiState = MutableStateFlow(ReviewsUiState())
    val uiState: StateFlow<ReviewsUiState> = _uiState.asStateFlow()

    fun loadReviews(appId: Long, rawAppName: String) {
        val decodedName = java.net.URLDecoder.decode(rawAppName, "UTF-8")
        _uiState.update { it.copy(isLoading = true, appId = appId, appName = decodedName, errorMessage = null) }

        val countryCode = storefrontPreferences.getCountry().code

        viewModelScope.launch {
            val result = repository.getCustomerReviews(appId, countryCode, page = 1)
            result.onSuccess { list ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        reviews = list,
                        currentPage = 1,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        reviews = emptyList(),
                        errorMessage = error.localizedMessage ?: "Failed to load customer reviews"
                    )
                }
            }
        }
    }

    fun loadMoreReviews() {
        if (_uiState.value.isPageLoading) return
        val nextPage = _uiState.value.currentPage + 1
        _uiState.update { it.copy(isPageLoading = true) }

        val countryCode = storefrontPreferences.getCountry().code

        viewModelScope.launch {
            val result = repository.getCustomerReviews(_uiState.value.appId, countryCode, page = nextPage)
            result.onSuccess { newReviews ->
                _uiState.update { state ->
                    val combined = (state.reviews + newReviews).distinctBy { it.reviewContent }
                    state.copy(
                        isPageLoading = false,
                        reviews = combined,
                        currentPage = nextPage
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isPageLoading = false) }
            }
        }
    }
}
