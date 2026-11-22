package com.adammg.iquery.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adammg.iquery.data.repository.SettingsPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isDynamicColor: Boolean = false,
    val themeMode: String = "system" // "system", "light", "dark"
)

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val settingsPreferences = SettingsPreferences.getInstance(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsPreferences.isDynamicColor.collectLatest { dynamic ->
                _uiState.update { it.copy(isDynamicColor = dynamic) }
            }
        }
        viewModelScope.launch {
            settingsPreferences.themeMode.collectLatest { mode ->
                _uiState.update { it.copy(themeMode = mode) }
            }
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        settingsPreferences.setDynamicColor(enabled)
    }

    fun setThemeMode(mode: String) {
        settingsPreferences.setThemeMode(mode)
    }
}
