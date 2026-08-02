package com.peditx.hermex.workspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peditx.hermex.auth.AuthRepository
import com.peditx.hermex.core.network.ApiError
import com.peditx.hermex.core.network.safeApiCall
import com.peditx.hermex.core.network.dto.GitCheckoutRequest
import com.peditx.hermex.core.network.dto.SessionIdRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GitWriteUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val branches: List<String> = emptyList(),
    val currentBranch: String? = null,
)

class GitWriteViewModel(
    private val sessionId: String,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GitWriteUiState())
    val uiState: StateFlow<GitWriteUiState> = _uiState.asStateFlow()

    fun commit(message: String) {
        if (message.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val api = authRepository.apiForActiveServer() ?: throw ApiError.Network(Exception("Not signed in"))
                safeApiCall { api.gitCommit(sessionId, message) }
                _uiState.update { it.copy(isLoading = false, successMessage = "Committed: $message") }
            } catch (e: ApiError) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Commit failed") }
            }
        }
    }

    fun push() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val api = authRepository.apiForActiveServer() ?: throw ApiError.Network(Exception("Not signed in"))
                safeApiCall { api.gitPush(SessionIdRequest(session_id = sessionId)) }
                _uiState.update { it.copy(isLoading = false, successMessage = "Pushed successfully") }
            } catch (e: ApiError) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Push failed") }
            }
        }
    }

    fun pull() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val api = authRepository.apiForActiveServer() ?: throw ApiError.Network(Exception("Not signed in"))
                safeApiCall { api.gitPull(SessionIdRequest(session_id = sessionId)) }
                _uiState.update { it.copy(isLoading = false, successMessage = "Pulled successfully") }
            } catch (e: ApiError) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Pull failed") }
            }
        }
    }

    fun checkout(branch: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val api = authRepository.apiForActiveServer() ?: throw ApiError.Network(Exception("Not signed in"))
                safeApiCall { api.gitCheckout(GitCheckoutRequest(session_id = sessionId, branch = branch)) }
                _uiState.update { it.copy(isLoading = false, currentBranch = branch, successMessage = "Checked out $branch") }
            } catch (e: ApiError) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Checkout failed") }
            }
        }
    }

    fun discard(path: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val api = authRepository.apiForActiveServer() ?: throw ApiError.Network(Exception("Not signed in"))
                safeApiCall { api.gitDiscard(com.peditx.hermex.core.network.dto.GitDiscardRequest(session_id = sessionId, path = path)) }
                _uiState.update { it.copy(isLoading = false, successMessage = "Changes discarded") }
            } catch (e: ApiError) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Discard failed") }
            }
        }
    }

    fun loadBranches() {
        viewModelScope.launch {
            try {
                val api = authRepository.apiForActiveServer() ?: return@launch
                val response = safeApiCall { api.gitBranches(sessionId) }
                val branches = response.branches?.branches?.mapNotNull { it.name } ?: emptyList()
                _uiState.update { it.copy(branches = branches) }
            } catch (_: ApiError) {}
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
