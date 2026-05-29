package com.nimitpasricha.pause.domain

/**
 * The pause duration policy — the one rule that decides how long the wait is.
 *
 * Escalates by visit count per app, per day:
 *   1st open  -> 15s
 *   2nd open  -> 30s
 *   3rd open  -> 45s
 *   4th+      -> 60s (hard cap)
 *
 * Deliberately deterministic and monotonic: reopening only ever makes the next
 * wait longer, never shorter, and the duration is never randomized (randomness
 * would invite "reroll until I get a short one"). Not user-configurable — these
 * thresholds live in code, on purpose.
 */
object TimerPolicy {

    const val MAX_SECONDS = 60

    // The escalation curve, indexed by visit count. Visits past the last entry
    // stay at the cap. (Starts at 15s — a 5s pause did too little.)
    private val LADDER = intArrayOf(15, 30, 45, 60)

    /**
     * @param visitCount 1-based count of today's opens of this app (the visit
     *   being paused is included — the first open of the day is `1`).
     * @return the pause duration in seconds for this visit.
     */
    fun durationSeconds(visitCount: Int): Int {
        val index = (visitCount - 1).coerceIn(0, LADDER.size - 1)
        return LADDER[index]
    }
}
