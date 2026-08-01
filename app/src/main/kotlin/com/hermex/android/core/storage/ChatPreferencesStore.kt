package com.hermex.android.core.storage

/** Local chat display preferences -- distinct from [ServerStore]/[CookieStore], which persist
 * auth/connection state. See [CookieStore] for why this is plain DataStore rather than
 * EncryptedSharedPreferences (nothing here is sensitive either). */
interface ChatPreferencesStore {
    suspend fun loadExpandThinkingByDefault(): Boolean
    suspend fun setExpandThinkingByDefault(value: Boolean)
    suspend fun loadExpandToolCallsByDefault(): Boolean
    suspend fun setExpandToolCallsByDefault(value: Boolean)
    suspend fun loadNotificationsEnabled(): Boolean
    suspend fun setNotificationsEnabled(value: Boolean)
    suspend fun loadShowSubagentSessions(): Boolean
    suspend fun setShowSubagentSessions(value: Boolean)
    // Session Display settings
    suspend fun loadShowSessionMessageCount(): Boolean
    suspend fun setShowSessionMessageCount(value: Boolean)
    suspend fun loadShowSessionWorkspace(): Boolean
    suspend fun setShowSessionWorkspace(value: Boolean)
    suspend fun loadShowCronSessions(): Boolean
    suspend fun setShowCronSessions(value: Boolean)
    suspend fun loadShowCliSessions(): Boolean
    suspend fun setShowCliSessions(value: Boolean)
    suspend fun loadShowClaudeCodeSessions(): Boolean
    suspend fun setShowClaudeCodeSessions(value: Boolean)
    // Chat Display settings
    suspend fun loadShowsThinkingAndToolCards(): Boolean
    suspend fun setShowsThinkingAndToolCards(value: Boolean)
    suspend fun loadThinkingCardsStartExpanded(): Boolean
    suspend fun setThinkingCardsStartExpanded(value: Boolean)
    suspend fun loadToolCardsStartExpanded(): Boolean
    suspend fun setToolCardsStartExpanded(value: Boolean)
    suspend fun loadIsStreamedTextAnimationEnabled(): Boolean
    suspend fun setIsStreamedTextAnimationEnabled(value: Boolean)
    suspend fun loadShowsAssistantTurnTimestamps(): Boolean
    suspend fun setShowsAssistantTurnTimestamps(value: Boolean)
    suspend fun loadWrapsCodeBlockLines(): Boolean
    suspend fun setWrapsCodeBlockLines(value: Boolean)
    suspend fun loadRtlChatLayoutEnabled(): Boolean
    suspend fun setRtlChatLayoutEnabled(value: Boolean)
    suspend fun loadHidesAttachmentPaths(): Boolean
    suspend fun setHidesAttachmentPaths(value: Boolean)
    // Interaction settings
    suspend fun loadIsHapticsEnabled(): Boolean
    suspend fun setIsHapticsEnabled(value: Boolean)
    suspend fun loadStreamingSendBehavior(): String
    suspend fun setStreamingSendBehavior(value: String)
    suspend fun loadSttProvider(): String
    suspend fun setSttProvider(value: String)
}

/** Default used wherever no real store is wired in -- mirrors [NoOpAppearancePreferencesStore].
 * Always the default value for each preference; every write is a no-op. */
object NoOpChatPreferencesStore : ChatPreferencesStore {
    override suspend fun loadExpandThinkingByDefault(): Boolean = false
    override suspend fun setExpandThinkingByDefault(value: Boolean) = Unit
    override suspend fun loadExpandToolCallsByDefault(): Boolean = false
    override suspend fun setExpandToolCallsByDefault(value: Boolean) = Unit
    override suspend fun loadNotificationsEnabled(): Boolean = false
    override suspend fun setNotificationsEnabled(value: Boolean) = Unit
    override suspend fun loadShowSubagentSessions(): Boolean = true
    override suspend fun setShowSubagentSessions(value: Boolean) = Unit
    // Session Display
    override suspend fun loadShowSessionMessageCount(): Boolean = true
    override suspend fun setShowSessionMessageCount(value: Boolean) = Unit
    override suspend fun loadShowSessionWorkspace(): Boolean = true
    override suspend fun setShowSessionWorkspace(value: Boolean) = Unit
    override suspend fun loadShowCronSessions(): Boolean = true
    override suspend fun setShowCronSessions(value: Boolean) = Unit
    override suspend fun loadShowCliSessions(): Boolean = true
    override suspend fun setShowCliSessions(value: Boolean) = Unit
    override suspend fun loadShowClaudeCodeSessions(): Boolean = false
    override suspend fun setShowClaudeCodeSessions(value: Boolean) = Unit
    // Chat Display
    override suspend fun loadShowsThinkingAndToolCards(): Boolean = true
    override suspend fun setShowsThinkingAndToolCards(value: Boolean) = Unit
    override suspend fun loadThinkingCardsStartExpanded(): Boolean = false
    override suspend fun setThinkingCardsStartExpanded(value: Boolean) = Unit
    override suspend fun loadToolCardsStartExpanded(): Boolean = false
    override suspend fun setToolCardsStartExpanded(value: Boolean) = Unit
    override suspend fun loadIsStreamedTextAnimationEnabled(): Boolean = true
    override suspend fun setIsStreamedTextAnimationEnabled(value: Boolean) = Unit
    override suspend fun loadShowsAssistantTurnTimestamps(): Boolean = false
    override suspend fun setShowsAssistantTurnTimestamps(value: Boolean) = Unit
    override suspend fun loadWrapsCodeBlockLines(): Boolean = false
    override suspend fun setWrapsCodeBlockLines(value: Boolean) = Unit
    override suspend fun loadRtlChatLayoutEnabled(): Boolean = false
    override suspend fun setRtlChatLayoutEnabled(value: Boolean) = Unit
    override suspend fun loadHidesAttachmentPaths(): Boolean = true
    override suspend fun setHidesAttachmentPaths(value: Boolean) = Unit
    // Interaction
    override suspend fun loadIsHapticsEnabled(): Boolean = true
    override suspend fun setIsHapticsEnabled(value: Boolean) = Unit
    override suspend fun loadStreamingSendBehavior(): String = "steer"
    override suspend fun setStreamingSendBehavior(value: String) = Unit
    override suspend fun loadSttProvider(): String = "on_device"
    override suspend fun setSttProvider(value: String) = Unit
}
