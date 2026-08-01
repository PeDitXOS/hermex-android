package com.hermex.android.core.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull

private val Context.chatPreferencesDataStore by preferencesDataStore(name = "hermex_chat_preferences")

class DataStoreChatPreferencesStore(private val context: Context) : ChatPreferencesStore {
    private val expandThinkingKey = booleanPreferencesKey("expand_thinking_by_default")
    private val expandToolCallsKey = booleanPreferencesKey("expand_tool_calls_by_default")
    private val notificationsEnabledKey = booleanPreferencesKey("notifications_enabled")
    private val showSubagentSessionsKey = booleanPreferencesKey("show_subagent_sessions")
    // Session Display
    private val showSessionMessageCountKey = booleanPreferencesKey("show_session_message_count")
    private val showSessionWorkspaceKey = booleanPreferencesKey("show_session_workspace")
    private val showCronSessionsKey = booleanPreferencesKey("show_cron_sessions")
    private val showCliSessionsKey = booleanPreferencesKey("show_cli_sessions")
    private val showClaudeCodeSessionsKey = booleanPreferencesKey("show_claude_code_sessions")
    // Chat Display
    private val showsThinkingAndToolCardsKey = booleanPreferencesKey("shows_thinking_and_tool_cards")
    private val thinkingCardsStartExpandedKey = booleanPreferencesKey("thinking_cards_start_expanded")
    private val toolCardsStartExpandedKey = booleanPreferencesKey("tool_cards_start_expanded")
    private val isStreamedTextAnimationEnabledKey = booleanPreferencesKey("is_streamed_text_animation_enabled")
    private val showsAssistantTurnTimestampsKey = booleanPreferencesKey("shows_assistant_turn_timestamps")
    private val wrapsCodeBlockLinesKey = booleanPreferencesKey("wraps_code_block_lines")
    private val rtlChatLayoutEnabledKey = booleanPreferencesKey("rtl_chat_layout_enabled")
    private val hidesAttachmentPathsKey = booleanPreferencesKey("hides_attachment_paths")
    // Interaction
    private val isHapticsEnabledKey = booleanPreferencesKey("is_haptics_enabled")
    private val streamingSendBehaviorKey = stringPreferencesKey("streaming_send_behavior")
    private val sttProviderKey = stringPreferencesKey("stt_provider")

    override suspend fun loadExpandThinkingByDefault(): Boolean =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(expandThinkingKey) ?: false

    override suspend fun setExpandThinkingByDefault(value: Boolean) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[expandThinkingKey] = value }
    }

    override suspend fun loadExpandToolCallsByDefault(): Boolean =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(expandToolCallsKey) ?: false

    override suspend fun setExpandToolCallsByDefault(value: Boolean) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[expandToolCallsKey] = value }
    }

    override suspend fun loadNotificationsEnabled(): Boolean =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(notificationsEnabledKey) ?: false

    override suspend fun setNotificationsEnabled(value: Boolean) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[notificationsEnabledKey] = value }
    }

    override suspend fun loadShowSubagentSessions(): Boolean =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(showSubagentSessionsKey) ?: true

    override suspend fun setShowSubagentSessions(value: Boolean) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[showSubagentSessionsKey] = value }
    }

    // Session Display
    override suspend fun loadShowSessionMessageCount(): Boolean =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(showSessionMessageCountKey) ?: true

    override suspend fun setShowSessionMessageCount(value: Boolean) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[showSessionMessageCountKey] = value }
    }

    override suspend fun loadShowSessionWorkspace(): Boolean =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(showSessionWorkspaceKey) ?: true

    override suspend fun setShowSessionWorkspace(value: Boolean) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[showSessionWorkspaceKey] = value }
    }

    override suspend fun loadShowCronSessions(): Boolean =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(showCronSessionsKey) ?: true

    override suspend fun setShowCronSessions(value: Boolean) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[showCronSessionsKey] = value }
    }

    override suspend fun loadShowCliSessions(): Boolean =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(showCliSessionsKey) ?: true

    override suspend fun setShowCliSessions(value: Boolean) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[showCliSessionsKey] = value }
    }

    override suspend fun loadShowClaudeCodeSessions(): Boolean =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(showClaudeCodeSessionsKey) ?: false

    override suspend fun setShowClaudeCodeSessions(value: Boolean) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[showClaudeCodeSessionsKey] = value }
    }

    // Chat Display
    override suspend fun loadShowsThinkingAndToolCards(): Boolean =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(showsThinkingAndToolCardsKey) ?: true

    override suspend fun setShowsThinkingAndToolCards(value: Boolean) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[showsThinkingAndToolCardsKey] = value }
    }

    override suspend fun loadThinkingCardsStartExpanded(): Boolean =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(thinkingCardsStartExpandedKey) ?: false

    override suspend fun setThinkingCardsStartExpanded(value: Boolean) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[thinkingCardsStartExpandedKey] = value }
    }

    override suspend fun loadToolCardsStartExpanded(): Boolean =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(toolCardsStartExpandedKey) ?: false

    override suspend fun setToolCardsStartExpanded(value: Boolean) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[toolCardsStartExpandedKey] = value }
    }

    override suspend fun loadIsStreamedTextAnimationEnabled(): Boolean =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(isStreamedTextAnimationEnabledKey) ?: true

    override suspend fun setIsStreamedTextAnimationEnabled(value: Boolean) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[isStreamedTextAnimationEnabledKey] = value }
    }

    override suspend fun loadShowsAssistantTurnTimestamps(): Boolean =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(showsAssistantTurnTimestampsKey) ?: false

    override suspend fun setShowsAssistantTurnTimestamps(value: Boolean) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[showsAssistantTurnTimestampsKey] = value }
    }

    override suspend fun loadWrapsCodeBlockLines(): Boolean =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(wrapsCodeBlockLinesKey) ?: false

    override suspend fun setWrapsCodeBlockLines(value: Boolean) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[wrapsCodeBlockLinesKey] = value }
    }

    override suspend fun loadRtlChatLayoutEnabled(): Boolean =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(rtlChatLayoutEnabledKey) ?: false

    override suspend fun setRtlChatLayoutEnabled(value: Boolean) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[rtlChatLayoutEnabledKey] = value }
    }

    override suspend fun loadHidesAttachmentPaths(): Boolean =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(hidesAttachmentPathsKey) ?: true

    override suspend fun setHidesAttachmentPaths(value: Boolean) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[hidesAttachmentPathsKey] = value }
    }

    // Interaction
    override suspend fun loadIsHapticsEnabled(): Boolean =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(isHapticsEnabledKey) ?: true

    override suspend fun setIsHapticsEnabled(value: Boolean) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[isHapticsEnabledKey] = value }
    }

    override suspend fun loadStreamingSendBehavior(): String =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(streamingSendBehaviorKey) ?: "steer"

    override suspend fun setStreamingSendBehavior(value: String) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[streamingSendBehaviorKey] = value }
    }

    override suspend fun loadSttProvider(): String =
        context.chatPreferencesDataStore.data.firstOrNull()?.get(sttProviderKey) ?: "on_device"

    override suspend fun setSttProvider(value: String) {
        context.chatPreferencesDataStore.edit { prefs -> prefs[sttProviderKey] = value }
    }
}
