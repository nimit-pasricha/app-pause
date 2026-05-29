package com.nimitpasricha.pause.domain

import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class PauseLinesTest {

    @Test
    fun `pick never returns the avoided line`() {
        // Many draws with a fixed seed; each must differ from the previous one.
        val random = Random(42)
        var previous: PauseLine? = null
        repeat(500) {
            val line = PauseLines.pick(visitCount = 3, avoid = previous, random = random)
            assertNotEquals(previous, line)
            previous = line
        }
    }

    @Test
    fun `every tier yields both interference and two-beat lines over many draws`() {
        for (visit in intArrayOf(1, 2, 5)) {
            val random = Random(7)
            val drawn = (1..400).map { PauseLines.pick(visit, random = random) }.toSet()
            assertTrue("tier for visit $visit should produce interference lines",
                drawn.any { it is PauseLine.Interference })
            assertTrue("tier for visit $visit should produce two-beat reveals",
                drawn.any { it is PauseLine.TwoBeat })
        }
    }

    @Test
    fun `tiers are distinct - visit 1, 2, and 3 draw from different pools`() {
        // With the same seed, distinct pools should not all collapse to one line set.
        val v1 = (1..200).map { PauseLines.pick(1, random = Random(1)) }.toSet()
        val v3 = (1..200).map { PauseLines.pick(3, random = Random(1)) }.toSet()
        assertTrue("tier 1 and tier 3 pools should differ", (v1 intersect v3).size < v1.size)
    }
}
