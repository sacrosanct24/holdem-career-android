package com.example.holdemcareer.domain.poker.engine

enum class GameMode(val displayName: String, val description: String) {
    NO_LIMIT_CASH("No-Limit Cash", "Standard cash game; bet any amount up to your stack."),
    FIXED_LIMIT_CASH("Fixed-Limit Cash", "Structured betting increments per round ($1/$2 preflop-flop, $2/$4 turn-river)."),
    SIT_AND_GO("Sit & Go Tournament", "8-handed tournament with escalating blinds. Last player standing takes the prize!")
}

data class BlindStructure(
    val smallBlind: Int,
    val bigBlind: Int,
    val ante: Int = 0
)

object TournamentBlinds {
    val levels = listOf(
        BlindStructure(10, 20, 0),
        BlindStructure(15, 30, 0),
        BlindStructure(25, 50, 5),
        BlindStructure(50, 100, 10),
        BlindStructure(100, 200, 25),
        BlindStructure(200, 400, 50)
    )
}
