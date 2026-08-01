package com.hermex.android.workspace

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Git commit screen — enter commit message and commit staged changes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitCommitScreen(
    sessionId: String,
    onCommit: (message: String) -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
) {
    var commitMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Git Commit") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onCommit(commitMessage) },
                        enabled = commitMessage.isNotBlank() && !isLoading,
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = "Commit")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            OutlinedTextField(
                value = commitMessage,
                onValueChange = { commitMessage = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Commit message") },
                placeholder = { Text("Describe your changes...") },
                minLines = 3,
                maxLines = 8,
            )

            Spacer(Modifier.height(16.dp))

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            errorMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { onCommit(commitMessage) },
                modifier = Modifier.fillMaxWidth(),
                enabled = commitMessage.isNotBlank() && !isLoading,
            ) {
                Text("Commit")
            }
        }
    }
}
