package com.peditx.hermex.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class KanbanBoard(
    val columns: List<KanbanColumn> = emptyList(),
    val error: String? = null,
)

@Serializable
data class KanbanColumn(
    val id: String,
    val title: String,
    val cards: List<KanbanCard> = emptyList(),
)

@Serializable
data class KanbanCard(
    val id: String,
    val title: String,
    val description: String? = null,
    val columnId: String? = null,
    val priority: Int? = null,
    val createdAt: String? = null,
)

@Serializable
data class KanbanCardRequest(
    val session_id: String,
    val title: String,
    val description: String? = null,
    val column_id: String? = null,
)

@Serializable
data class KanbanCardUpdateRequest(
    val session_id: String,
    val card_id: String,
    val title: String? = null,
    val description: String? = null,
    val column_id: String? = null,
    val priority: Int? = null,
)

@Serializable
data class KanbanMutationResponse(
    val ok: Boolean? = null,
    val card_id: String? = null,
    val error: String? = null,
)

@Serializable
data class KanbanBoardResponse(
    val board: KanbanBoard? = null,
    val error: String? = null,
)

@Serializable
data class KanbanCardDeleteRequest(
    val session_id: String,
    val card_id: String,
)
