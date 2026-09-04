package com.example.holdemcareer.domain.poker.engine

import com.example.holdemcareer.domain.poker.model.Card

data class PlayerState(
    val id: String,
    val name: String,
    val chips: Int,
    val currentBet: Int = 0,
    val holeCards: List<Card> = emptyList(),
    val isFolded: Boolean = false,
    val isAllIn: Boolean = false,
    val isHuman: Boolean = false,
    val seatIndex: Int = 0,
    val avatarId: String = ""
) {
    val isActiveInHand: Boolean get() = !isFolded && chips > 0 && !isAllIn
}
