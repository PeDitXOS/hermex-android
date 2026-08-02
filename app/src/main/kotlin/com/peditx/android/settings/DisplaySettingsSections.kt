package com.peditx.hermex.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Session display settings — controls what's shown in session list rows.
 * iOS SessionRowDisplaySettings equivalent.
 */
@Composable
fun SessionDisplaySettingsSection(
    showMessageCount: Boolean,
    showWorkspace: Boolean,
    showCronSessions: Boolean,
    showCliSessions: Boolean,
    showClaudeCodeSessions: Boolean,
    showSubagentSessions: Boolean,
    onToggleMessageCount: (Boolean) -> Unit,
    onToggleWorkspace: (Boolean) -> Unit,
    onToggleCronSessions: (Boolean) -> Unit,
    onToggleCliSessions: (Boolean) -> Unit,
    onToggleClaudeCodeSessions: (Boolean) -> Unit,
    onToggleSubagentSessions: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SettingsToggleRow("Message Count", "Show message count in session list", showMessageCount, onToggleMessageCount)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        SettingsToggleRow("Workspace", "Show workspace in session list", showWorkspace, onToggleWorkspace)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        SettingsToggleRow("Cron Sessions", "Show cron/scheduled sessions", showCronSessions, onToggleCronSessions)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        SettingsToggleRow("CLI Sessions", "Show CLI terminal sessions", showCliSessions, onToggleCliSessions)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        SettingsToggleRow("Claude Code Sessions", "Show Claude Code sessions", showClaudeCodeSessions, onToggleClaudeCodeSessions)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        SettingsToggleRow("Subagent Sessions", "Show subagent sessions", showSubagentSessions, onToggleSubagentSessions)
    }
}

/**
 * Chat display settings — controls how messages are rendered.
 * iOS ChatTranscriptDisplaySettings equivalent.
 */
@Composable
fun ChatDisplaySettingsSection(
    showThinkingAndToolCards: Boolean,
    thinkingExpanded: Boolean,
    toolExpanded: Boolean,
    streamAnimation: Boolean,
    responseTimestamps: Boolean,
    wrapCodeBlocks: Boolean,
    rtlLayout: Boolean,
    hideAttachmentPaths: Boolean,
    onToggleThinkingTools: (Boolean) -> Unit,
    onToggleThinkingExpanded: (Boolean) -> Unit,
    onToggleToolExpanded: (Boolean) -> Unit,
    onToggleStreamAnimation: (Boolean) -> Unit,
    onToggleTimestamps: (Boolean) -> Unit,
    onToggleWrapCode: (Boolean) -> Unit,
    onToggleRtl: (Boolean) -> Unit,
    onToggleHidePaths: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SettingsToggleRow("Thinking and Tool Cards", "Show thinking and tool call cards", showThinkingAndToolCards, onToggleThinkingTools)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        SettingsToggleRow("Expand Thinking by Default", "Thinking cards start expanded", thinkingExpanded, onToggleThinkingExpanded)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        SettingsToggleRow("Expand Tools by Default", "Tool cards start expanded", toolExpanded, onToggleToolExpanded)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        SettingsToggleRow("Streamed Text Animation", "Fade in words as response streams", streamAnimation, onToggleStreamAnimation)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        SettingsToggleRow("Response Timestamps", "Show timestamp above each response", responseTimestamps, onToggleTimestamps)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        SettingsToggleRow("Wrap Code Block Lines", "Wrap long lines in code blocks", wrapCodeBlocks, onToggleWrapCode)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        SettingsToggleRow("Right-to-Left Chat Layout", "RTL layout for Arabic/Hebrew/Persian", rtlLayout, onToggleRtl)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        SettingsToggleRow("Hide Attachment Paths", "Hide file paths in sent messages", hideAttachmentPaths, onToggleHidePaths)
    }
}

/**
 * Interaction settings — haptics, streaming behavior, STT provider.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractionSettingsSection(
    hapticsEnabled: Boolean,
    streamingBehavior: String,
    sttProvider: String,
    onToggleHaptics: (Boolean) -> Unit,
    onSetStreamingBehavior: (String) -> Unit,
    onSetSttProvider: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SettingsToggleRow("Haptic Feedback", "Vibrate on interactions", hapticsEnabled, onToggleHaptics)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        // Streaming send behavior
        var expanded by remember { mutableStateOf(false) }
        SettingsInfoRow(
            title = "Send While Responding",
            value = when (streamingBehavior) {
                "steer" -> "Steer"
                "cancel_and_send" -> "Cancel & Send"
                "queue" -> "Queue"
                else -> "Steer"
            },
        )
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(text = { Text("Steer — redirect the response") }, onClick = { onSetStreamingBehavior("steer"); expanded = false })
                DropdownMenuItem(text = { Text("Cancel & Send — stop then send") }, onClick = { onSetStreamingBehavior("cancel_and_send"); expanded = false })
                DropdownMenuItem(text = { Text("Queue — wait for response to finish") }, onClick = { onSetStreamingBehavior("queue"); expanded = false })
            }
        }
        SettingsFootnote("What happens when you send a message while a response is streaming.")

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        // STT provider
        var sttExpanded by remember { mutableStateOf(false) }
        SettingsInfoRow(
            title = "Dictation Provider",
            value = if (sttProvider == "server") "Server" else "On-Device",
        )
        ExposedDropdownMenuBox(expanded = sttExpanded, onExpandedChange = { sttExpanded = it }) {
            ExposedDropdownMenu(expanded = sttExpanded, onDismissRequest = { sttExpanded = false }) {
                DropdownMenuItem(text = { Text("On-Device — keeps audio off server") }, onClick = { onSetSttProvider("on_device"); sttExpanded = false })
                DropdownMenuItem(text = { Text("Server — uses server transcription") }, onClick = { onSetSttProvider("server"); sttExpanded = false })
            }
        }
        SettingsFootnote("On-device keeps composer dictation audio off your Hermes server.")
    }
}
