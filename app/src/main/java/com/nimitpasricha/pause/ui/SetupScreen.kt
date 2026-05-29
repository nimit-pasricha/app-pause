package com.nimitpasricha.pause.ui

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.nimitpasricha.pause.data.Stats
import com.nimitpasricha.pause.data.WatchedApps
import com.nimitpasricha.pause.data.WinStats
import com.nimitpasricha.pause.theme.Fraunces
import com.nimitpasricha.pause.theme.ThemeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** A launchable app the user can choose to watch. */
data class InstalledApp(
    val packageName: String,
    val label: String,
    val icon: ImageBitmap,
)

@Composable
fun SetupScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val theme = remember { ThemeProvider.random() }
    val palette = theme.palette

    val watchedApps = remember { WatchedApps(context) }
    val statsRepo = remember { Stats(context) }
    val watched by watchedApps.watched.collectAsState(initial = emptySet())
    val stats by statsRepo.stats.collectAsState(initial = WinStats(0, 0))

    var apps by remember { mutableStateOf<List<InstalledApp>>(emptyList()) }
    LaunchedEffect(Unit) { apps = loadInstalledApps(context) }

    var query by remember { mutableStateOf("") }
    val filteredApps = remember(apps, query) {
        val q = query.trim()
        if (q.isEmpty()) apps else apps.filter { it.label.contains(q, ignoreCase = true) }
    }

    // Re-check the permission prompts whenever we come back to this screen
    // (the user may have just toggled them in system Settings).
    var serviceEnabled by remember { mutableStateOf(isServiceEnabled(context)) }
    var canOverlay by remember { mutableStateOf(Settings.canDrawOverlays(context)) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                serviceEnabled = isServiceEnabled(context)
                canOverlay = Settings.canDrawOverlays(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.surface),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 24.dp, end = 24.dp, top = 56.dp, bottom = 40.dp,
        ),
    ) {
        item {
            Text(
                text = "Beat",
                fontFamily = Fraunces,
                fontSize = 40.sp,
                color = palette.heroText,
            )
            Text(
                text = "Give it a beat before you open.",
                fontSize = 15.sp,
                color = palette.contextText,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp),
            )
        }

        item {
            WinCard(stats = stats, palette = palette)
            Spacer(Modifier.height(16.dp))
        }

        if (!serviceEnabled) {
            item {
                NudgeCard(
                    palette = palette,
                    title = "Turn Beat on",
                    body = "Beat needs the accessibility service to notice when you open a watched app. You enable it once, by hand — Settings › Accessibility › Beat.",
                    button = "Open Accessibility settings",
                    onClick = { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) },
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        if (!canOverlay) {
            item {
                NudgeCard(
                    palette = palette,
                    title = "Let Beat draw on top",
                    body = "So the pause can appear over the app you're opening, allow \"Display over other apps\".",
                    button = "Allow display over apps",
                    onClick = {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}"),
                            ),
                        )
                    },
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        item {
            Text(
                text = "Apps to watch",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.contextText,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            )
        }

        if (apps.isEmpty()) {
            item {
                Text(
                    text = "Loading your apps…",
                    fontSize = 14.sp,
                    color = palette.contextText,
                )
            }
        } else {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    placeholder = { Text("Search apps") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = palette.heroText,
                        unfocusedTextColor = palette.heroText,
                        cursorColor = palette.primaryButton,
                        focusedBorderColor = palette.primaryButton,
                        unfocusedBorderColor = palette.ringTrack,
                        focusedPlaceholderColor = palette.contextText,
                        unfocusedPlaceholderColor = palette.contextText,
                    ),
                )
            }

            if (filteredApps.isEmpty()) {
                item {
                    Text(
                        text = "No apps match \"${query.trim()}\".",
                        fontSize = 14.sp,
                        color = palette.contextText,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            } else {
                items(filteredApps, key = { it.packageName }) { app ->
                    AppRow(
                        app = app,
                        checked = app.packageName in watched,
                        palette = palette,
                        onToggle = { on ->
                            scope.launch { watchedApps.setWatched(app.packageName, on) }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun WinCard(stats: WinStats, palette: com.nimitpasricha.pause.theme.PausePalette) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = palette.ringTrack),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stats.backedOutTotal.toString(),
                fontFamily = Fraunces,
                fontSize = 52.sp,
                color = palette.primaryButton,
            )
            Text(
                text = if (stats.backedOutTotal == 1) "time you talked yourself out of it"
                else "times you talked yourself out of it",
                fontSize = 14.sp,
                color = palette.heroText,
            )
            if (stats.streakDays >= 2) {
                Text(
                    text = "${stats.streakDays}-day streak. Nice.",
                    fontSize = 13.sp,
                    color = palette.contextText,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun NudgeCard(
    palette: com.nimitpasricha.pause.theme.PausePalette,
    title: String,
    body: String,
    button: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = palette.ringTrack.copy(alpha = 0.6f)),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = palette.heroText)
            Text(
                text = body,
                fontSize = 14.sp,
                color = palette.contextText,
                modifier = Modifier.padding(top = 6.dp, bottom = 14.dp),
            )
            Button(
                onClick = onClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.primaryButton,
                    contentColor = palette.onPrimaryButton,
                ),
            ) {
                Text(button)
            }
        }
    }
}

@Composable
private fun AppRow(
    app: InstalledApp,
    checked: Boolean,
    palette: com.nimitpasricha.pause.theme.PausePalette,
    onToggle: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(bitmap = app.icon, contentDescription = null, modifier = Modifier.size(40.dp))
            Text(
                text = app.label,
                fontSize = 16.sp,
                color = palette.heroText,
                modifier = Modifier.padding(start = 16.dp),
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = palette.onPrimaryButton,
                checkedTrackColor = palette.primaryButton,
            ),
        )
    }
}

private suspend fun loadInstalledApps(context: Context): List<InstalledApp> =
    withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val launcher = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        pm.queryIntentActivities(launcher, 0)
            .asSequence()
            .map { it.activityInfo.packageName }
            .distinct()
            .filter { it != context.packageName }
            .mapNotNull { pkg ->
                runCatching {
                    val info = pm.getApplicationInfo(pkg, 0)
                    InstalledApp(
                        packageName = pkg,
                        label = pm.getApplicationLabel(info).toString(),
                        icon = pm.getApplicationIcon(info).toBitmap().asImageBitmap(),
                    )
                }.getOrNull()
            }
            .sortedBy { it.label.lowercase() }
            .toList()
    }

private fun isServiceEnabled(context: Context): Boolean {
    val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    return am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        .any { it.resolveInfo.serviceInfo.packageName == context.packageName }
}
