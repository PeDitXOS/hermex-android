package com.hermex.android.workspace

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Git actions menu — commit, push, pull, checkout, discard.
 */
@Composable
fun GitActionsMenu(
    onCommit: () -> Unit,
    onPush: () -> Unit,
    onPull: () -> Unit,
    onCheckout: () -> Unit,
    onDiscard: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        GitActionRow(Icons.Filled.CheckCircle, "Commit", "Commit staged changes", onCommit)
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        GitActionRow(Icons.Filled.Upload, "Push", "Push commits to remote", onPush)
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        GitActionRow(Icons.Filled.Download, "Pull", "Pull latest from remote", onPull)
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        GitActionRow(Icons.Filled.SwapHoriz, "Checkout Branch", "Switch to a different branch", onCheckout)
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        GitActionRow(Icons.Filled.Delete, "Discard Changes", "Discard uncommitted changes", onDiscard, isDestructive = true)
    }
}

@Composable
private fun GitActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (isDestructive) MaterialTheme.colorScheme.error
                   else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDestructive) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
