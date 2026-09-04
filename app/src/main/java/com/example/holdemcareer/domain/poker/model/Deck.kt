package com.example.holdemcareer.domain.poker.model

import kotlin.random.Random

class Deck(val seed: Long? = null) {
    private val random: Random = seed?.let { Random(it) } ?: Random.Default
    private val cards = mutableListOf<Card>()

    init {
        resetAndShuffle()
    }

    fun resetAndShuffle() {
        cards.clear()
        for (suit in Suit.entries) {
            for (rank in Rank.entries) {
                cards.add(Card(rank, suit))
            }
        }
        cards.shuffle(random)
    }

    fun deal(): Card {
        check(cards.isNotEmpty()) { "Cannot deal from an empty deck" }
        return cards.removeAt(cards.size - 1)
    }

    fun deal(count: Int): List<Card> {
        return List(count) { deal() }
    }

    fun remainingCount(): Int = cards.size
}
