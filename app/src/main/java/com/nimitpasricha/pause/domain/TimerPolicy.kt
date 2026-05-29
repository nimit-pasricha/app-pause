package com.nimitpasricha.pause.domain

/**
 * The pause duration policy — the one rule that decides how long the wait is.
 *
 * Escalates by visit count per app, per day: the Nth open of the day waits
 * `N * 15s`, climbing 15s each reopen until it caps:
 *   1st open  -> 15s
 *   2nd open  -> 30s
 *   3rd open  -> 45s
 *   …
 *   20th+     -> 300s (5-minute hard cap)
 *
 * Deliberately deterministic and monotonic: reopening only ever makes the next
 * wait longer, never shorter, and the duration is never randomized (randomness
 * would invite "reroll until I get a short one"). Not user-configurable — these
 * thresholds live in code, on purpose.
 */
object TimerPolicy {

    /** The hard cap: a 5-minute pause. */
    const val MAX_SECONDS = 300

    /** How much each reopen of the day adds to the wait. */
    const val STEP_SECONDS = 15

    /**
     * @param visitCount 1-based count of today's opens of this app (the visit
     *   being paused is included — the first open of the day is `1`).
     * @return the pause duration in seconds for this visit.
     */
    fun durationSeconds(visitCount: Int): Int =
        (visitCount.coerceAtLeast(1) * STEP_SECONDS).coerceAtMost(MAX_SECONDS)
}
