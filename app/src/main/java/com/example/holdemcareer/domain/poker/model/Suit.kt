package com.example.holdemcareer.domain.poker.model

enum class Suit(val symbol: Char, val isRed: Boolean) {
    CLUBS('♣', isRed = false),
    DIAMONDS('♦', isRed = true),
    HEARTS('♥', isRed = true),
    SPADES('♠', isRed = false);

    override fun toString(): String = symbol.toString()
}
