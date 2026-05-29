package com.nimitpasricha.pause.service

import java.util.concurrent.ConcurrentHashMap

/**
 * A tiny shared gate between [PauseActivity][com.nimitpasricha.pause.ui.PauseActivity]
 * and [PauseAccessibilityService].
 *
 * When the user taps "Open anyway", the activity marks that package as allowed
 * here; the service then lets the user into it without re-pausing. The
 * allowance is cleared once the user navigates away to a different app, so the
 * *next* fresh open pauses again (the grace is for this session only — it must
 * not become a loophole).
 */
object PauseGate {

    private val allowed = ConcurrentHashMap.newKeySet<String>()

    /** Mark [packageName] as allowed through (called from "Open anyway"). */
    fun allow(packageName: String) {
        allowed.add(packageName)
    }

    /** Whether [packageName] is currently allowed through without a pause. */
    fun isAllowed(packageName: String): Boolean = allowed.contains(packageName)

    /** Drop the allowance for [packageName] (called when the user leaves it). */
    fun clear(packageName: String) {
        allowed.remove(packageName)
    }
}
