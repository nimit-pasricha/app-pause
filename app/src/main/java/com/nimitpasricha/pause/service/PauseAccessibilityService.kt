package com.nimitpasricha.pause.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
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
 * chose "Open anyway". We track the current foreground *app* (packages that
 * have a launcher entry — which filters out IMEs, dialogs, and system UI) and
 * only act on a real switch into a watched app.
 */
class PauseAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(SupervisorJob())

    /** Snapshot of watched packages, kept current by collecting the DataStore flow. */
    @Volatile
    private var watched: Set<String> = emptySet()

    /** The real app currently in the foreground (null until the first switch). */
    @Volatile
    private var foregroundApp: String? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        WatchedApps(applicationContext).watched
            .onEach { watched = it }
            .launchIn(scope)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return

        // Never react to our own pause/home screens — that would loop.
        if (pkg == packageName) return

        // Ignore window blips that aren't a real app coming forward (keyboards,
        // dialogs, system UI). Only launchable apps change the foreground app.
        if (!isLaunchableApp(pkg)) return

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

    private fun isLaunchableApp(packageName: String): Boolean =
        packageManager.getLaunchIntentForPackage(packageName) != null

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
