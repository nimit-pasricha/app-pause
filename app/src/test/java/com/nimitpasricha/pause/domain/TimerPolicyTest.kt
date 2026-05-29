package com.nimitpasricha.pause.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TimerPolicyTest {

    @Test
    fun `first visit is 15 seconds`() {
        assertEquals(15, TimerPolicy.durationSeconds(1))
    }

    @Test
    fun `second visit is 30 seconds`() {
        assertEquals(30, TimerPolicy.durationSeconds(2))
    }

    @Test
    fun `third visit is 45 seconds`() {
        assertEquals(45, TimerPolicy.durationSeconds(3))
    }

    @Test
    fun `fourth visit is 60 seconds`() {
        assertEquals(60, TimerPolicy.durationSeconds(4))
    }

    @Test
    fun `keeps climbing 15s per reopen up to the cap`() {
        assertEquals(75, TimerPolicy.durationSeconds(5))
        assertEquals(150, TimerPolicy.durationSeconds(10))
        assertEquals(285, TimerPolicy.durationSeconds(19))
    }

    @Test
    fun `twentieth visit reaches the 5 minute cap`() {
        assertEquals(300, TimerPolicy.durationSeconds(20))
    }

    @Test
    fun `never exceeds the 5 minute cap`() {
        for (visit in 20..1000) {
            assertEquals(TimerPolicy.MAX_SECONDS, TimerPolicy.durationSeconds(visit))
        }
        assertEquals(300, TimerPolicy.MAX_SECONDS)
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
        assertEquals(15, TimerPolicy.durationSeconds(0))
        assertEquals(15, TimerPolicy.durationSeconds(-3))
    }
}
