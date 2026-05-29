package com.nimitpasricha.pause.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * The colors that recolor the pause screen so each theme feels like its own
 * world. Warm and friendly rather than clinical — the feel of a sticky note
 * from a friend.
 */
data class PausePalette(
    /** The screen background base tone. */
    val surface: Color,
    /** The hero serif line — the screen's protagonist. */
    val heroText: Color,
    /** The quiet context line ("Instagram · 7th visit today"). */
    val contextText: Color,
    /** The prominent, filled "Actually, never mind" button. */
    val primaryButton: Color,
    val onPrimaryButton: Color,
    /** The deliberately quiet "Open anyway" affordance. */
    val quietButtonText: Color,
    /** The gently-draining timer ring and its faint track. */
    val ring: Color,
    val ringTrack: Color,
)

/**
 * A theme is a data bundle: a palette, some edge-framed background art, and
 * (later) a cat. Adding the tenth theme must be as easy as adding the second —
 * so a theme is just an instance of this class registered in [ThemeProvider].
 *
 * [background] paints soft decoration in the corners/margins; it must never
 * compete with the hero text or buttons for legibility.
 */
class Theme(
    val name: String,
    val palette: PausePalette,
    val background: @Composable (Modifier) -> Unit,
)
