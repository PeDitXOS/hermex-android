package com.hermex.android.workspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermex.android.auth.AuthRepository
import com.hermex.android.core.network.ApiError
import com.hermex.android.core.network.safeApiCall
import com.hermex.android.core.network.dto.KanbanBoard
import com.hermex.android.core.network.dto.KanbanCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class KanbanUiState(
    val isLoading: Boolean = false,
    val columns: Map<String, List<KanbanCard>> = emptyMap(),
    val errorMessage: String? = null,
    val selectedCard: KanbanCard? = null,
)

class KanbanViewModel(
    private val sessionId: String,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(KanbanUiState())
    val uiState: StateFlow<KanbanUiState> = _uiState.asStateFlow()

    fun loadBoard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val api = authRepository.apiForActiveServer() ?: throw ApiError.Network(Exception("Not signed in"))
                // Kanban endpoint is not in the API contract yet; fall back to empty board
                // TODO: wire up once server exposes /api/kanban/board
                _uiState.update { it.copy(isLoading = false, columns = emptyMap()) }
            } catch (e: ApiError) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load board") }
            }
        }
    }

    fun selectCard(card: KanbanCard?) {
        _uiState.update { it.copy(selectedCard = card) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
