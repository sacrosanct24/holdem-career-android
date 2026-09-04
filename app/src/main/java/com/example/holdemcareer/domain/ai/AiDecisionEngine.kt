package com.example.holdemcareer.domain.ai

import com.example.holdemcareer.domain.poker.engine.ActionType
import com.example.holdemcareer.domain.poker.engine.GameState
import com.example.holdemcareer.domain.poker.engine.PlayerAction
import com.example.holdemcareer.domain.poker.engine.PlayerState
import com.example.holdemcareer.domain.poker.evaluator.HandEvaluator
import com.example.holdemcareer.domain.poker.model.Card
import kotlin.math.sqrt
import kotlin.random.Random

object AiDecisionEngine {

    fun decideAction(
        profile: OpponentProfile,
        gameState: GameState,
        playerState: PlayerState
    ): PlayerAction {
        val callAmount = gameState.currentHighestBet - playerState.currentBet
        val pot = gameState.totalPot
        val potOdds = if (pot + callAmount > 0) callAmount.toFloat() / (pot + callAmount) else 0f

        // 1. Texture-aware raw equity
        var rawEquity = estimateEquityWithTexture(
            holeCards = playerState.holeCards,
            communityCards = gameState.communityCards
        )

        // 2. Multi-way pot scale-down (for 3+ active players)
        val activePlayersCount = gameState.players.count { !it.isFolded }
        if (activePlayersCount > 2) {
            val scaleFactor = 1.0f / sqrt(activePlayersCount.toDouble()).toFloat()
            rawEquity *= scaleFactor
        }

        // 3. Action-based opponent image adjustment (if facing bet/raise)
        if (callAmount > 0) {
            val lastRaiser = gameState.players.firstOrNull { it.currentBet == gameState.currentHighestBet && it.id != playerState.id }
            if (lastRaiser != null) {
                val raiserProfile = OpponentCatalog.getById(lastRaiser.id)
                if (raiserProfile.pfr < 0.10f) {
                    // Nit raiser (e.g. Marcus) -> reduce our perceived equity
                    rawEquity *= 0.75f
                } else if (raiserProfile.pfr > 0.30f) {
                    // Aggressive/maniac raiser -> boost our perceived equity
                    rawEquity *= 1.15f
                }
            }
        }

        val isOnTilt = profile.currentTiltHands > 0

        // 4. Tilt adjustment
        var adjustedEquity = rawEquity
        if (isOnTilt) {
            adjustedEquity = minOf(1.0f, rawEquity * 1.35f) // Tilt overestimates hand strength
        }

        val rng = Random.nextFloat()
        val isBluffing = rng < (profile.bluffFrequency * if (isOnTilt) 1.8f else 1.0f)

        val reasonPrefix = if (isOnTilt) "still steaming from last pot" else null

        // Decision logic
        if (callAmount <= 0) {
            if (adjustedEquity > 0.65f || isBluffing) {
                val betAmount = minOf(playerState.chips, maxOf(gameState.bigBlindAmount, pot / 2))
                if (betAmount > 0) {
                    val reason = buildReason(
                        reasonPrefix,
                        if (adjustedEquity > 0.65f) "value betting top equity (${(adjustedEquity * 100).toInt()}%)" else "semi-bluffing"
                    )
                    return PlayerAction(
                        playerId = playerState.id,
                        type = ActionType.BET,
                        amount = betAmount,
                        reason = reason
                    )
                }
            }
            return PlayerAction(
                playerId = playerState.id,
                type = ActionType.CHECK,
                reason = buildReason(reasonPrefix, "checking behind")
            )
        }

        if (adjustedEquity < potOdds && !isBluffing) {
            return PlayerAction(
                playerId = playerState.id,
                type = ActionType.FOLD,
                reason = buildReason(
                    reasonPrefix,
                    "pot odds (${(potOdds * 100).toInt()}%) higher than texture equity (${(adjustedEquity * 100).toInt()}%)"
                )
            )
        }

        if ((adjustedEquity > 0.80f || isBluffing) && playerState.chips > callAmount) {
            val raiseAmount = minOf(playerState.chips, gameState.currentHighestBet * 2)
            val reason = buildReason(
                reasonPrefix,
                if (adjustedEquity > 0.80f) "raising premium equity (${(adjustedEquity * 100).toInt()}%)" else "bluff raising"
            )
            return PlayerAction(
                playerId = playerState.id,
                type = ActionType.RAISE,
                amount = raiseAmount,
                reason = reason
            )
        }

        val actualCall = minOf(playerState.chips, callAmount)
        val actionType = if (actualCall == playerState.chips) ActionType.ALL_IN else ActionType.CALL
        val reason = buildReason(
            reasonPrefix,
            "pot odds (${(potOdds * 100).toInt()}%) justify call with equity (${(adjustedEquity * 100).toInt()}%)"
        )

        return PlayerAction(
            playerId = playerState.id,
            type = actionType,
            amount = actualCall,
            reason = reason
        )
    }

    private fun estimateEquityWithTexture(holeCards: List<Card>, communityCards: List<Card>): Float {
        if (holeCards.size < 2) return 0.2f

        if (communityCards.isEmpty()) {
            val r1 = holeCards[0].rank.value
            val r2 = holeCards[1].rank.value
            val isPair = r1 == r2
            val isSuited = holeCards[0].suit == holeCards[1].suit
            val highRank = maxOf(r1, r2)

            var score = highRank / 14.0f
            if (isPair) score += 0.35f
            if (isSuited) score += 0.10f
            return minOf(0.95f, score)
        }

        val fullCards = holeCards + communityCards
        val eval = HandEvaluator.evaluate(fullCards)
        var baseEquity = when (eval.handRank.value) {
            10 -> 1.0f  // Royal Flush
            9  -> 0.98f // Straight Flush
            8  -> 0.95f // Four of a Kind
            7  -> 0.90f // Full House
            6  -> 0.82f // Flush
            5  -> 0.75f // Straight
            4  -> 0.65f // Three of a Kind
            3  -> 0.50f // Two Pair
            2  -> 0.38f // One Pair
            else -> 0.18f
        }

        // Texture adjustments for board coordination
        val suitCounts = communityCards.groupBy { it.suit }.mapValues { it.value.size }
        val maxFlushSuitCount = suitCounts.values.maxOrNull() ?: 0

        // If 4 or 5 flush cards on board and player doesn't have flush, downgrade weak hands
        if (maxFlushSuitCount >= 4 && eval.handRank.value < 6) {
            baseEquity *= 0.60f
        }

        return minOf(1.0f, maxOf(0.05f, baseEquity))
    }

    private fun buildReason(prefix: String?, text: String): String {
        return if (prefix != null) "$prefix; $text" else text
    }
}
