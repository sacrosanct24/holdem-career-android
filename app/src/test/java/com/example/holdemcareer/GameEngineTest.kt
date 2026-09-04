package com.example.holdemcareer

import com.example.holdemcareer.domain.poker.engine.ActionType
import com.example.holdemcareer.domain.poker.engine.BettingRound
import com.example.holdemcareer.domain.poker.engine.GameEngine
import com.example.holdemcareer.domain.poker.engine.PlayerAction
import com.example.holdemcareer.domain.poker.engine.PlayerState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {

    @Test
    fun testHandFlowToFoldWin() {
        val engine = GameEngine()
        val players = listOf(
            PlayerState(id = "p1", name = "Alice", chips = 500, seatIndex = 0),
            PlayerState(id = "p2", name = "Bob", chips = 500, seatIndex = 1)
        )

        // Heads up: dealerIndex = 0 -> SB = 1 (p2), BB = 0 (p1), first actor = SB (p2)
        engine.startNewHand(players, dealerIndex = 0, smallBlind = 5, bigBlind = 10, seed = 42L)

        val state1 = engine.state
        assertEquals(BettingRound.PRE_FLOP, state1.currentRound)

        // Bob (active player) folds
        val activePlayerId = state1.players[state1.activePlayerIndex].id
        engine.applyAction(PlayerAction(playerId = activePlayerId, type = ActionType.FOLD))

        val finalState = engine.state
        assertEquals(BettingRound.ENDED, finalState.currentRound)
        assertEquals(1, finalState.winnersSummary.size)
        assertTrue(finalState.winnersSummary[0].amountWon > 0)
    }
}
