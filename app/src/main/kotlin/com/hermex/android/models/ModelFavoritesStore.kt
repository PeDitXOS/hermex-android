package com.hermex.android.models

import android.content.Context
import android.content.SharedPreferences

/**
 * Per-server model favorites. Stored in SharedPreferences.
 */
class ModelFavoritesStore(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("model_favorites", Context.MODE_PRIVATE)

    fun getFavorites(serverId: String): Set<String> {
        return prefs.getStringSet(serverId, emptySet()) ?: emptySet()
    }

    fun toggleFavorite(serverId: String, modelName: String) {
        val current = getFavorites(serverId).toMutableSet()
        if (current.contains(modelName)) current.remove(modelName) else current.add(modelName)
        prefs.edit().putStringSet(serverId, current).apply()
    }

    fun isFavorite(serverId: String, modelName: String): Boolean {
        return getFavorites(serverId).contains(modelName)
    }
}
