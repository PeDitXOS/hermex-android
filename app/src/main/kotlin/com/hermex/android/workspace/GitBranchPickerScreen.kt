package com.hermex.android.workspace

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Git branch picker — select a branch to checkout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitBranchPickerScreen(
    branches: List<String>,
    currentBranch: String?,
    onBranchSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Switch Branch") },
                navigationIcon = {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(branches, key = { it }) { branch ->
                val isCurrent = branch == currentBranch
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (!isCurrent) onBranchSelected(branch)
                        },
                    color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.surface,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Filled.GitBranch,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = branch,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        if (isCurrent) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = "Current",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}