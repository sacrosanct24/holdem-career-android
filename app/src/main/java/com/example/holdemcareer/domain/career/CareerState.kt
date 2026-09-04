package com.example.holdemcareer.domain.career

data class CareerState(
    val currentRunBankroll: Int = 50,
    val startingBankrollBaseline: Int = 50,
    val currentTableBuyIn: Int = 50,
    val totalHandsPlayedAllTime: Int = 0,
    val totalHandsPlayedCurrentRun: Int = 0,
    val highestBankrollReached: Int = 50,
    val activeLoanAmount: Int = 0,
    val activeLoanOpponentId: String? = null,
    val unlockedMilestones: List<String> = emptyList()
) {
    val isBusted: Boolean get() = currentRunBankroll <= 0
}

data class Milestone(
    val id: String,
    val title: String,
    val description: String,
    val requiredBankroll: Int,
    val bonusStartingBankroll: Int
)

object MilestonesCatalog {
    val allMilestones = listOf(
        Milestone("m1", "First Orbit", "Earn a bankroll of $100", 100, 75),
        Milestone("m2", "High Roller", "Earn a bankroll of $250", 250, 100),
        Milestone("m3", "Table Boss", "Earn a bankroll of $500", 500, 200),
        Milestone("m4", "Vegas Legend", "Earn a bankroll of $1,000", 1000, 500)
    )
}
