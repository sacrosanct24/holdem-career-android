package com.example.holdemcareer

import com.example.holdemcareer.domain.ai.AiDecisionEngine
import com.example.holdemcareer.domain.ai.OpponentCatalog
import com.example.holdemcareer.domain.poker.engine.ActionType
import com.example.holdemcareer.domain.poker.engine.BettingRound
import com.example.holdemcareer.domain.poker.engine.GameState
import com.example.holdemcareer.domain.poker.engine.PlayerState
import com.example.holdemcareer.domain.poker.model.Card
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AiDecisionEngineTest {

    @Test
    fun testMarcusFoldsToHighBetWithWeakHand() {
        val marcusProfile = OpponentCatalog.getById("marcus").copy(bluffFrequency = 0.0f)
        val marcusState = PlayerState(
            id = "marcus",
            name = "Marcus",
            chips = 500,
            currentBet = 0,
            holeCards = listOf(Card.fromString("2c"), Card.fromString("7d"))
        )

        val gameState = GameState(
            handSeed = 100L,
            players = listOf(marcusState),
            communityCards = listOf(Card.fromString("Ah"), Card.fromString("Kd"), Card.fromString("Qc")),
            currentRound = BettingRound.FLOP,
            currentHighestBet = 100,
            totalPot = 150
        )

        val action = AiDecisionEngine.decideAction(marcusProfile, gameState, marcusState)
        assertEquals(ActionType.FOLD, action.type)
        assertNotNull(action.reason)
    }

    @Test
    fun testVinceOnTiltIsMoreAggressive() {
        val normalVince = OpponentCatalog.getById("vince")
        val tiltingVince = normalVince.copy(currentTiltHands = 5)

        // Give Vince Trips (Three of a Kind)
        val vinceState = PlayerState(
            id = "vince",
            name = "Vince",
            chips = 500,
            currentBet = 0,
            holeCards = listOf(Card.fromString("Jh"), Card.fromString("Jd"))
        )

        val gameState = GameState(
            handSeed = 200L,
            players = listOf(vinceState),
            communityCards = listOf(Card.fromString("Js"), Card.fromString("8d"), Card.fromString("2s")),
            currentRound = BettingRound.FLOP,
            currentHighestBet = 0,
            totalPot = 50
        )

        val action = AiDecisionEngine.decideAction(tiltingVince, gameState, vinceState)
        assertEquals(ActionType.BET, action.type)
        assertTrue(action.reason?.contains("steaming") == true)
    }
}
