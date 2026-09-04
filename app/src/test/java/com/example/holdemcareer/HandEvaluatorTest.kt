package com.example.holdemcareer

import com.example.holdemcareer.domain.poker.evaluator.HandEvaluator
import com.example.holdemcareer.domain.poker.evaluator.HandRank
import com.example.holdemcareer.domain.poker.model.Card
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HandEvaluatorTest {

    @Test
    fun testRoyalFlush() {
        val cards = listOf(
            Card.fromString("Ah"),
            Card.fromString("Kh"),
            Card.fromString("Qh"),
            Card.fromString("Jh"),
            Card.fromString("Th"),
            Card.fromString("2c"),
            Card.fromString("3d")
        )
        val result = HandEvaluator.evaluate(cards)
        assertEquals(HandRank.ROYAL_FLUSH, result.handRank)
    }

    @Test
    fun testFullHouseBeatsFlush() {
        val fullHouseCards = listOf(
            Card.fromString("Ks"), Card.fromString("Kh"), Card.fromString("Kd"),
            Card.fromString("4c"), Card.fromString("4s"), Card.fromString("2h"), Card.fromString("3d")
        )
        val flushCards = listOf(
            Card.fromString("Ah"), Card.fromString("Jh"), Card.fromString("8h"),
            Card.fromString("6h"), Card.fromString("2h"), Card.fromString("Kc"), Card.fromString("Qd")
        )

        val fhResult = HandEvaluator.evaluate(fullHouseCards)
        val flushResult = HandEvaluator.evaluate(flushCards)

        assertEquals(HandRank.FULL_HOUSE, fhResult.handRank)
        assertEquals(HandRank.FLUSH, flushResult.handRank)
        assertTrue(fhResult > flushResult)
    }

    @Test
    fun testAceLowWheelStraight() {
        val wheelCards = listOf(
            Card.fromString("Ah"), Card.fromString("2c"), Card.fromString("3d"),
            Card.fromString("4s"), Card.fromString("5h"), Card.fromString("9c"), Card.fromString("Kd")
        )
        val result = HandEvaluator.evaluate(wheelCards)
        assertEquals(HandRank.STRAIGHT, result.handRank)
        assertEquals(5, result.tieBreakers[0])
    }

    @Test
    fun testTwoPairKickerTieBreaker() {
        val hand1 = listOf(
            Card.fromString("As"), Card.fromString("Ah"), Card.fromString("Ks"),
            Card.fromString("Kh"), Card.fromString("Qc"), Card.fromString("2d"), Card.fromString("3s")
        )
        val hand2 = listOf(
            Card.fromString("Ad"), Card.fromString("Ac"), Card.fromString("Kd"),
            Card.fromString("Kc"), Card.fromString("Jd"), Card.fromString("4s"), Card.fromString("5c")
        )

        val res1 = HandEvaluator.evaluate(hand1)
        val res2 = HandEvaluator.evaluate(hand2)

        assertEquals(HandRank.TWO_PAIR, res1.handRank)
        assertEquals(HandRank.TWO_PAIR, res2.handRank)
        assertTrue(res1 > res2) // Queen kicker beats Jack kicker
    }
}
