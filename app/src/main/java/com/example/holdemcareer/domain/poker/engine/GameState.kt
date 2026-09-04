package com.example.holdemcareer.domain.poker.engine

import com.example.holdemcareer.domain.poker.model.Card

data class WinnerInfo(
    val playerId: String,
    val playerName: String,
    val amountWon: Int,
    val handDescription: String
)

data class GameState(
    val handSeed: Long,
    val players: List<PlayerState>,
    val communityCards: List<Card> = emptyList(),
    val currentRound: BettingRound = BettingRound.PRE_FLOP,
    val currentHighestBet: Int = 0,
    val totalPot: Int = 0,
    val activePlayerIndex: Int = 0,
    val dealerIndex: Int = 0,
    val smallBlindIndex: Int = 0,
    val bigBlindIndex: Int = 0,
    val smallBlindAmount: Int = 1,
    val bigBlindAmount: Int = 2,
    val gameMode: GameMode = GameMode.NO_LIMIT_CASH,
    val winnersSummary: List<WinnerInfo> = emptyList(),
    val actionLogs: List<String> = emptyList()
)
