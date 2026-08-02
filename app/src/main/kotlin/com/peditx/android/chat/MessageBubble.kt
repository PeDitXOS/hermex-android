package com.peditx.hermex.chat

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.peditx.hermex.core.network.dto.ChatMessage
import com.peditx.hermex.core.network.dto.MessageAttachment
import com.peditx.hermex.core.network.dto.attachmentsForDisplay
import com.peditx.hermex.core.network.dto.fileTypeIcon
import com.peditx.hermex.core.network.dto.stripAttachedFilesMarker
import com.peditx.hermex.chat.AttachmentFileOpener
import com.peditx.hermex.chat.MarkdownText
import com.peditx.hermex.ui.theme.HermexRadii
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** What "copy message" actually puts on the clipboard */
fun copyableTextFor(message: ChatMessage): String = message.content.orEmpty()

/**
 * Per-message bubble with a context menu (long-press) offering:
 *  - Copy (all messages)
 *  - Edit (user messages only, when allowed)
 *  - Fork From Here (all messages)
 *  - Regenerate (assistant messages only)
 *  - Listen (assistant messages only, TTS)
 *  - Select Text (all messages)
 *
 * Matches iOS ChatMessageActionMenu semantics.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
    sessionId: String? = null,
    serverBaseUrl: String? = null,
    /** Non-null only for the message this action currently applies to. */
    onEdit: (() -> Unit)? = null,
    onRegenerate: (() -> Unit)? = null,
    /** Fork from this message index. */
    onFork: (() -> Unit)? = null,
    /** Listen to this message via TTS. */
    onListen: (() -> Unit)? = null,
    /** Whether the message is currently being listened to. */
    isListening: Boolean = false,
) {
    val isUser = message.role == "user"
    val isAssistant = message.role == "assistant"
    val displayContent = stripAttachedFilesMarker(message.content)
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var showImageViewer by remember { mutableStateOf<String?>(null) }
    var showContextMenu by remember { mutableStateOf(false) }
    val hasCodeBlocks = remember(displayContent) {
        displayContent?.contains("```") == true
    }

    val contextMenuModifier = Modifier.combinedClickable(
        onClick = {},
        onLongClick = { showContextMenu = true },
    )

    Box(modifier = modifier) {
        if (isUser) {
            Box(modifier = Modifier.fillMaxWidth().padding(start = 32.dp)) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .widthIn(max = 320.dp),
                    horizontalAlignment = Alignment.End,
                ) {
                    MessageAttachments(
                        message = message,
                        context = context,
                        sessionId = sessionId,
                        serverBaseUrl = serverBaseUrl,
                        onOpenImage = { showImageViewer = it },
                    )
                    if (!displayContent.isNullOrBlank()) {
                        Surface(
                            modifier = contextMenuModifier,
                            shape = RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            tonalElevation = 1.dp,
                        ) {
                            Text(
                                text = displayContent,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(contextMenuModifier),
            ) {
                MessageAttachments(
                    message = message,
                    context = context,
                    sessionId = sessionId,
                    serverBaseUrl = serverBaseUrl,
                    onOpenImage = { showImageViewer = it },
                )
                if (!displayContent.isNullOrBlank()) {
                    MarkdownText(
                        markdown = displayContent.orEmpty(),
                        textColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    )
                }
                // Copy Code button for messages with code blocks
                if (hasCodeBlocks && isAssistant) {
                    TextButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(displayContent.orEmpty()))
                            Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.padding(start = 4.dp),
                    ) {
                        Icon(
                            Icons.Filled.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Copy Code", style = MaterialTheme.typography.labelSmall)
                    }
                }
                // Timestamp for assistant messages
                message.timestamp?.let { ts ->
                    val timeStr = remember(ts) {
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date((ts * 1000).toLong()))
                    }
                    Text(
                        text = timeStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp),
                    )
                }
            }
        }

        // Context menu (long-press dropdown)
        DropdownMenu(
            expanded = showContextMenu,
            onDismissRequest = { showContextMenu = false },
        ) {
            // Copy — all messages
            DropdownMenuItem(
                text = { Text("Copy") },
                onClick = {
                    clipboardManager.setText(AnnotatedString(copyableTextFor(message)))
                    Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                    showContextMenu = false
                },
                leadingIcon = { Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(20.dp)) },
            )

            // Edit — user messages only, when enabled
            if (isUser && onEdit != null) {
                DropdownMenuItem(
                    text = { Text("Edit Message") },
                    onClick = {
                        showContextMenu = false
                        onEdit()
                    },
                    leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(20.dp)) },
                )
            }

            // Fork From Here — all messages
            if (onFork != null) {
                DropdownMenuItem(
                    text = { Text("Fork From Here") },
                    onClick = {
                        showContextMenu = false
                        onFork()
                    },
                    leadingIcon = { Icon(Icons.Filled.AccountTree, contentDescription = null, modifier = Modifier.size(20.dp)) },
                )
            }

            // Regenerate — assistant messages only
            if (isAssistant && onRegenerate != null) {
                DropdownMenuItem(
                    text = { Text("Regenerate Response") },
                    onClick = {
                        showContextMenu = false
                        onRegenerate()
                    },
                    leadingIcon = { Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(20.dp)) },
                )
            }

            // Listen / TTS — assistant messages only
            if (isAssistant && onListen != null) {
                DropdownMenuItem(
                    text = { Text(if (isListening) "Stop Listening" else "Listen") },
                    onClick = {
                        showContextMenu = false
                        onListen()
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Speaker,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                    },
                )
            }

            // Select Text — all messages
            DropdownMenuItem(
                text = { Text("Select Text") },
                onClick = {
                    clipboardManager.setText(AnnotatedString(copyableTextFor(message)))
                    Toast.makeText(context, "Text selected and copied", Toast.LENGTH_SHORT).show()
                    showContextMenu = false
                },
                leadingIcon = { Icon(Icons.Filled.TextSnippet, contentDescription = null, modifier = Modifier.size(20.dp)) },
            )
        }
    }

    // Image viewer
    showImageViewer?.let { imageUrl ->
        ImageViewer(imageUrl = imageUrl, onDismiss = { showImageViewer = null })
    }
}

/** Full-width streaming bubble (not yet finalized into a [ChatMessage]). */
@Composable
fun StreamingBubble(text: String) {
    if (text.isBlank()) return
    MarkdownText(
        markdown = text,
        textColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
    )
}

@Composable
private fun MessageAttachments(
    message: ChatMessage,
    context: android.content.Context,
    sessionId: String?,
    serverBaseUrl: String?,
    onOpenImage: (String) -> Unit,
) {
    val attachments = remember(message) { message.attachmentsForDisplay() }
    if (attachments.isEmpty()) return

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(bottom = 4.dp),
    ) {
        items(attachments, key = { it.name ?: it.path ?: it.hashCode().toString() }) { attachment ->
            AttachmentPreview(
                attachment = attachment,
                context = context,
                sessionId = sessionId,
                serverBaseUrl = serverBaseUrl,
                onClick = {
                    if (attachment.isImage == true) {
                        val url = buildString {
                            append(serverBaseUrl.orEmpty())
                            append("/api/file/raw?session_id=")
                            append(sessionId.orEmpty())
                            append("&path=")
                            append(attachment.path ?: attachment.name.orEmpty())
                        }
                        onOpenImage(url)
                    } else {
                        AttachmentFileOpener.open(context, attachment)
                    }
                },
            )
        }
    }
}

@Composable
private fun AttachmentPreview(
    attachment: MessageAttachment,
    context: android.content.Context,
    sessionId: String?,
    serverBaseUrl: String?,
    onClick: () -> Unit,
) {
    val iconRes = fileTypeIcon(attachment.mime)
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.size(width = 120.dp, height = 80.dp),
    ) {
        if (attachment.isImage == true && serverBaseUrl != null && sessionId != null) {
            val imageUrl = buildString {
                append(serverBaseUrl)
                append("/api/file/raw?session_id=")
                append(sessionId)
                append("&path=")
                append(attachment.path ?: attachment.name.orEmpty())
            }
            AsyncImage(
                model = imageUrl,
                contentDescription = attachment.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .size(width = 120.dp, height = 80.dp),
            )
        } else {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = attachment.name ?: "File",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun ChatJumpButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
        tonalElevation = 2.dp,
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            modifier = Modifier.padding(8.dp).size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
