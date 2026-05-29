package com.nimitpasricha.pause.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * The single on-device store for all of Beat's state. There is no backend, no
 * account, and no sync — everything lives here, in DataStore Preferences.
 */
val Context.pauseDataStore: DataStore<Preferences> by preferencesDataStore(name = "beat")
