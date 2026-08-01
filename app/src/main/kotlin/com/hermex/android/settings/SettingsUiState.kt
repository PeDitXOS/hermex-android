package com.hermex.android.settings

import com.hermex.android.core.storage.AppIconVariant
import com.hermex.android.core.storage.HeaderLogoColor

data class SettingsUiState(
    val isLoading: Boolean = true,
    val serverUrl: String? = null,
    /** The active server's user-editable display name (see [com.hermex.android.core.storage.HermexServerConfig]). */
    val activeServerName: String? = null,
    val serverVersion: String? = null,
    /** The server's remembered default model id, shown as-is (matching iOS). */
    val defaultModel: String? = null,
    val defaultProfile: String? = null,
    val isSigningOut: Boolean = false,
    val errorMessage: String? = null,
    val expandThinkingByDefault: Boolean = false,
    val expandToolCallsByDefault: Boolean = false,
    /** Count of saved custom headers. */
    val customHeaderCount: Int = 0,
    val headerLogoColor: HeaderLogoColor = HeaderLogoColor.DEFAULT,
    val appIconVariant: AppIconVariant = AppIconVariant.SYSTEM,
    val notificationsEnabled: Boolean = false,
    val showSubagentSessions: Boolean = true,
    val userInitials: String = "BD",
    // === iOS parity toggles ===
    val showSessionMessageCount: Boolean = true,
    val showSessionWorkspace: Boolean = true,
    val showCronSessions: Boolean = true,
    val showCliSessions: Boolean = true,
    val showClaudeCodeSessions: Boolean = false,
    val showsThinkingAndToolCards: Boolean = true,
    val thinkingCardsStartExpanded: Boolean = false,
    val toolCardsStartExpanded: Boolean = false,
    val isStreamedTextAnimationEnabled: Boolean = true,
    val showsAssistantTurnTimestamps: Boolean = false,
    val wrapsCodeBlockLines: Boolean = false,
    val rtlChatLayoutEnabled: Boolean = false,
    val hidesAttachmentPaths: Boolean = true,
    val tintsPrimaryActions: Boolean = false,
    val isHapticsEnabled: Boolean = true,
    /** Streaming send behavior: steer, cancel-and-send, or queue. */
    val streamingSendBehavior: String = "steer",
    /** STT provider: on_device or server. */
    val sttProvider: String = "on_device",
)
