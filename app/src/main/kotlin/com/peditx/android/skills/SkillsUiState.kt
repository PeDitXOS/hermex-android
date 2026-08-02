package com.peditx.hermex.skills

import com.peditx.hermex.core.network.dto.SkillSummary

data class SkillsUiState(
    val isLoading: Boolean = true,
    val skills: List<SkillSummary> = emptyList(),
    val errorMessage: String? = null,
)

data class SkillDetailUiState(
    val isLoading: Boolean = true,
    val name: String? = null,
    val content: String? = null,
    val linkedFiles: List<String> = emptyList(),
    val errorMessage: String? = null,
)
