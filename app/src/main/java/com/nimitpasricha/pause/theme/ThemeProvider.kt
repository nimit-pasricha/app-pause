package com.nimitpasricha.pause.theme

import com.nimitpasricha.pause.theme.themes.BlossomTheme
import com.nimitpasricha.pause.theme.themes.SkyTheme
import java.time.LocalDate
import java.time.Month

/**
 * Chooses the day's theme. The rotation has a seasonal backbone with day-to-day
 * variation *within* a season — never user-selected. Deterministic from the
 * date, so the look is stable across a given day (e.g. the pause screen and the
 * home screen agree).
 *
 * Adding a theme is just dropping it into the [seasonThemes] list for a season;
 * the day-variation picker handles the rest.
 */
object ThemeProvider {

    enum class Season { SPRING, SUMMER, AUTUMN, WINTER }

    fun themeFor(date: LocalDate): Theme {
        val pool = seasonThemes(seasonOf(date.month))
        // Day-of-year drives the within-season variation deterministically.
        return pool[date.dayOfYear % pool.size]
    }

    private fun seasonThemes(season: Season): List<Theme> = when (season) {
        // Only two themes ship today; the per-season lists are where future
        // themes (and seasonal cats) slot in without touching anything else.
        Season.SPRING -> listOf(BlossomTheme)
        Season.SUMMER -> listOf(SkyTheme)
        Season.AUTUMN -> listOf(BlossomTheme)
        Season.WINTER -> listOf(SkyTheme)
    }

    private fun seasonOf(month: Month): Season = when (month) {
        Month.MARCH, Month.APRIL, Month.MAY -> Season.SPRING
        Month.JUNE, Month.JULY, Month.AUGUST -> Season.SUMMER
        Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER -> Season.AUTUMN
        Month.DECEMBER, Month.JANUARY, Month.FEBRUARY -> Season.WINTER
    }
}
