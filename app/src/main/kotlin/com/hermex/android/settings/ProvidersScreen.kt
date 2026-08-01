package com.hermex.android.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Provider status info — which AI providers are configured and healthy.
 */
data class ProviderStatus(
    val name: String,
    val isConfigured: Boolean,
    val isHealthy: Boolean? = null, // null = unknown
    val modelCount: Int = 0,
)

/**
 * Providers screen — iOS ProvidersView equivalent.
 * Shows configured AI providers and their status.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvidersScreen(
    providers: List<ProviderStatus>,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Providers") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(providers, key = { it.name }) { provider ->
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = provider.name,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(
                                text = if (provider.isConfigured) "${provider.modelCount} models" else "Not configured",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Icon(
                            imageVector = when {
                                provider.isHealthy == true -> Icons.Filled.CheckCircle
                                provider.isHealthy == false -> Icons.Filled.Error
                                else -> Icons.Filled.Help
                            },
                            contentDescription = null,
                            tint = when {
                                provider.isHealthy == true -> MaterialTheme.colorScheme.primary
                                provider.isHealthy == false -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                }
            }
        }
    }
}
