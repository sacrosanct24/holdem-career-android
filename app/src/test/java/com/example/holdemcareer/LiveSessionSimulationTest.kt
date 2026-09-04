package com.example.holdemcareer

import com.example.holdemcareer.domain.ai.AiDecisionEngine
import com.example.holdemcareer.domain.ai.OpponentCatalog
import com.example.holdemcareer.domain.poker.engine.BettingRound
import com.example.holdemcareer.domain.poker.engine.GameEngine
import com.example.holdemcareer.domain.poker.engine.PlayerState
import org.junit.Assert.assertTrue
import org.junit.Test

class LiveSessionSimulationTest {

    @Test
    fun testSimulate100HandsLiveTableSession() {
        val engine = GameEngine()
        val opponents = OpponentCatalog.allOpponents.take(5)

        var players = listOf(
            PlayerState(id = "human", name = "You", chips = 200, isHuman = true, seatIndex = 0)
        ) + opponents.mapIndexed { idx, opp ->
            PlayerState(id = opp.id, name = opp.name, chips = 200, isHuman = false, seatIndex = idx + 1)
        }

        var handsPlayed = 0
        var totalPotsAwarded = 0

        for (handIndex in 1..100) {
            val dealerIdx = handIndex % players.size
            engine.startNewHand(
                initialPlayers = players,
                dealerIndex = dealerIdx,
                smallBlind = 1,
                bigBlind = 2,
                seed = 1000L + handIndex
            )

            // Auto-run hand until completion
            var loopGuard = 0
            while (engine.state.currentRound != BettingRound.ENDED && loopGuard < 200) {
                loopGuard++
                val activeIdx = engine.state.activePlayerIndex
                val activePlayer = engine.state.players.getOrNull(activeIdx) ?: break

                if (activePlayer.isFolded || activePlayer.isAllIn) {
                    // Skip if active player is non-actionable
                    break
                }

                val profile = if (activePlayer.isHuman) {
                    OpponentCatalog.getById("elena") // Simulate solid human play
                } else {
                    OpponentCatalog.getById(activePlayer.id)
                }

                val action = AiDecisionEngine.decideAction(profile, engine.state, activePlayer)
                val success = engine.applyAction(action)
                if (!success) break
            }

            if (engine.state.currentRound == BettingRound.ENDED) {
                handsPlayed++
                totalPotsAwarded += engine.state.totalPot

                // Re-up players who busted below big blind
                players = engine.state.players.map { p ->
                    if (p.chips < 2) p.copy(chips = 200) else p
                }
            }
        }

        // Verify that the vast majority (> 90%) of simulated hands complete to showdown/fold
        assertTrue("Expected at least 90 completed hands, got $handsPlayed", handsPlayed >= 90)
        assertTrue(totalPotsAwarded > 0)
    }
}
