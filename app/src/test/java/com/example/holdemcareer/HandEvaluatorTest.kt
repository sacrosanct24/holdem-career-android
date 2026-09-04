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
    fun testStraightFlush() {
        val cards = listOf(
            Card.fromString("9s"), Card.fromString("8s"), Card.fromString("7s"),
            Card.fromString("6s"), Card.fromString("5s"), Card.fromString("2c"), Card.fromString("3d")
        )
        val result = HandEvaluator.evaluate(cards)
        assertEquals(HandRank.STRAIGHT_FLUSH, result.handRank)
        assertEquals(9, result.tieBreakers[0])
    }

    @Test
    fun testFourOfAKindBeatsFullHouse() {
        val quads = listOf(
            Card.fromString("9s"), Card.fromString("9h"), Card.fromString("9d"),
            Card.fromString("9c"), Card.fromString("Ks"), Card.fromString("2c"), Card.fromString("3d")
        )
        val fullHouse = listOf(
            Card.fromString("As"), Card.fromString("Ah"), Card.fromString("Ad"),
            Card.fromString("Kc"), Card.fromString("Kd"), Card.fromString("2h"), Card.fromString("3s")
        )

        val quadResult = HandEvaluator.evaluate(quads)
        val fhResult = HandEvaluator.evaluate(fullHouse)

        assertEquals(HandRank.FOUR_OF_A_KIND, quadResult.handRank)
        assertEquals(HandRank.FULL_HOUSE, fhResult.handRank)
        assertTrue(quadResult > fhResult)
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

    @Test
    fun testSplitPotExactTie() {
        val board = listOf(
            Card.fromString("Ah"), Card.fromString("Kd"), Card.fromString("Qc"),
            Card.fromString("Js"), Card.fromString("Th")
        )
        val player1 = listOf(Card.fromString("2c"), Card.fromString("3c")) + board
        val player2 = listOf(Card.fromString("4d"), Card.fromString("5d")) + board

        val res1 = HandEvaluator.evaluate(player1)
        val res2 = HandEvaluator.evaluate(player2)

        assertEquals(HandRank.STRAIGHT, res1.handRank)
        assertEquals(HandRank.STRAIGHT, res2.handRank)
        assertEquals(0, res1.compareTo(res2)) // Exact tie for split pot!
    }
}
