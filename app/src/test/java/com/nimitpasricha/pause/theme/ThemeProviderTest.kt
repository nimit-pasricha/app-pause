package com.nimitpasricha.pause.theme

import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class ThemeProviderTest {

    @Test
    fun `random never returns the avoided theme`() {
        val random = Random(13)
        var previous: Theme? = null
        repeat(500) {
            val theme = ThemeProvider.random(avoid = previous, random = random)
            assertNotEquals(previous, theme)
            previous = theme
        }
    }

    @Test
    fun `random only returns registered themes`() {
        val random = Random(99)
        repeat(200) {
            assertTrue(ThemeProvider.random(random = random) in ThemeProvider.all)
        }
    }

    @Test
    fun `rotation exercises more than one theme`() {
        val random = Random(5)
        val seen = (1..200).map { ThemeProvider.random(random = random) }.toSet()
        assertTrue("rotation should surface multiple themes", seen.size > 1)
    }
}
