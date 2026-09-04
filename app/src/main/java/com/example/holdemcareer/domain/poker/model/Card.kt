package com.example.holdemcareer.domain.poker.model

data class Card(val rank: Rank, val suit: Suit) : Comparable<Card> {
    override fun compareTo(other: Card): Int {
        return this.rank.value.compareTo(other.rank.value)
    }

    override fun toString(): String {
        return "${rank.symbol}${suit.symbol}"
    }

    companion object {
        fun fromString(str: String): Card {
            require(str.length in 2..3) { "Invalid card string: $str" }
            val rankStr = str.dropLast(1)
            val suitChar = str.last()

            val rank = Rank.entries.first {
                it.symbol.equals(rankStr, ignoreCase = true) ||
                        (rankStr.equals("T", ignoreCase = true) && it == Rank.TEN)
            }
            val suit = Suit.entries.first {
                it.symbol == suitChar ||
                        it.name.startsWith(suitChar.uppercase())
            }

            return Card(rank, suit)
        }
    }
}
