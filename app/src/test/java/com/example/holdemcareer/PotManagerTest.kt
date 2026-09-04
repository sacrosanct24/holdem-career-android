package com.example.holdemcareer

import com.example.holdemcareer.domain.poker.engine.PotManager
import org.junit.Assert.assertEquals
import org.junit.Test

class PotManagerTest {

    @Test
    fun testSimplePot() {
        val potManager = PotManager()
        potManager.recordBet("p1", 50)
        potManager.recordBet("p2", 50)
        potManager.recordBet("p3", 50)

        val pots = potManager.calculatePots(setOf("p1", "p2", "p3"))
        assertEquals(1, pots.size)
        assertEquals(150, pots[0].amount)
        assertEquals(setOf("p1", "p2", "p3"), pots[0].eligiblePlayerIds)
    }

    @Test
    fun testSidePotsWithAllIn() {
        val potManager = PotManager()
        // p1 goes all-in for 100
        // p2 calls 100 and has 200 left
        // p3 calls 100 and raises to 300
        // p2 calls 200 more
        potManager.recordBet("p1", 100)
        potManager.recordBet("p2", 300)
        potManager.recordBet("p3", 300)

        val pots = potManager.calculatePots(setOf("p1", "p2", "p3"))
        assertEquals(2, pots.size)

        // Main pot: 100 * 3 = 300 (eligible: p1, p2, p3)
        assertEquals(300, pots[0].amount)
        assertEquals(setOf("p1", "p2", "p3"), pots[0].eligiblePlayerIds)

        // Side pot: (300-100) * 2 = 400 (eligible: p2, p3)
        assertEquals(400, pots[1].amount)
        assertEquals(setOf("p2", "p3"), pots[1].eligiblePlayerIds)
    }
}
