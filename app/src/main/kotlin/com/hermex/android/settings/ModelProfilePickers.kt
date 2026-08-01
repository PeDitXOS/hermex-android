package com.hermex.android.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hermex.android.core.network.dto.ModelCatalogGroup

/**
 * Full-screen model picker — iOS DefaultModelPickerView equivalent.
 * Shows available models grouped by provider, with search.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultModelPickerScreen(
    modelGroups: List<ModelCatalogGroup>,
    currentModel: String?,
    onModelSelected: (model: String, provider: String?) -> Unit,
    onDismiss: () -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Default Model") },
                navigationIcon = {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search models...") },
                singleLine = true,
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                modelGroups.forEach { group ->
                                // Provider section header
                                item(key = "header_${group.name}") {
                                    Text(
                                        text = group.name,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(vertical = 8.dp),
                                    )
                                }

                                // Models in this group
                                val filteredModels = group.models.filter {
                                    searchQuery.isBlank() || it.displayName.contains(searchQuery, ignoreCase = true)
                                }
                                items(filteredModels, key = { it.id }) { model ->
                                    val isSelected = model.id == currentModel
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onModelSelected(model.id, group.providerId)
                                                onDismiss()
                                            },
                                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                            else MaterialTheme.colorScheme.surface,
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 12.dp, horizontal = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(
                                                text = model.displayName,
                                                modifier = Modifier.weight(1f),
                                                style = MaterialTheme.typography.bodyLarge,
                                            )
                                            if (isSelected) {
                                                Icon(
                                                    Icons.Filled.Check,
                                                    contentDescription = "Selected",
                                                    tint = MaterialTheme.colorScheme.primary,
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

/**
 * Full-screen profile picker — iOS DefaultProfilePickerView equivalent.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultProfilePickerScreen(
    profiles: List<Pair<String, String>>, // (name, displayName)
    currentProfile: String?,
    onProfileSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Default Profile") },
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
            items(profiles, key = { it.first }) { (name, displayName) ->
                val isSelected = name == currentProfile
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onProfileSelected(name)
                            onDismiss()
                        },
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            else MaterialTheme.colorScheme.surface,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = displayName.ifBlank { name },
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        if (isSelected) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}
