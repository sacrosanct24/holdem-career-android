package com.example.holdemcareer

import com.example.holdemcareer.domain.career.CoachAnalyzer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CoachAnalyzerTest {

    @Test
    fun testDetectsHighVpipAndLimpingLeaks() {
        val feedback = CoachAnalyzer.analyzeSession(
            handsPlayed = 50,
            vpip = 0.55f, // Very loose
            pfr = 0.08f,  // Very passive limper
            netProfit = -100
        )

        assertEquals(2, feedback.severeLeaksCount)
        assertTrue(feedback.tips.any { it.contains("playing too many hands") })
        assertTrue(feedback.tips.any { it.contains("limping too much") })
    }

    @Test
    fun testSolidPerformanceWithPositiveProfit() {
        val feedback = CoachAnalyzer.analyzeSession(
            handsPlayed = 50,
            vpip = 0.22f,
            pfr = 0.18f,
            netProfit = 250
        )

        assertEquals(0, feedback.severeLeaksCount)
        assertTrue(feedback.tips.any { it.contains("+ $250 profit") || it.contains("+$250 profit") })
    }
}
