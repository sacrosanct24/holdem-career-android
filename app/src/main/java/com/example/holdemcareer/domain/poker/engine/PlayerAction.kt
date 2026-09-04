package com.example.holdemcareer.domain.poker.engine

enum class ActionType {
    FOLD,
    CHECK,
    CALL,
    BET,
    RAISE,
    ALL_IN
}

data class PlayerAction(
    val playerId: String,
    val type: ActionType,
    val amount: Int = 0,
    val reason: String? = null
)
