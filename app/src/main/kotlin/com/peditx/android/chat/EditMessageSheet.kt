package com.peditx.hermex.chat

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Bottom sheet for editing a user message — iOS EditMessageSheet equivalent.
 * Shows a TextEditor with the original text, a warning about discarding later messages,
 * and Cancel/Send buttons.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMessageSheet(
    originalText: String,
    messagesAfterCount: Int,
    onConfirm: (newText: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var editText by remember { mutableStateOf(originalText) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            Text(
                text = "Edit Message",
                style = MaterialTheme.typography.titleMedium,
            )

            if (messagesAfterCount > 0) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "⚠ Editing will discard $messagesAfterCount later message${if (messagesAfterCount > 1) "s" else ""}.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = editText,
                onValueChange = { editText = it },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 8,
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { onConfirm(editText) },
                    enabled = editText.isNotBlank() && editText != originalText,
                ) { Text("Send") }
            }
        }
    }
}
