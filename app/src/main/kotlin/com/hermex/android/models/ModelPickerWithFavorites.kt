package com.hermex.android.models

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hermex.android.core.network.dto.ModelCatalogGroup

/**
 * Model picker with favorites support — iOS model picker with favorites section.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelPickerWithFavorites(
    modelGroups: List<ModelCatalogGroup>,
    currentModel: String?,
    favorites: Set<String>,
    onModelSelected: (model: String, provider: String?) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    var showFavoritesOnly by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Model") },
                navigationIcon = {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Search + favorites toggle
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search models...") },
                    singleLine = true,
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = { showFavoritesOnly = !showFavoritesOnly }) {
                    Icon(
                        if (showFavoritesOnly) Icons.Filled.Star else Icons.Filled.StarOutline,
                        contentDescription = "Favorites",
                        tint = if (showFavoritesOnly) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp)) {
                // Favorites section
                if (favorites.isNotEmpty() && !showFavoritesOnly) {
                    item {
                        Text("Favorites", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp))
                    }
                    modelGroups.forEach { group ->
                        group.models.filter { it.name in favorites }.filter { searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) }.forEach { model ->
                            item(key = "fav_${model.name}") {
                                ModelRow(model.name, group.provider, model.name == currentModel, true, onModelSelected, onToggleFavorite)
                            }
                        }
                    }
                }

                // All models by provider
                modelGroups.forEach { group ->
                    val filteredModels = group.models.filter {
                        (searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true)) &&
                        (!showFavoritesOnly || it.name in favorites)
                    }
                    if (filteredModels.isNotEmpty()) {
                        item(key = "header_${group.provider}") {
                            Text(group.provider, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp))
                        }
                        items(filteredModels, key = { it.name }) { model ->
                            ModelRow(model.name, group.provider, model.name == currentModel, model.name in favorites, onModelSelected, onToggleFavorite)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelRow(
    name: String,
    provider: String,
    isSelected: Boolean,
    isFavorite: Boolean,
    onModelSelected: (String, String?) -> Unit,
    onToggleFavorite: (String) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onModelSelected(name, provider) },
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            IconButton(onClick = { onToggleFavorite(name) }, modifier = Modifier.size(32.dp)) {
                Icon(
                    if (isFavorite) Icons.Filled.Star else Icons.Filled.StarOutline,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
            }
            if (isSelected) {
                Icon(Icons.Filled.Star, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            }
        }
    }
}
