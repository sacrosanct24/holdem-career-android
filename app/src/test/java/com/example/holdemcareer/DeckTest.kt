package com.example.holdemcareer

import com.example.holdemcareer.domain.poker.model.Deck
import org.junit.Assert.assertEquals
import org.junit.Test

class DeckTest {

    @Test
    fun testDeckDeals52UniqueCards() {
        val deck = Deck()
        val cards = deck.deal(52)
        assertEquals(52, cards.size)
        assertEquals(52, cards.toSet().size)
        assertEquals(0, deck.remainingCount())
    }

    @Test
    fun testSeedReproducibility() {
        val seed = 123456789L
        val deck1 = Deck(seed)
        val deck2 = Deck(seed)

        val cards1 = deck1.deal(10)
        val cards2 = deck2.deal(10)

        assertEquals(cards1, cards2)
    }
}
