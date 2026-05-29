package com.nimitpasricha.pause.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TimerPolicyTest {

    @Test
    fun `first visit is 5 seconds`() {
        assertEquals(5, TimerPolicy.durationSeconds(1))
    }

    @Test
    fun `second visit is 15 seconds`() {
        assertEquals(15, TimerPolicy.durationSeconds(2))
    }

    @Test
    fun `third visit is 30 seconds`() {
        assertEquals(30, TimerPolicy.durationSeconds(3))
    }

    @Test
    fun `never exceeds the 30 second cap`() {
        for (visit in 3..1000) {
            assertEquals(TimerPolicy.MAX_SECONDS, TimerPolicy.durationSeconds(visit))
        }
    }

    @Test
    fun `is monotonic - reopening never shortens the wait`() {
        var previous = 0
        for (visit in 1..50) {
            val current = TimerPolicy.durationSeconds(visit)
            assertTrue("visit $visit shortened the wait", current >= previous)
            previous = current
        }
    }

    @Test
    fun `defensive - zero or negative counts fall back to the shortest wait`() {
        assertEquals(5, TimerPolicy.durationSeconds(0))
        assertEquals(5, TimerPolicy.durationSeconds(-3))
    }
}
