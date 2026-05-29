package com.nimitpasricha.pause.domain

/**
 * Pure, side-effect-free logic for per-app, per-day visit counting.
 *
 * Kept free of Android/DataStore types so it can be unit-tested on the JVM.
 * [VisitTracker] persists this and supplies the current date.
 *
 * @property day the calendar day these counts belong to, as an ISO `yyyy-MM-dd`
 *   string. Used to detect a daily rollover.
 * @property counts visit count per package name, for [day].
 */
data class DailyVisits(
    val day: String,
    val counts: Map<String, Int>,
) {
    /**
     * Records one visit to [packageName] on [today], returning the updated
     * ledger and the new count for that app.
     *
     * If [today] differs from [day], the whole ledger resets first (fresh start
     * each morning) — so the returned count starts again at 1.
     */
    fun recordVisit(today: String, packageName: String): Pair<DailyVisits, Int> {
        val base = if (today == day) counts else emptyMap()
        val newCount = (base[packageName] ?: 0) + 1
        val updated = DailyVisits(day = today, counts = base + (packageName to newCount))
        return updated to newCount
    }

    /** The current count for [packageName] on [today] (0 after a rollover). */
    fun countFor(today: String, packageName: String): Int =
        if (today == day) counts[packageName] ?: 0 else 0

    companion object {
        val EMPTY = DailyVisits(day = "", counts = emptyMap())
    }
}
