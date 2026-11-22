package com.adammg.iquery.ui.screens.developer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adammg.iquery.data.model.AppItem
import com.adammg.iquery.data.repository.AppRepository
import com.adammg.iquery.data.repository.StorefrontPreferences
import com.adammg.iquery.util.DeviceClassifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DeveloperAppsUiState(
    val isLoading: Boolean = true,
    val developerName: String = "",
    val apps: List<AppItem> = emptyList(),
    val errorMessage: String? = null
)

class DeveloperAppsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = AppRepository()
    private val storefrontPreferences = StorefrontPreferences.getInstance(application)

    private val _uiState = MutableStateFlow(DeveloperAppsUiState())
    val uiState: StateFlow<DeveloperAppsUiState> = _uiState.asStateFlow()

    fun loadDeveloperApps(artistId: Long, developerName: String) {
        val decodedName = java.net.URLDecoder.decode(developerName, "UTF-8")
        _uiState.update { it.copy(isLoading = true, developerName = decodedName, errorMessage = null) }

        val countryCode = storefrontPreferences.getCountry().code

        viewModelScope.launch {
            val result = repository.getAppsByArtist(artistId, decodedName, countryCode)
            result.onSuccess { list ->
                val iPhoneApps = list.filter { DeviceClassifier.supportsIPhone(it.supportedDevices) }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        apps = iPhoneApps,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        apps = emptyList(),
                        errorMessage = error.localizedMessage ?: "Failed to load developer apps"
                    )
                }
            }
        }
    }
}
