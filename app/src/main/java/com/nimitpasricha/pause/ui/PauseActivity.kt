package com.nimitpasricha.pause.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.nimitpasricha.pause.data.Stats
import com.nimitpasricha.pause.data.VisitTracker
import com.nimitpasricha.pause.domain.PauseLine
import com.nimitpasricha.pause.domain.PauseLines
import com.nimitpasricha.pause.domain.TimerPolicy
import com.nimitpasricha.pause.service.PauseGate
import com.nimitpasricha.pause.theme.ThemeProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.ceil

/**
 * The full-screen pause shown over a watched app. Launched by
 * [PauseAccessibilityService][com.nimitpasricha.pause.service.PauseAccessibilityService].
 *
 * It records the visit, computes the wait via [TimerPolicy], picks a line, runs
 * the gently-draining timer, swaps a two-beat setup for its payoff partway
 * through, and offers the two paths: back out (rewarded) or — once the timer
 * hits zero — open anyway.
 */
class PauseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val targetPackage = intent.getStringExtra(EXTRA_PACKAGE)
        if (targetPackage.isNullOrEmpty()) {
            finish()
            return
        }

        setContent {
            PauseRoot(
                targetPackage = targetPackage,
                appLabel = appLabel(targetPackage),
                onBackOut = {
                    // The rewarded path: count the win, then go home.
                    lifecycleScope.launch { Stats(applicationContext).recordBackedOut() }
                    goHome()
                    finish()
                },
                onOpenAnyway = {
                    // Let the service know to wave this one through, then reveal
                    // the app underneath by finishing.
                    PauseGate.allow(targetPackage)
                    finish()
                },
            )
        }
    }

    /** Hardware back behaves like "never mind" — a quiet exit home, counted as a win. */
    private fun goHome() {
        val home = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(home)
    }

    private fun appLabel(packageName: String): String = try {
        val pm = packageManager
        pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
    } catch (_: PackageManager.NameNotFoundException) {
        packageName
    }

    companion object {
        const val EXTRA_PACKAGE = "com.nimitpasricha.pause.extra.PACKAGE"

        /** Remembered across pauses so the same line isn't shown twice in a row. */
        private var lastShownLine: PauseLine? = null

        fun lastLine(): PauseLine? = lastShownLine

        fun rememberLine(line: PauseLine) {
            lastShownLine = line
        }
    }
}

@Composable
private fun PauseRoot(
    targetPackage: String,
    appLabel: String,
    onBackOut: () -> Unit,
    onOpenAnyway: () -> Unit,
) {
    val context = LocalContext.current
    val theme = remember { ThemeProvider.themeFor(LocalDate.now()) }

    var totalSeconds by remember { mutableStateOf(0) }
    var line by remember { mutableStateOf<PauseLine?>(null) }
    var contextLabel by remember { mutableStateOf("") }
    var elapsedMs by remember { mutableLongStateOf(0L) }

    BackHandler { onBackOut() }

    LaunchedEffect(targetPackage) {
        val count = VisitTracker(context).recordVisit(targetPackage)
        totalSeconds = TimerPolicy.durationSeconds(count)
        line = PauseLines.pick(count, avoid = PauseActivity.lastLine())
            .also { PauseActivity.rememberLine(it) }
        contextLabel = "$appLabel · ${ordinal(count)} visit today"

        val startedAt = System.currentTimeMillis()
        val totalMs = totalSeconds * 1000L
        while (true) {
            elapsedMs = System.currentTimeMillis() - startedAt
            if (elapsedMs >= totalMs) break
            delay(50)
        }
        elapsedMs = totalMs
    }

    val currentLine = line
    if (currentLine == null) {
        // Briefly themed surface while the first DataStore read completes.
        androidx.compose.foundation.layout.Box(
            Modifier
                .fillMaxSize()
                .background(theme.palette.surface),
        )
        return
    }

    val totalMs = totalSeconds * 1000L
    val remainingMs = (totalMs - elapsedMs).coerceAtLeast(0L)
    val secondsRemaining = ceil(remainingMs / 1000.0).toInt()
    val progress = if (totalMs == 0L) 0f else remainingMs.toFloat() / totalMs
    val unlocked = remainingMs <= 0L

    // Two-beat reveal: hold the setup, then flip to the payoff at the halfway mark.
    val heroLine = when (currentLine) {
        is PauseLine.Interference -> currentLine.text
        is PauseLine.TwoBeat ->
            if (remainingMs <= totalMs / 2) currentLine.payoff else currentLine.setup
    }

    PauseScreen(
        theme = theme,
        heroLine = heroLine,
        contextLabel = contextLabel,
        progress = progress,
        secondsRemaining = secondsRemaining,
        openUnlocked = unlocked,
        onBackOut = onBackOut,
        onOpenAnyway = onOpenAnyway,
    )
}

/** "1st", "2nd", "3rd", "7th", "11th"… for the plain context line. */
private fun ordinal(n: Int): String {
    val suffix = if (n % 100 in 11..13) {
        "th"
    } else when (n % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
    return "$n$suffix"
}
