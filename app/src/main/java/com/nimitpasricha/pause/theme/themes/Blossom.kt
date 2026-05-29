package com.nimitpasricha.pause.theme.themes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.nimitpasricha.pause.theme.PausePalette
import com.nimitpasricha.pause.theme.Theme

/**
 * Blossom — a pink spring theme: a soft branch in the top corner with a few
 * petals drifting down the margins. Warm and quiet; it decorates the edges and
 * stays well clear of the hero text.
 */
val BlossomTheme: Theme = Theme(
    name = "Blossom",
    palette = PausePalette(
        surface = Color(0xFFFFF5F0),
        heroText = Color(0xFF5A3A36),
        contextText = Color(0xFFA98A82),
        primaryButton = Color(0xFFE58B7B),
        onPrimaryButton = Color(0xFFFFFFFF),
        quietButtonText = Color(0xFFB58A80),
        ring = Color(0xFFE58B7B),
        ringTrack = Color(0xFFF3DDD5),
    ),
    background = { modifier ->
        val branch = Color(0xFFC99A8E)
        val petal = Color(0xFFF1B9AE)
        val blossom = Color(0xFFF6C9C0)
        Canvas(modifier = modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // A branch arcing in from the top-left corner.
            val branchPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(-20f, 40f)
                cubicTo(w * 0.18f, 70f, w * 0.28f, 150f, w * 0.40f, 120f)
            }
            drawPath(branchPath, color = branch.copy(alpha = 0.55f), style = Stroke(width = 6f))

            // A few blossoms clustered on the branch.
            listOf(
                Offset(w * 0.16f, 78f) to 16f,
                Offset(w * 0.27f, 132f) to 13f,
                Offset(w * 0.37f, 122f) to 11f,
            ).forEach { (center, r) ->
                drawCircle(blossom.copy(alpha = 0.6f), radius = r, center = center)
                drawCircle(petal.copy(alpha = 0.7f), radius = r * 0.4f, center = center)
            }

            // Petals drifting down the right and bottom margins.
            listOf(
                Offset(w * 0.90f, h * 0.20f),
                Offset(w * 0.84f, h * 0.42f),
                Offset(w * 0.92f, h * 0.66f),
                Offset(w * 0.18f, h * 0.88f),
                Offset(w * 0.46f, h * 0.94f),
                Offset(w * 0.72f, h * 0.90f),
            ).forEach { c ->
                drawCircle(petal.copy(alpha = 0.45f), radius = 7f, center = c)
            }
        }
    },
)
