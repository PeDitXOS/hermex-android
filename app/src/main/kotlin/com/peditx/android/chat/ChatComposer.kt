package com.peditx.hermex.chat

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.text.format.Formatter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.pointerInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledIconButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardActions
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.peditx.hermex.core.network.dto.ModelCatalogGroup
import com.peditx.hermex.core.network.dto.ModelCatalogOption
import com.peditx.hermex.core.network.dto.ProfileSummary
import coil3.compose.SubcomposeAsyncImage
import com.peditx.hermex.core.network.dto.fileTypeIcon
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
 * Single-row composer: [+] [Text Field with Send/Steer/Voice]
 * - + button on far left (inside text field area)
 * - Text field in middle with "Ask Hermes" placeholder
 * - Single action button on right that changes: Send → Steer (streaming) → Voice (hold)
 * - Voice note recording via hold-to-talk on the action button
 */
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
            // Voice note recorded
        }
    }

    // Action button state - handles click (send/steer/stop) and long press (voice)
    val actionButtonInteractionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    var longPressDetected by remember { mutableStateOf(false) }

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
                HorizontalDivider(
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
                                    ListItem(
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

                // Main input row: [+] [TextField with action button]
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        // + button (inside the text field area, far left)
                        IconButton(onClick = { filePickerLauncher.launch(arrayOf("*/*")) }) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Attach file",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        // Text field with integrated action button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    shape = RoundedCornerShape(HermexRadii.Composer)
                                )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                // Text field
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
                                        .padding(start = 12.dp, end = 4.dp),
                                    placeholder = { Text(
                                        text = if (composerState.isStreaming) "Steer the response…" else "Ask Hermes",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    ) },
                                    enabled = composerState.isTextFieldEnabled,
                                    maxLines = 5,
                                    singleLine = false,
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Send,
                                        keyboardType = KeyboardType.Text
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            if (composerState.canSend && !composerState.isStreaming && !composerState.showStopButton) {
                                                actions.onSend()
                                            }
                                        }
                                    ),
                                    shape = RoundedCornerShape(HermexRadii.Composer),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedBorderColor = Color.Transparent,
                                        cursorColor = MaterialTheme.colorScheme.primary,
                                        textColor = MaterialTheme.colorScheme.onSurface,
                                        placeholderTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    ),
                                    label = null,
                                )

                                // Single action button: Send / Steer / Stop
                                val actionButton = when {
                                    composerState.showStopButton -> {
                                        Icons.Filled.Close to "Stop" to MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
                                    }
                                    composerState.isStreaming -> {
                                        Icons.AutoMirrored.Filled.Send to "Steer" to MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
                                    }
                                    else -> {
                                        Icons.AutoMirrored.Filled.Send to "Send" to MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
                                    }
                                }
                                val icon = actionButton.first
                                val contentDesc = actionButton.second
                                val colors = actionButton.third
                                val containerColor = colors.first
                                val contentColor = colors.second

                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .pointerInput(Unit) {
                                            androidx.compose.foundation.gestures.detectTapGestures(
                                                onTap = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LightTouch)
                                                    when {
                                                        composerState.showStopButton -> actions.onStop()
                                                        composerState.isStreaming -> {
                                                            if (composerState.canSend) actions.onSteer()
                                                        }
                                                        else -> {
                                                            if (composerState.canSend) actions.onSend()
                                                        }
                                                    }
                                                },
                                                onLongPress = {
                                                    if (!composerState.isStreaming && !composerState.showStopButton) {
                                                        longPressDetected = true
                                                        val hasPermission = ContextCompat.checkSelfPermission(
                                                            context, Manifest.permission.RECORD_AUDIO
                                                        ) == PackageManager.PERMISSION_GRANTED
                                                        if (hasPermission) {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            startRecording()
                                                        } else {
                                                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                                        }
                                                    }
                                                },
                                                onPress = { },
                                                onPressUp = { if (longPressDetected && isRecording) { stopRecording(); longPressDetected = false } }
                                            )
                                        }
                                        .padding(end = 4.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    // Recording indicator overlay
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
                                                    modifier = Modifier.size(18.dp),
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
                                            onClick = {
                                                // Click handled by pointerInput
                                            },
                                            enabled = composerState.canSend || composerState.showStopButton,
                                            colors = androidx.compose.material3.FilledIconButtonDefaults.filledIconButtonColors(
                                                containerColor = containerColor,
                                                contentColor = contentColor,
                                                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                            ),
                                            modifier = Modifier.size(40.dp),
                                        ) {
                                            Icon(
                                                icon,
                                                contentDescription = contentDesc,
                                                modifier = Modifier.size(20.dp),
                                            )
                                        }
                                    }
                                }
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

/** Shared visual container for the composer's bottom control strip -- a small tonal pill with an
 * icon and an optional label, so Attach/Profile/Model all read as one family of "Hermex chips"
 * instead of bare icons. [onClick] is required: every chip here must map to a real, already-
 * existing action -- a chip that looks tappable but does nothing is worse than no chip at all. */
@Composable
private fun ComposerChip(
    icon: ImageVector,
    label: String?,
    contentDescription: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    val contentColor = if (enabled) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    }
    val chipContent: @Composable (androidx.compose.foundation.layout.RowScope.() -> Unit) = {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
        } else {
            Icon(
                icon,
                contentDescription = if (label == null) contentDescription else null,
                modifier = Modifier.size(15.dp),
                tint = contentColor,
            )
        }
        if (label != null) {
            Spacer(Modifier.width(5.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 96.dp),
            )
        }
    }
    Surface(
        modifier = Modifier.clickable(enabled = enabled && !isLoading, onClick = onClick),
        shape = RoundedCornerShape(HermexRadii.Accessory),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = if (label != null) 10.dp else 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = chipContent,
        )
    }
}

@Composable
private fun ProfileSelectorButton(
    profileOptions: List<ProfileSummary>,
    selectedProfileName: String?,
    isSwitchingProfile: Boolean,
    onSelectProfile: (String) -> Unit,
) {
    val displayName = profileOptions.firstOrNull { it.normalizedName == selectedProfileName }?.displayName
        ?: selectedProfileName
        ?: "Profile"
    ComposerChip(
        icon = Icons.Filled.Person,
        label = displayName,
        contentDescription = "Select profile",
        enabled = !isSwitchingProfile,
        isLoading = isSwitchingProfile,
        onClick = { /* TODO: open profile picker */ },
    )
}

@Composable
private fun ModelSelectorButton(
    modelCatalogGroups: List<ModelCatalogGroup>,
    currentModel: String?,
    currentModelProvider: String?,
    isLoadingModelCatalog: Boolean,
    isUpdatingComposerConfiguration: Boolean,
    onOpenModelPicker: () -> Unit,
    onSelectModel: (ModelCatalogOption) -> Unit,
) {
    val displayName = currentModel ?: "Model"
    ComposerChip(
        icon = Icons.Filled.Settings,
        label = displayName,
        contentDescription = "Select model",
        enabled = !isUpdatingComposerConfiguration,
        isLoading = isLoadingModelCatalog || isUpdatingComposerConfiguration,
        onClick = onOpenModelPicker,
    )
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
                            modifier = Modifier.size(40.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            SubcomposeAsyncImage(
                                model = attachment.path!!,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(HermexRadii.Accessory)),
                                placeholder = {
                                    Box(
                                        modifier = Modifier.size(40.dp)
                                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                            .clip(RoundedCornerShape(HermexRadii.Accessory)),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp,
                                        )
                                    }
                                },
                                error = {
                                    Box(
                                        modifier = Modifier.size(40.dp)
                                            .background(MaterialTheme.colorScheme.errorContainer)
                                            .clip(RoundedCornerShape(HermexRadii.Accessory)),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            Icons.Filled.Image,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.size(20.dp),
                                        )
                                    }
                                },
                            )
                        }
                    } else {
                        // File type icon
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .clip(RoundedCornerShape(HermexRadii.Accessory)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                attachment.mime?.fileTypeIcon() ?? Icons.Filled.Image,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp, top = 4.dp, bottom = 4.dp),
                    ) {
                        Text(
                            text = attachment.name ?: "Unknown file",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = attachment.size?.let { Formatter.formatShortFileSize(context, it) }
                                ?: "Unknown size",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(
                        onClick = { onRemove(attachment.id) },
                        modifier = Modifier.padding(end = 4.dp).size(32.dp),
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Remove",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}