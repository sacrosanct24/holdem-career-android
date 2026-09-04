package com.example.holdemcareer.domain.career

data class OpponentDossierState(
    val opponentId: String,
    val handsPlayedAgainst: Int = 0,
    val timesStackedByPlayer: Int = 0,
    val netProfitAgainstPlayer: Int = 0,
    val isDossierUnlockedLevel1: Boolean = false, // Bio & Playstyle
    val isDossierUnlockedLevel2: Boolean = false, // Real Stats
    val isDossierUnlockedLevel3: Boolean = false  // Secret Tell / Exploit
)

object DossierManager {

    fun updateDossier(
        current: OpponentDossierState,
        handsDelta: Int,
        stacked: Boolean,
        profitDelta: Int
    ): OpponentDossierState {
        val newHands = current.handsPlayedAgainst + handsDelta
        val newStacked = current.timesStackedByPlayer + (if (stacked) 1 else 0)
        val newProfit = current.netProfitAgainstPlayer + profitDelta

        val lvl1 = newHands >= 10 || current.isDossierUnlockedLevel1
        val lvl2 = newHands >= 50 || current.isDossierUnlockedLevel2
        val lvl3 = (newHands >= 50 && newStacked >= 1) || current.isDossierUnlockedLevel3

        return current.copy(
            handsPlayedAgainst = newHands,
            timesStackedByPlayer = newStacked,
            netProfitAgainstPlayer = newProfit,
            isDossierUnlockedLevel1 = lvl1,
            isDossierUnlockedLevel2 = lvl2,
            isDossierUnlockedLevel3 = lvl3
        )
    }
}
