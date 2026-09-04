package com.example.holdemcareer.domain.poker.evaluator

import com.example.holdemcareer.domain.poker.model.Card
import com.example.holdemcareer.domain.poker.model.Rank

object HandEvaluator {

    fun evaluate(cards: List<Card>): HandEvaluationResult {
        require(cards.size in 5..7) { "Must evaluate between 5 and 7 cards, got ${cards.size}" }

        val combinations = generateCombinations(cards, 5)
        var bestResult: HandEvaluationResult? = null

        for (fiveCardHand in combinations) {
            val result = evaluateFiveCards(fiveCardHand)
            if (bestResult == null || result > bestResult) {
                bestResult = result
            }
        }

        return bestResult!!
    }

    private fun generateCombinations(cards: List<Card>, k: Int): List<List<Card>> {
        val result = mutableListOf<List<Card>>()
        fun combine(start: Int, current: MutableList<Card>) {
            if (current.size == k) {
                result.add(current.toList())
                return
            }
            for (i in start until cards.size) {
                current.add(cards[i])
                combine(i + 1, current)
                current.removeAt(current.size - 1)
            }
        }
        combine(0, mutableListOf())
        return result
    }

    private fun evaluateFiveCards(cards: List<Card>): HandEvaluationResult {
        val sortedCards = cards.sortedByDescending { it.rank.value }
        val isFlush = sortedCards.all { it.suit == sortedCards[0].suit }

        val straightTopRank = checkStraight(sortedCards)
        val isStraight = straightTopRank != null

        if (isFlush && isStraight) {
            return if (straightTopRank == Rank.ACE.value) {
                HandEvaluationResult(
                    handRank = HandRank.ROYAL_FLUSH,
                    tieBreakers = listOf(Rank.ACE.value),
                    bestFiveCards = sortedCards,
                    description = "Royal Flush"
                )
            } else {
                HandEvaluationResult(
                    handRank = HandRank.STRAIGHT_FLUSH,
                    tieBreakers = listOf(straightTopRank),
                    bestFiveCards = sortedCards,
                    description = "Straight Flush, ${rankName(straightTopRank)} High"
                )
            }
        }

        val rankCounts = sortedCards.groupBy { it.rank.value }
            .mapValues { it.value.size }
            .toList()
            .sortedWith(compareByDescending<Pair<Int, Int>> { it.second }.thenByDescending { it.first })

        val counts = rankCounts.map { it.second }

        if (counts == listOf(4, 1)) {
            val quadRank = rankCounts[0].first
            val kickerRank = rankCounts[1].first
            return HandEvaluationResult(
                handRank = HandRank.FOUR_OF_A_KIND,
                tieBreakers = listOf(quadRank, kickerRank),
                bestFiveCards = sortedCards,
                description = "Four of a Kind, ${rankName(quadRank)}s"
            )
        }

        if (counts == listOf(3, 2)) {
            val tripRank = rankCounts[0].first
            val pairRank = rankCounts[1].first
            return HandEvaluationResult(
                handRank = HandRank.FULL_HOUSE,
                tieBreakers = listOf(tripRank, pairRank),
                bestFiveCards = sortedCards,
                description = "Full House, ${rankName(tripRank)}s full of ${rankName(pairRank)}s"
            )
        }

        if (isFlush) {
            return HandEvaluationResult(
                handRank = HandRank.FLUSH,
                tieBreakers = sortedCards.map { it.rank.value },
                bestFiveCards = sortedCards,
                description = "Flush, ${rankName(sortedCards[0].rank.value)} High"
            )
        }

        if (isStraight) {
            return HandEvaluationResult(
                handRank = HandRank.STRAIGHT,
                tieBreakers = listOf(straightTopRank!!),
                bestFiveCards = sortedCards,
                description = "Straight, ${rankName(straightTopRank)} High"
            )
        }

        if (counts == listOf(3, 1, 1)) {
            val tripRank = rankCounts[0].first
            val kickers = listOf(rankCounts[1].first, rankCounts[2].first)
            return HandEvaluationResult(
                handRank = HandRank.THREE_OF_A_KIND,
                tieBreakers = listOf(tripRank) + kickers,
                bestFiveCards = sortedCards,
                description = "Three of a Kind, ${rankName(tripRank)}s"
            )
        }

        if (counts == listOf(2, 2, 1)) {
            val highPair = rankCounts[0].first
            val lowPair = rankCounts[1].first
            val kicker = rankCounts[2].first
            return HandEvaluationResult(
                handRank = HandRank.TWO_PAIR,
                tieBreakers = listOf(highPair, lowPair, kicker),
                bestFiveCards = sortedCards,
                description = "Two Pair, ${rankName(highPair)}s and ${rankName(lowPair)}s"
            )
        }

        if (counts == listOf(2, 1, 1, 1)) {
            val pairRank = rankCounts[0].first
            val kickers = listOf(rankCounts[1].first, rankCounts[2].first, rankCounts[3].first)
            return HandEvaluationResult(
                handRank = HandRank.ONE_PAIR,
                tieBreakers = listOf(pairRank) + kickers,
                bestFiveCards = sortedCards,
                description = "Pair of ${rankName(pairRank)}s"
            )
        }

        val kickers = sortedCards.map { it.rank.value }
        return HandEvaluationResult(
            handRank = HandRank.HIGH_CARD,
            tieBreakers = kickers,
            bestFiveCards = sortedCards,
            description = "High Card, ${rankName(kickers[0])}"
        )
    }

    private fun checkStraight(sortedCards: List<Card>): Int? {
        val ranks = sortedCards.map { it.rank.value }.distinct()
        if (ranks.size < 5) return null

        if (ranks[0] - ranks[4] == 4) {
            return ranks[0]
        }

        // Ace-low straight: A-5-4-3-2 -> ranks are [14, 5, 4, 3, 2]
        if (ranks == listOf(14, 5, 4, 3, 2)) {
            return 5
        }

        return null
    }

    private fun rankName(value: Int): String {
        return Rank.entries.firstOrNull { it.value == value }?.symbol ?: value.toString()
    }
}
