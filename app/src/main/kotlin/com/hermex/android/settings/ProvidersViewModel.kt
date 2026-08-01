package com.hermex.android.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermex.android.auth.AuthRepository
import com.hermex.android.core.network.ApiError
import com.hermex.android.core.network.safeApiCall
import com.hermex.android.core.network.dto.ModelsLiveResponse
import com.hermex.android.core.network.dto.ModelOptionDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProvidersViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun loadProviders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val api = authRepository.apiForActiveServer() ?: throw ApiError.Network(Exception("Not signed in"))
                val response = safeApiCall { api.modelsLive() }
                val models = response.models ?: emptyList()
                val providers = models.groupBy { it.providerId ?: "unknown" }.map { (providerId, models) ->
                    ProviderStatus(
                        name = providerId,
                        isConfigured = true,
                        isHealthy = true,
                        modelCount = models.size,
                    )
                }
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: ApiError) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load providers") }
            }
        }
    }
}
