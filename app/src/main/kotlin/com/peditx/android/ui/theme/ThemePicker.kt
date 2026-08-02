package com.peditx.hermex.ui.theme

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

enum class AppTheme(val label: String) {
    System("System Default"),
    Light("Light"),
    Dark("Dark"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePickerScreen(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    onDismiss: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Theme") },
                navigationIcon = {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
        ) {
            items(AppTheme.entries, key = { it.name }) { theme ->
                val isSelected = theme == currentTheme
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onThemeSelected(theme)
                            onDismiss()
                        },
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            else MaterialTheme.colorScheme.surface,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            when (theme) {
                                AppTheme.System -> Icons.Filled.SettingsBrightness
                                AppTheme.Light -> Icons.Filled.LightMode
                                AppTheme.Dark -> Icons.Filled.DarkMode
                            },
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(theme.label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                        if (isSelected) Icon(Icons.Filled.Check, null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
