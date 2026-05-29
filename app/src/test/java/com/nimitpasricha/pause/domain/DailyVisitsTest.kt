package com.nimitpasricha.pause.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class DailyVisitsTest {

    private val ig = "com.instagram.android"
    private val tt = "com.zhiliaoapp.musically"

    @Test
    fun `first visit of the day counts as 1`() {
        val (_, count) = DailyVisits.EMPTY.recordVisit("2026-05-29", ig)
        assertEquals(1, count)
    }

    @Test
    fun `repeated visits same day increment`() {
        var ledger = DailyVisits.EMPTY
        val counts = (1..4).map {
            val (next, c) = ledger.recordVisit("2026-05-29", ig)
            ledger = next
            c
        }
        assertEquals(listOf(1, 2, 3, 4), counts)
    }

    @Test
    fun `counts are independent per app`() {
        var ledger = DailyVisits.EMPTY
        ledger = ledger.recordVisit("2026-05-29", ig).first
        ledger = ledger.recordVisit("2026-05-29", ig).first
        val (_, ttCount) = ledger.recordVisit("2026-05-29", tt)
        assertEquals(1, ttCount)
        assertEquals(2, ledger.countFor("2026-05-29", ig))
    }

    @Test
    fun `new day resets all counts`() {
        var ledger = DailyVisits.EMPTY
        repeat(5) { ledger = ledger.recordVisit("2026-05-29", ig).first }
        assertEquals(5, ledger.countFor("2026-05-29", ig))

        // First open the next morning starts fresh at 1.
        val (next, count) = ledger.recordVisit("2026-05-30", ig)
        assertEquals(1, count)
        assertEquals(0, next.countFor("2026-05-30", tt))
    }

    @Test
    fun `countFor returns zero after rollover even before recording`() {
        var ledger = DailyVisits.EMPTY
        ledger = ledger.recordVisit("2026-05-29", ig).first
        assertEquals(0, ledger.countFor("2026-05-30", ig))
    }
}
