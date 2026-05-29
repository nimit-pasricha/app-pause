package com.nimitpasricha.pause.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.nimitpasricha.pause.domain.DailyVisits
import kotlinx.coroutines.flow.first
import java.time.LocalDate

/**
 * Persists per-app, per-day visit counts and applies the daily reset. The pure
 * counting/rollover logic lives in [DailyVisits]; this class only stores it and
 * supplies "today".
 *
 * Counts are encoded as a string set of `"packageNamecount"` entries so we
 * avoid pulling in a JSON dependency for such a small payload.
 */
class VisitTracker(
    private val context: Context,
    private val today: () -> String = { LocalDate.now().toString() },
) {

    /** Records one visit to [packageName] today and returns the new count. */
    suspend fun recordVisit(packageName: String): Int {
        var newCount = 0
        context.pauseDataStore.edit { prefs ->
            val ledger = DailyVisits(
                day = prefs[DAY_KEY] ?: "",
                counts = decode(prefs[COUNTS_KEY] ?: emptySet()),
            )
            val (updated, count) = ledger.recordVisit(today(), packageName)
            prefs[DAY_KEY] = updated.day
            prefs[COUNTS_KEY] = encode(updated.counts)
            newCount = count
        }
        return newCount
    }

    /** The current count for [packageName] today, without recording a visit. */
    suspend fun currentCount(packageName: String): Int {
        val prefs = context.pauseDataStore.data.first()
        val ledger = DailyVisits(
            day = prefs[DAY_KEY] ?: "",
            counts = decode(prefs[COUNTS_KEY] ?: emptySet()),
        )
        return ledger.countFor(today(), packageName)
    }

    private fun encode(counts: Map<String, Int>): Set<String> =
        counts.map { (pkg, n) -> "$pkg$SEP$n" }.toSet()

    private fun decode(raw: Set<String>): Map<String, Int> =
        raw.mapNotNull { entry ->
            val i = entry.lastIndexOf(SEP)
            if (i < 0) return@mapNotNull null
            val pkg = entry.substring(0, i)
            val n = entry.substring(i + 1).toIntOrNull() ?: return@mapNotNull null
            pkg to n
        }.toMap()

    private companion object {
        const val SEP = ''
        val DAY_KEY = stringPreferencesKey("visits_day")
        val COUNTS_KEY = stringSetPreferencesKey("visits_counts")
    }
}
