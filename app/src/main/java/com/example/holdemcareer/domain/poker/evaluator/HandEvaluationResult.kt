package com.example.holdemcareer.domain.poker.evaluator

import com.example.holdemcareer.domain.poker.model.Card

data class HandEvaluationResult(
    val handRank: HandRank,
    val tieBreakers: List<Int>,
    val bestFiveCards: List<Card>,
    val description: String
) : Comparable<HandEvaluationResult> {

    override fun compareTo(other: HandEvaluationResult): Int {
        val rankCompare = this.handRank.value.compareTo(other.handRank.value)
        if (rankCompare != 0) return rankCompare

        val maxTieBreakers = minOf(this.tieBreakers.size, other.tieBreakers.size)
        for (i in 0 until maxTieBreakers) {
            val compare = this.tieBreakers[i].compareTo(other.tieBreakers[i])
            if (compare != 0) return compare
        }
        return 0
    }
}
