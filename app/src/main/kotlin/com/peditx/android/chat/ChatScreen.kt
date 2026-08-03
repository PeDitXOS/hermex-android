package com.peditx.hermex.chat

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peditx.hermex.core.notifications.HermexNotificationRoutes
import com.peditx.hermex.core.util.HermexLog
import com.peditx.hermex.core.util.TtftTracer
import com.peditx.hermex.navigation.LocalHermexDrawerOpener
import com.peditx.hermex.sessions.DeleteSessionDialog
import com.peditx.hermex.sessions.MoveToProjectDialog
import com.peditx.hermex.sessions.RenameSessionDialog
import com.peditx.hermex.ui.theme.HermexErrorBanner
import com.peditx.hermex.ui.theme.HermexRadii
import com.peditx.hermex.chat.WithRtlSupport
import com.peditx.hermex.chat.MarkdownText
import com.peditx.hermex.chat.shareSession
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onBack: () -> Unit,
    onSwitchedSession: (String) -> Unit,
    onOpenWorkspace: () -> Unit,
    modifier: Modifier = Modifier,
    initialComposerDraft: String? = null,
    pendingFileUploadUris: List<String>? = null,
    isPaneMode: Boolean = false,
    sessionId: String? = null,
    serverBaseUrl: String? = null,
    sessionTitle: String? = null,
    onRenameSession: ((String) -> Unit)? = null,
    onDeleteSession: (() -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val openDrawer = LocalHermexDrawerOpener.current
    val context = LocalContext.current
    var showSessionMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMoveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(initialComposerDraft) {
        initialComposerDraft?.let(viewModel::stageDraftIfComposerEmpty)
    }

    LaunchedEffect(pendingFileUploadUris) {
        val uriStrings = pendingFileUploadUris ?: return@LaunchedEffect
        val uris = uriStrings.mapNotNull { android.net.Uri.parse(it) }
            .filter { it != android.net.Uri.EMPTY }
        if (uris.isNotEmpty()) {
            viewModel.uploadAttachmentsSequentially(uris)
        }
    }

    uiState.pendingProfileSwitch?.let { profileName ->
        val displayName = uiState.profileOptions.firstOrNull { it.normalizedName == profileName }?.displayName ?: profileName
        AlertDialog(
            onDismissRequest = viewModel::dismissPendingProfileSwitch,
            shape = RoundedCornerShape(HermexRadii.Dialog),
            title = { Text("Start a new session?") },
            text = { Text("Switching to \"$displayName\" starts a new session on that profile. This chat's history stays here.") },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmPendingProfileSwitch(onSwitchedSession) }) {
                    Text("Start New Session")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissPendingProfileSwitch) { Text("Cancel") }
            },
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    if (!isPaneMode) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Surface(
                                shape = RoundedCornerShape(HermexRadii.Accessory),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                tonalElevation = 2.dp,
                            ) {
                                Text(
                                    text = "Hermes Agent",
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { openDrawer() }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Open menu")
                    }
                },
                actions = {
                    IconButton(onClick = onOpenWorkspace) {
                        Icon(Icons.Filled.RemoveRedEye, contentDescription = "Eye")
                    }
                    IconButton(onClick = onOpenWorkspace) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Files")
                    }
                    IconButton(onClick = viewModel::loadSession) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                    Box {
                        IconButton(onClick = { showSessionMenu = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(expanded = showSessionMenu, onDismissRequest = { showSessionMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Rename") },
                                onClick = { showSessionMenu = false; showRenameDialog = true },
                                leadingIcon = { Icon(Icons.Filled.Edit, null) },
                            )
                            DropdownMenuItem(
                                text = { Text("Move to Project") },
                                onClick = {
                                    showSessionMenu = false
                                    showMoveDialog = true
                                    viewModel.loadProjects()
                                },
                                leadingIcon = { Icon(Icons.Filled.Folder, null) },
                            )
                            DropdownMenuItem(
                                text = { Text("Share") },
                                onClick = {
                                    showSessionMenu = false
                                    sessionId?.let { shareSession(context, it, sessionTitle ?: "Session") }
                                },
                                leadingIcon = { Icon(Icons.Filled.Share, null) },
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                                onClick = { showSessionMenu = false; showDeleteDialog = true },
                                leadingIcon = { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) },
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
        bottomBar = {
            uiState.showRetryHint?.let { hint ->
                Text(
                    text = hint,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp, bottom = 4.dp),
                )
            }
            ChatComposer(
                composerState = ChatComposerState.from(uiState),
                profileSelectorState = ChatComposerProfileSelectorState.from(uiState),
                modelSelectorState = ChatComposerModelSelectorState.from(uiState),
                attachmentState = ChatComposerAttachmentState.from(uiState),
                actions = ChatComposerActions(
                    onTextChanged = viewModel::onComposerTextChanged,
                    onSend = viewModel::sendMessage,
                    onSteer = viewModel::steerMessage,
                    onStop = viewModel::cancelStream,
                    onSelectProfile = viewModel::selectProfile,
                    onOpenModelPicker = viewModel::refreshModelCatalogForPickerOpen,
                    onSelectModel = viewModel::selectComposerModel,
                    onAttachFile = viewModel::uploadAttachment,
                    onRemoveAttachment = viewModel::removePendingAttachment,
                    onSendVoiceNote = viewModel::sendVoiceNote,
                    onRefresh = viewModel::loadSession,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            uiState.cacheStatusMessage?.let { message ->
                Surface(
                    shape = RoundedCornerShape(HermexRadii.Accessory),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
            ) {
                if (uiState.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val listState = rememberLazyListState()
                    val totalItems = uiState.messages.size + uiState.activeToolCalls.size +
                        (if (uiState.streamingText.isNotEmpty()) 1 else 0) +
                        (if (uiState.streamingReasoning.isNotEmpty()) 1 else 0) +
                        (if (uiState.isStreaming) 1 else 0)

                    var stickToBottom by remember { mutableStateOf(true) }
                    val coroutineScope = rememberCoroutineScope()

                    LaunchedEffect(listState) {
                        var userIsScrolling = false
                        launch {
                            listState.interactionSource.interactions.collect { interaction ->
                                if (interaction is DragInteraction.Start) userIsScrolling = true
                            }
                        }
                        launch {
                            snapshotFlow { listState.isScrollInProgress }.collect { scrolling ->
                                if (!scrolling && userIsScrolling) {
                                    userIsScrolling = false
                                    stickToBottom = !listState.canScrollForward
                                }
                            }
                        }
                    }

                    LaunchedEffect(uiState.isStreaming) {
                        if (uiState.isStreaming && !listState.canScrollForward) {
                            stickToBottom = true
                        }
                    }

                    LaunchedEffect(stickToBottom) {
                        HermexLog.d("ChatScroll", "stickToBottom=$stickToBottom")
                    }

                    LaunchedEffect(totalItems, stickToBottom) {
                        if (totalItems > 0 && stickToBottom) {
                            listState.scrollToItem(totalItems - 1)
                            var attempts = 0
                            while (attempts < 10) {
                                withFrameNanos {}
                                attempts++
                                if (!listState.canScrollForward) break
                                listState.scrollBy(Float.MAX_VALUE)
                            }
                        }
                    }

                    LaunchedEffect(uiState.streamingText.isNotEmpty()) {
                        if (uiState.streamingText.isNotEmpty()) {
                            withFrameNanos {}
                            TtftTracer.markOnce("First token rendered in Compose")
                        }
                    }

                    var lastScrollMs by remember { mutableLongStateOf(0L) }
                    LaunchedEffect(uiState.streamingText) {
                        if (stickToBottom && uiState.isStreaming) {
                            val now = System.currentTimeMillis()
                            if (now - lastScrollMs > 100) {
                                lastScrollMs = now
                                listState.scrollToItem(totalItems - 1)
                                listState.scrollBy(Float.MAX_VALUE)
                            }
                        }
                    }

                    val toolCallsByAnchor = uiState.activeToolCalls.groupBy { it.anchorMessageCount }

                    WithRtlSupport(enabled = uiState.rtlChatLayoutEnabled) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            val canMutateHistory = !uiState.isSending && !uiState.isStreaming
                            
                            // Show "Start chatting" when no messages
                            if (uiState.messages.isEmpty() && !uiState.isStreaming && uiState.streamingText.isEmpty()) {
                                item(key = "start-chatting") {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "Start chatting",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        )
                                    }
                                }
                            }
                            
                            uiState.messages.forEachIndexed { index, message ->
                                toolCallsByAnchor[index]?.forEach { toolCall ->
                                    item(key = toolCall.stableId) {
                                        ToolCallCard(toolCall, initiallyExpanded = uiState.expandToolCallsByDefault)
                                    }
                                }
                                val historicalToolCall = message.toHistoricalToolCallUi()
                                item(key = message.stableId) {
                                    if (historicalToolCall != null) {
                                        ToolCallCard(historicalToolCall, initiallyExpanded = uiState.expandToolCallsByDefault)
                                    } else {
                                        MessageBubble(
                                            message = message,
                                            sessionId = sessionId,
                                            serverBaseUrl = serverBaseUrl,
                                            onEdit = if (canMutateHistory && message.role == "user") {
                                                { viewModel.editMessage(index) }
                                            } else null,
                                            onRegenerate = if (canMutateHistory && message.role != "user" && index == uiState.messages.lastIndex) {
                                                viewModel::regenerate
                                            } else null,
                                            onFork = if (canMutateHistory) {
                                                { viewModel.forkFromMessage() }
                                            } else null,
                                            onListen = if (message.role == "assistant") {
                                                { viewModel.toggleListen(index) }
                                            } else null,
                                        )
                                    }
                                }
                            }
                            if (uiState.streamingReasoning.isNotEmpty()) {
                                item(key = "streaming-reasoning") {
                                    ReasoningBlock(uiState.streamingReasoning, initiallyExpanded = uiState.expandThinkingByDefault)
                                }
                            }
                            toolCallsByAnchor[uiState.messages.size]?.forEach { toolCall ->
                                item(key = toolCall.stableId) {
                                    ToolCallCard(toolCall, initiallyExpanded = uiState.expandToolCallsByDefault)
                                }
                            }
                            if (uiState.streamingText.isNotEmpty()) {
                                item(key = "streaming-text") { StreamingBubble(uiState.streamingText) }
                            }
                            if (uiState.isStreaming) {
                                item(key = "streaming-status") {
                                    Text(
                                        text = "Generating…",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                    )
                                }
                            }
                        }
                    }

                    val canJumpToTop by remember { derivedStateOf { listState.canScrollBackward } }
                    val canJumpToBottom by remember { derivedStateOf { listState.canScrollForward } }

                    if (canJumpToTop) {
                        Box(Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.TopEnd) {
                            ChatJumpButton(
                                icon = Icons.Filled.KeyboardArrowUp,
                                contentDescription = "Jump to oldest message",
                                onClick = { coroutineScope.launch { listState.scrollToItem(0) } },
                            )
                        }
                    }
                    if (canJumpToBottom) {
                        Box(Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.BottomEnd) {
                            ChatJumpButton(
                                icon = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Jump to newest message",
                                onClick = { coroutineScope.launch { listState.scrollToItem(totalItems - 1) } },
                            )
                        }
                    }
                }
            }
        }
    }
}
}