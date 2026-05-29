package com.nimitpasricha.pause.theme.themes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.nimitpasricha.pause.theme.PausePalette
import com.nimitpasricha.pause.theme.Theme

/**
 * Sky — a blue theme: a soft sun glowing in the top corner with a couple of
 * clouds resting low in the margins. Calm and airy; decoration only.
 */
val SkyTheme: Theme = Theme(
    name = "Sky",
    palette = PausePalette(
        surface = Color(0xFFF2F7FC),
        heroText = Color(0xFF2E3A4A),
        contextText = Color(0xFF7E91A8),
        primaryButton = Color(0xFF6FA8D6),
        onPrimaryButton = Color(0xFFFFFFFF),
        quietButtonText = Color(0xFF8AA3BC),
        ring = Color(0xFF6FA8D6),
        ringTrack = Color(0xFFDCE8F3),
    ),
    background = { modifier ->
        val sun = Color(0xFFF6E2A8)
        val cloud = Color(0xFFE6EFF8)
        Canvas(modifier = modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // A soft sun haloing in from the top-right corner.
            drawCircle(sun.copy(alpha = 0.30f), radius = 120f, center = Offset(w * 0.92f, 60f))
            drawCircle(sun.copy(alpha = 0.55f), radius = 66f, center = Offset(w * 0.92f, 60f))

            // Clouds: clusters of overlapping circles low in the margins.
            fun cloud(cx: Float, cy: Float, scale: Float, alpha: Float) {
                val c = cloud.copy(alpha = alpha)
                drawCircle(c, radius = 26f * scale, center = Offset(cx, cy))
                drawCircle(c, radius = 34f * scale, center = Offset(cx + 30f * scale, cy + 4f * scale))
                drawCircle(c, radius = 24f * scale, center = Offset(cx + 64f * scale, cy))
                drawCircle(c, radius = 22f * scale, center = Offset(cx + 18f * scale, cy + 16f * scale))
            }
            cloud(w * 0.10f, h * 0.86f, scale = 1.1f, alpha = 0.75f)
            cloud(w * 0.62f, h * 0.92f, scale = 0.9f, alpha = 0.6f)
            cloud(w * 0.80f, h * 0.40f, scale = 0.7f, alpha = 0.45f)
        }
    },
)
