package com.nimitpasricha.pause.theme

import com.nimitpasricha.pause.theme.themes.BlossomTheme
import com.nimitpasricha.pause.theme.themes.SkyTheme
import kotlin.random.Random

/**
 * Chooses a theme. The look switches on every pause — picked at random with no
 * immediate repeat (the same trick the copy lines use), so the screen stays a
 * little surprising rather than fading into wallpaper. Never user-selected.
 *
 * Adding a theme is just dropping it into [all]; the rotation handles the rest.
 */
object ThemeProvider {

    /** Every registered theme. Adding the tenth is as cheap as the second. */
    val all: List<Theme> = listOf(BlossomTheme, SkyTheme)

    /** A random theme, never equal to [avoid] (the last one shown). */
    fun random(avoid: Theme? = null, random: Random = Random.Default): Theme {
        if (all.size <= 1) return all.first()
        while (true) {
            val candidate = all[random.nextInt(all.size)]
            if (candidate != avoid) return candidate
        }
    }
}
