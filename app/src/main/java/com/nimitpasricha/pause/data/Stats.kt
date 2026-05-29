package com.nimitpasricha.pause.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/** The headline win count and a simple "days you showed up" streak. */
data class WinStats(
    val backedOutTotal: Int,
    val streakDays: Int,
)

/**
 * The only thing Beat counts: how often the user *backed out* of an app, plus a
 * gentle streak of consecutive days they did so at least once. Deliberately not
 * a usage dashboard — there is nothing here to compulsively check.
 */
class Stats(
    private val context: Context,
    private val today: () -> LocalDate = { LocalDate.now() },
) {

    val stats: Flow<WinStats> = context.pauseDataStore.data.map { prefs ->
        WinStats(
            backedOutTotal = prefs[BACKED_OUT] ?: 0,
            streakDays = prefs[STREAK_DAYS] ?: 0,
        )
    }

    /**
     * Records a "backed out" win and updates the streak. The streak counts
     * consecutive calendar days with at least one back-out: same day → no
     * change, the next day → +1, a gap → reset to 1.
     */
    suspend fun recordBackedOut() {
        context.pauseDataStore.edit { prefs ->
            prefs[BACKED_OUT] = (prefs[BACKED_OUT] ?: 0) + 1

            val now = today()
            val last = prefs[STREAK_LAST_DATE]?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            val streak = prefs[STREAK_DAYS] ?: 0
            prefs[STREAK_DAYS] = when (last) {
                now -> streak.coerceAtLeast(1)
                now.minusDays(1) -> streak + 1
                else -> 1
            }
            prefs[STREAK_LAST_DATE] = now.toString()
        }
    }

    private companion object {
        val BACKED_OUT = intPreferencesKey("backed_out_total")
        val STREAK_DAYS = intPreferencesKey("streak_days")
        val STREAK_LAST_DATE = stringPreferencesKey("streak_last_date")
    }
}
