package com.nimitpasricha.pause.theme

import com.nimitpasricha.pause.theme.themes.BlossomTheme
import com.nimitpasricha.pause.theme.themes.SkyTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import java.time.LocalDate

class ThemeProviderTest {

    @Test
    fun `spring days use the blossom theme`() {
        assertSame(BlossomTheme, ThemeProvider.themeFor(LocalDate.of(2026, 4, 15)))
    }

    @Test
    fun `summer days use the sky theme`() {
        assertSame(SkyTheme, ThemeProvider.themeFor(LocalDate.of(2026, 7, 1)))
    }

    @Test
    fun `the theme is stable across a single day`() {
        val date = LocalDate.of(2026, 5, 29)
        assertSame(ThemeProvider.themeFor(date), ThemeProvider.themeFor(date))
    }

    @Test
    fun `every month resolves to a theme`() {
        for (month in 1..12) {
            val theme = ThemeProvider.themeFor(LocalDate.of(2026, month, 10))
            assertEquals(true, theme === BlossomTheme || theme === SkyTheme)
        }
    }
}
