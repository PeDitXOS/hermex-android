package com.peditx.hermex.chat

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.peditx.hermex.core.network.dto.ModelCatalogGroup
import com.peditx.hermex.core.network.dto.ModelCatalogOption
import com.peditx.hermex.core.network.dto.ProfileSummary
import com.peditx.hermex.core.util.HermexLog
import com.peditx.hermex.ui.theme.HermexRadii
import java.io.File

/** [ChatComposer]'s callbacks, grouped so adding a future action (slash commands) doesn't widen
 * [ChatComposer]'s own parameter list. */
data class ChatComposerActions(
    val onTextChanged: (String) -> Unit,
    val onSend: () -> Unit,
    val onSteer: () -> Unit,
    val onStop: () -> Unit,
    val onSelectProfile: (String) -> Unit,
    val onOpenModelPicker: () -> Unit,
    val onSelectModel: (ModelCatalogOption) -> Unit,
    val onAttachFile: (Uri) -> Unit,
    val onRemoveAttachment: (String) -> Unit,
    val onSendVoiceNote: (File, String) -> Unit,
    val onRefresh: () -> Unit,
    val onStartVoiceRecording: () -> Unit,
    val onStartAudioWave: () -> Unit,
)

/** The profile dropdown's own list/selection data -- separate from [ChatComposerState] because
 * it's plain display data, not busy/disabled state. */
data class ChatComposerProfileSelectorState(
    val profileOptions: List<ProfileSummary>,
    val selectedProfileName: String?,
) {
    companion object {
        fun from(uiState: ChatUiState): ChatComposerProfileSelectorState = ChatComposerProfileSelectorState(
            profileOptions = uiState.profileOptions,
            selectedProfileName = uiState.selectedProfileName,
        )
    }
}

/** The model dropdown's own list/selection data -- separate from [ChatComposerState] for the
 * same reason as [ChatComposerProfileSelectorState]. */
data class ChatComposerModelSelectorState(
    val modelCatalogGroups: List<ModelCatalogGroup>,
    val currentModel: String?,
    val currentModelProvider: String?,
    val isLoadingModelCatalog: Boolean,
) {
    companion object {
        fun from(uiState: ChatUiState): ChatComposerModelSelectorState = ChatComposerModelSelectorState(
            modelCatalogGroups = uiState.modelCatalogGroups,
            currentModel = uiState.currentModel,
            currentModelProvider = uiState.currentModelProvider,
            isLoadingModelCatalog = uiState.isLoadingModelCatalog,
        )
    }
}

/** The pending-attachment strip's own list data -- separate from [ChatComposerState] for the same
 * reason as [ChatComposerProfileSelectorState]: it's plain display data, not busy/disabled state. */
data class ChatComposerAttachmentState(
    val pendingAttachments: List<PendingAttachmentUi>,
) {
    companion object {
        fun from(uiState: ChatUiState): ChatComposerAttachmentState = ChatComposerAttachmentState(
            pendingAttachments = uiState.pendingAttachments,
        )
    }
}

/** Caps how wide the composer dock ever gets, so a large tablet's wide-layout right pane doesn't
 * stretch it into an uncomfortably wide, hard-to-scan input. Never binds on phone-scale widths. */
private val ComposerMaxWidth = 840.dp

/**
 * Composer matching the design:
 * - Header: [☰] [Hermes Agent pill] [👁]  (handled by ChatScreen top app bar)
 * - Main area: "Start chatting" when empty (handled by ChatScreen)
 * - Input bar: Single rounded rect: [+] [Text Field "Ask Conduit"] [🎤] [🌊]
 * - Voice recording: Hold mic button
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatComposer(
    composerState: ChatComposerState,
    profileSelectorState: ChatComposerProfileSelectorState,
    modelSelectorState: ChatComposerModelSelectorState,
    attachmentState: ChatComposerAttachmentState,
    actions: ChatComposerActions,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var lastLoggedHeightPx by remember { mutableStateOf(-1) }
    var showCommandPicker by remember { mutableStateOf(false) }
    var commandQuery by remember { mutableStateOf("") }

    // File picker launcher for attach button
    val filePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let(actions.onAttachFile)
    }

    // Permission launcher for microphone
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            // Permission granted - voice recording can start
        }
    }

    // Voice recording state
    var isRecording by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableStateOf(0L) }
    val recordingScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    fun performHaptic() {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    fun startRecording() {
        isRecording = true
        recordingDuration = 0
        recordingScope.launch {
            while (isRecording && recordingDuration < 300_000) { // 5 min max
                delay(100)
                recordingDuration += 100
            }
        }
    }

    fun stopRecording() {
        isRecording = false
        if (recordingDuration > 500) {
            // Voice note recorded - send it
            actions.onSendVoiceNote(File(""), formatElapsed(recordingDuration))
        }
    }

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Surface(
            modifier = Modifier
                .widthIn(max = ComposerMaxWidth)
                .onGloballyPositioned { coordinates ->
                    val heightPx = coordinates.size.height
                    if (heightPx != lastLoggedHeightPx) {
                        lastLoggedHeightPx = heightPx
                        HermexLog.d("Composer", "measured height=${heightPx}px")
                    }
                },
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(topStart = HermexRadii.SettingsCard, topEnd = HermexRadii.SettingsCard),
            tonalElevation = 4.dp,
        ) {
            Column(modifier = Modifier.navigationBarsPadding()) {
                // Hairline separating the dock from the message list above
                androidx.compose.material3.HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    thickness = 1.dp
                )

                if (attachmentState.pendingAttachments.isNotEmpty()) {
                    PendingAttachmentStrip(
                        attachments = attachmentState.pendingAttachments,
                        onRemove = actions.onRemoveAttachment,
                    )
                }

                if (showCommandPicker) {
                    val suggestions = CommandRegistry.filter(commandQuery)
                    if (suggestions.isNotEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(HermexRadii.Accessory),
                            shadowElevation = 4.dp,
                        ) {
                            Column {
                                suggestions.forEach { suggestion ->
                                    androidx.compose.material3.ListItem(
                                        headlineContent = { Text(suggestion.command) },
                                        supportingContent = { Text(suggestion.description) },
                                        leadingContent = {
                                            Icon(suggestion.icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        },
                                        modifier = Modifier.clickable {
                                            actions.onTextChanged(suggestion.command + " ")
                                            showCommandPicker = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                }

                // Input bar: [+] [Text Field] [🎤] [🌊]
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    // Single rounded container for input
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(HermexRadii.Composer)
                            )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            // + button (far left)
                            IconButton(onClick = { filePickerLauncher.launch(arrayOf("*/*")) }) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "Attach file",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }

                            // Text field - takes remaining space
                            OutlinedTextField(
                                value = composerState.text,
                                onValueChange = { newValue ->
                                    actions.onTextChanged(newValue)
                                    if (newValue.startsWith("/")) {
                                        showCommandPicker = true
                                        commandQuery = newValue
                                    } else {
                                        showCommandPicker = false
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp, end = 4.dp),
                                placeholder = { Text(
                                    text = if (composerState.isStreaming) "Steer the response…" else "Ask Hermes",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                ) },
                                enabled = composerState.isTextFieldEnabled,
                                maxLines = 5,
                                singleLine = false,
                                shape = RoundedCornerShape(HermexRadii.Composer),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                ),
                                label = null,
                            )

                            // ===== DYNAMIC ACTION BUTTON: Send / Steer / Stop / Voice =====
                            val isStreaming = composerState.isStreaming
                            val hasText = composerState.text.isNotBlank()
                            val showStop = composerState.showStopButton

                            val actionIcon: androidx.compose.ui.graphics.vector.ImageVector
                            val actionDesc: String
                            val actionContainerColor: androidx.compose.ui.graphics.Color
                            val actionContentColor: androidx.compose.ui.graphics.Color
                            val actionEnabled: Boolean

                            when {
                                showStop || (isStreaming && !hasText) -> {
                                    actionIcon = Close
                                    actionDesc = "Stop"
                                    actionContainerColor = MaterialTheme.colorScheme.errorContainer
                                    actionContentColor = MaterialTheme.colorScheme.onErrorContainer
                                    actionEnabled = true
                                }
                                isStreaming && hasText -> {
                                    actionIcon = ArrowForward
                                    actionDesc = "Steer"
                                    actionContainerColor = MaterialTheme.colorScheme.primaryContainer
                                    actionContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    actionEnabled = composerState.canSend
                                }
                                !isStreaming && hasText -> {
                                    actionIcon = ArrowForward
                                    actionDesc = "Send"
                                    actionContainerColor = MaterialTheme.colorScheme.primaryContainer
                                    actionContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    actionEnabled = composerState.canSend
                                }
                                else -> {
                                    actionIcon = Mic
                                    actionDesc = "Voice"
                                    actionContainerColor = MaterialTheme.colorScheme.primaryContainer
                                    actionContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    actionEnabled = true
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .combinedClickable(
                                        onClick = {
                                            performHaptic()
                                            when {
                                                showStop || (isStreaming && !hasText) -> actions.onStop()
                                                isStreaming && hasText -> {
                                                    if (composerState.canSend) actions.onSteer()
                                                }
                                                !isStreaming && hasText -> {
                                                    if (composerState.canSend) actions.onSend()
                                                }
                                                else -> {
                                                    // Empty + not streaming: do nothing on click (voice needs long press)
                                                }
                                            }
                                        },
                                        onLongClick = {
                                            if (!isStreaming && !hasText) {
                                                val hasPermission = ContextCompat.checkSelfPermission(
                                                    context, Manifest.permission.RECORD_AUDIO
                                                ) == PackageManager.PERMISSION_GRANTED
                                                if (hasPermission) {
                                                    performHaptic()
                                                    startRecording()
                                                } else {
                                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                                }
                                            }
                                        },
                                        onDoubleClick = {}
                                    )
                                    .padding(end = 4.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (isRecording) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                shape = RoundedCornerShape(HermexRadii.Composer)
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                Icons.Filled.Mic,
                                                contentDescription = "Recording",
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.size(16.dp),
                                            )
                                            Text(
                                                text = formatElapsed(recordingDuration),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            )
                                        }
                                    }
                                } else {
                                    FilledIconButton(
                                        onClick = { /* handled by combinedClickable */ },
                                        enabled = actionEnabled,
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = actionContainerColor,
                                            contentColor = actionContentColor,
                                            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        ),
                                        modifier = Modifier.size(40.dp),
                                    ) {
                                        Icon(
                                            actionIcon,
                                            contentDescription = actionDesc,
                                            modifier = Modifier.size(20.dp),
                                        )
                                    }
                                }
                            }

                            // Audio wave button (for audio wave input)
                            IconButton(onClick = { actions.onStartAudioWave() }) {
                                Icon(
                                    Icons.Filled.KeyboardVoice,
                                    contentDescription = "Audio wave input",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Format milliseconds to MM:SS */
private fun formatElapsed(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

@Composable
private fun PendingAttachmentStrip(
    attachments: List<PendingAttachmentUi>,
    onRemove: (String) -> Unit,
) {
    val context = LocalContext.current
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(attachments, key = { it.id }) { attachment ->
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = RoundedCornerShape(HermexRadii.Accessory),
            ) {
                Row(
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (attachment.isImage == true && attachment.path != null) {
                        // Thumbnail for images
                        Box(
                            modifier = Modifier
                                .size(40.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            coil3.compose.SubcomposeAsyncImage(
                                model = attachment.path,
                                contentDescription = attachment.name ?: "Attachment thumbnail",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(HermexRadii.Accessory)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                loading = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                MaterialTheme.colorScheme.surfaceContainerLow,
                                                RoundedCornerShape(HermexRadii.Accessory),
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                        )
                                    }
                                },
                                error = {
                                    Icon(
                                        Icons.Filled.Image,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(24.dp),
                                    )
                                },
                            )
                        }
                    } else {
                        Icon(
                            attachment.mime?.let { com.peditx.hermex.core.network.dto.fileTypeIcon(it) }
                                ?: Icons.Filled.Image,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp).padding(8.dp),
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                    ) {
                        Text(
                            text = attachment.name ?: "File",
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        attachment.size?.let { size ->
                            Text(
                                text = android.text.format.Formatter.formatFileSize(context, size),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            )
                        }
                    }
                    IconButton(
                        onClick = { onRemove(attachment.id) },
                        modifier = Modifier.padding(end = 8.dp),
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Remove attachment",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}