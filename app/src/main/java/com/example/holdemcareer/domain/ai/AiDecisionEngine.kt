package com.example.holdemcareer.domain.ai

import com.example.holdemcareer.domain.poker.engine.ActionType
import com.example.holdemcareer.domain.poker.engine.GameState
import com.example.holdemcareer.domain.poker.engine.PlayerAction
import com.example.holdemcareer.domain.poker.engine.PlayerState
import com.example.holdemcareer.domain.poker.evaluator.HandEvaluator
import com.example.holdemcareer.domain.poker.model.Card
import com.example.holdemcareer.domain.poker.model.Rank
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

        val rawEquity = estimateEquity(playerState.holeCards, gameState.communityCards)
        val isOnTilt = profile.currentTiltHands > 0

        // Adjusted equity based on tilt and aggression
        var adjustedEquity = rawEquity
        if (isOnTilt) {
            adjustedEquity = minOf(1.0f, rawEquity * 1.35f) // Tilt overestimates hand strength
        }

        val rng = Random.nextFloat()
        val isBluffing = rng < (profile.bluffFrequency * if (isOnTilt) 1.8f else 1.0f)

        val reasonPrefix = if (isOnTilt) "still steaming from last pot" else null

        // 1. Facing no bet (Check or Bet)
        if (callAmount <= 0) {
            if (adjustedEquity > 0.65f || isBluffing) {
                val betAmount = minOf(playerState.chips, maxOf(gameState.bigBlindAmount, pot / 2))
                if (betAmount > 0) {
                    val reason = buildReason(
                        reasonPrefix,
                        if (adjustedEquity > 0.65f) "value betting top hand strength (${(adjustedEquity * 100).toInt()}%)" else "semi-bluffing"
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

        // 2. Facing a bet / raise (Fold, Call, or Raise)
        if (adjustedEquity < potOdds && !isBluffing) {
            return PlayerAction(
                playerId = playerState.id,
                type = ActionType.FOLD,
                reason = buildReason(
                    reasonPrefix,
                    "pot odds (${(potOdds * 100).toInt()}%) higher than equity (${(adjustedEquity * 100).toInt()}%)"
                )
            )
        }

        // Raise condition
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

        // Call condition
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

    private fun estimateEquity(holeCards: List<Card>, communityCards: List<Card>): Float {
        if (holeCards.size < 2) return 0.2f

        if (communityCards.isEmpty()) {
            // Pre-flop quick heuristic
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

        // Post-flop evaluation
        val fullCards = holeCards + communityCards
        val eval = HandEvaluator.evaluate(fullCards)
        return when (eval.handRank.value) {
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
    }

    private fun buildReason(prefix: String?, text: String): String {
        return if (prefix != null) "$prefix; $text" else text
    }
}
