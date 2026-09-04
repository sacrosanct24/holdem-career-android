package com.example.holdemcareer.domain.poker.engine

import com.example.holdemcareer.domain.poker.model.Card
import com.example.holdemcareer.domain.poker.model.Rank

object HandLoreGuide {

    fun getHandNicknameAndLore(holeCards: List<Card>): Pair<String, String>? {
        if (holeCards.size < 2) return null
        val r1 = holeCards[0].rank
        val r2 = holeCards[1].rank

        val isPair = r1 == r2
        val (high, low) = if (r1.value >= r2.value) Pair(r1, r2) else Pair(r2, r1)

        if (high == Rank.ACE && low == Rank.KING) {
            return Pair(
                "Big Slick (A-K)",
                "Why it's called 'Big Slick': Ace-King is a powerful starting hand, but because it is not yet a made pair, it can easily 'slip' through your fingers if the flop misses!"
            )
        }

        if (isPair && high == Rank.ACE) {
            return Pair(
                "Pocket Rockets / American Airlines (A-A)",
                "The absolute best starting hand in Texas Hold'em."
            )
        }

        if (isPair && high == Rank.KING) {
            return Pair(
                "Cowboys (K-K)",
                "The second strongest starting hand in Texas Hold'em."
            )
        }

        if (isPair && high == Rank.JACK) {
            return Pair(
                "Fishhooks (J-J)",
                "Named 'Fishhooks' because the letter 'J' resembles a fishing hook."
            )
        }

        if (high == Rank.TEN && low == Rank.TWO) {
            return Pair(
                "The Doyle Brunson (10-2)",
                "Named after poker legend Doyle Brunson, who won back-to-back WSOP Main Event titles (10-2 offsuit) in 1976 and 1977."
            )
        }

        return null
    }
}
