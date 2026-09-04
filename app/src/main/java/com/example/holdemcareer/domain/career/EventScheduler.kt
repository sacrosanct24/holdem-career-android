package com.example.holdemcareer.domain.career

import com.example.holdemcareer.domain.poker.engine.GameMode

data class SpecialEvent(
    val id: String,
    val title: String,
    val description: String,
    val gameMode: GameMode,
    val buyIn: Int,
    val startingStack: Int,
    val handsInterval: Int
)

object SpecialEventsCatalog {

    val deepEnd = SpecialEvent(
        id = "deep_end",
        title = "The Deep End",
        description = "Double starting stacks ($100) with a slow, deep-stack blind clock.",
        gameMode = GameMode.NO_LIMIT_CASH,
        buyIn = 50,
        startingStack = 100,
        handsInterval = 150
    )

    val shootout = SpecialEvent(
        id = "shootout",
        title = "The Shootout Tournament",
        description = "32 runners across 4 tables. Win your table to advance to the final table!",
        gameMode = GameMode.SIT_AND_GO,
        buyIn = 100,
        startingStack = 150,
        handsInterval = 300
    )

    fun checkActiveEvent(totalHandsPlayed: Int): SpecialEvent? {
        if (totalHandsPlayed > 0 && totalHandsPlayed % shootout.handsInterval == 0) {
            return shootout
        }
        if (totalHandsPlayed > 0 && totalHandsPlayed % deepEnd.handsInterval == 0) {
            return deepEnd
        }
        return null
    }
}
