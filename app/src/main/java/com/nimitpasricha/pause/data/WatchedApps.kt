package com.nimitpasricha.pause.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * The set of app package names the user has chosen to watch — the only thing
 * the user configures in the whole app.
 */
class WatchedApps(private val context: Context) {

    val watched: Flow<Set<String>> =
        context.pauseDataStore.data.map { it[KEY] ?: emptySet() }

    suspend fun isWatched(packageName: String): Boolean =
        watched.first().contains(packageName)

    suspend fun setWatched(packageName: String, watched: Boolean) {
        context.pauseDataStore.edit { prefs ->
            val current = prefs[KEY] ?: emptySet()
            prefs[KEY] = if (watched) current + packageName else current - packageName
        }
    }

    private companion object {
        val KEY = stringSetPreferencesKey("watched_apps")
    }
}
