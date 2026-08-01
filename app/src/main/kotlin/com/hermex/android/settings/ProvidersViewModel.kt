package com.hermex.android.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermex.android.auth.AuthRepository
import com.hermex.android.core.network.ApiError
import com.hermex.android.core.network.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProvidersUiState(
    val isLoading: Boolean = false,
    val providers: List<ProviderStatus> = emptyList(),
    val errorMessage: String? = null,
)

class ProvidersViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProvidersUiState())
    val uiState: StateFlow<ProvidersUiState> = _uiState.asStateFlow()

    fun loadProviders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val api = authRepository.apiForActiveServer() ?: throw ApiError.Network(Exception("Not signed in"))
                val response = safeApiCall { api.modelsLive() }
                val providers = response.providers?.map { provider ->
                    ProviderStatus(
                        name = provider.name,
                        isConfigured = true,
                        isHealthy = true,
                        modelCount = provider.models?.size ?: 0,
                    )
                } ?: emptyList()
                _uiState.update { it.copy(isLoading = false, providers = providers) }
            } catch (e: ApiError) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load providers") }
            }
        }
    }
}
