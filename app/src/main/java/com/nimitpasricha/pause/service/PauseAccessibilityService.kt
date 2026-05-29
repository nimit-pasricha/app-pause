package com.nimitpasricha.pause.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import com.nimitpasricha.pause.data.WatchedApps
import com.nimitpasricha.pause.ui.PauseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Watches which app is in the foreground and, when it's one the user chose to
 * watch, launches the [PauseActivity] over it.
 *
 * The hard part is firing exactly once per *fresh* open — not on every window
 * blip, not when the keyboard pops up mid-session, not right after the user
 * chose "Open anyway". We track the current foreground *app* and only act on a
 * real switch into a watched app.
 *
 * "Real" means: any package that isn't transient. We ignore our own screens,
 * the keyboard/IME, and the system UI shade — but crucially we do NOT ignore
 * the home launcher (many launchers have no app-drawer entry, so an
 * "is it launchable" test would skip them, leaving the tracker stuck on the
 * watched app and never re-pausing after the user goes home).
 */
class PauseAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(SupervisorJob())

    /** Snapshot of watched packages, kept current by collecting the DataStore flow. */
    @Volatile
    private var watched: Set<String> = emptySet()

    /** The real app currently in the foreground (null until the first switch). */
    @Volatile
    private var foregroundApp: String? = null

    /** The active keyboard's package, so its windows don't count as "leaving". */
    @Volatile
    private var imePackage: String? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        imePackage = currentImePackage()
        WatchedApps(applicationContext).watched
            .onEach { watched = it }
            .launchIn(scope)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return

        // Ignore transient windows that aren't a real app coming forward: our
        // own screens, the keyboard, and the system UI shade. Everything else —
        // including the home launcher and other apps — counts as a foreground
        // change.
        if (isTransientWindow(pkg)) return

        // Same app still in front (repeat events) — nothing to do.
        if (pkg == foregroundApp) return

        // A genuine app switch. Leaving the previous app drops its grace pass,
        // so reopening it later pauses again.
        foregroundApp?.let { PauseGate.clear(it) }
        foregroundApp = pkg

        if (pkg in watched && !PauseGate.isAllowed(pkg)) {
            launchPause(pkg)
        }
    }

    private fun launchPause(targetPackage: String) {
        val intent = Intent(this, PauseActivity::class.java).apply {
            putExtra(PauseActivity.EXTRA_PACKAGE, targetPackage)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
    }

    private fun isTransientWindow(pkg: String): Boolean =
        pkg == packageName || pkg == SYSTEM_UI_PACKAGE || pkg == imePackage

    /** The package of the current default input method (e.g. the keyboard). */
    private fun currentImePackage(): String? =
        Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
            ?.substringBefore('/')
            ?.takeIf { it.isNotEmpty() }

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private companion object {
        const val SYSTEM_UI_PACKAGE = "com.android.systemui"
    }
}
