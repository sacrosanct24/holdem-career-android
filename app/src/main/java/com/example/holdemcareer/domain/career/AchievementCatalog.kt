package com.example.holdemcareer.domain.career

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean = false
)

object AchievementCatalog {

    val allAchievements: List<Achievement> = listOf(
        Achievement("a1", "First Orbit", "Complete your first 10 hands at the table."),
        Achievement("a2", "The Unicorn", "Win a hand with a Royal Flush."),
        Achievement("a3", "Stack Master", "Stack an opponent's entire chip pile in a single hand."),
        Achievement("a4", "Intelligence Officer", "Unlock the Level 3 Secret Tell for any opponent."),
        Achievement("a5", "Phoenix", "Rebuild a positive bankroll from a Staking loan."),
        Achievement("a6", "Grinder", "Play 100 total hands in your career."),
        Achievement("a7", "Veteran", "Play 1,000 total hands in your career."),
        Achievement("a8", "Table Boss", "Reach the $1,000 buy-in table."),
        Achievement("a9", "Double Up", "Double your starting bankroll in a single session."),
        Achievement("a10", "Cooler", "Win a hand with a Full House or better.")
    ) + (11..46).map { idx ->
        Achievement(
            id = "a$idx",
            title = "Career Achievement #$idx",
            description = "Milestone achievement #$idx earned through career gameplay."
        )
    }
}
