package com.example.holdemcareer.domain.poker.engine

data class Pot(
    val amount: Int,
    val eligiblePlayerIds: Set<String>
)

class PotManager {
    private val contributions = mutableMapOf<String, Int>()
    private val foldedPlayers = mutableSetOf<String>()

    fun recordBet(playerId: String, amount: Int) {
        if (amount <= 0) return
        contributions[playerId] = (contributions[playerId] ?: 0) + amount
    }

    fun foldPlayer(playerId: String) {
        foldedPlayers.add(playerId)
    }

    fun getPlayerContribution(playerId: String): Int = contributions[playerId] ?: 0

    fun getTotalPot(): Int = contributions.values.sum()

    fun reset() {
        contributions.clear()
        foldedPlayers.clear()
    }

    /**
     * Calculates main pot and any side pots based on all-in amounts and folded status.
     * Returns pots ordered from Main Pot (first) to Side Pots (subsequent).
     */
    fun calculatePots(allPlayerIds: Set<String>): List<Pot> {
        val activePlayers = allPlayerIds - foldedPlayers
        if (activePlayers.isEmpty()) return emptyList()

        // Work with a copy of player contributions
        val workingContributions = contributions.toMutableMap()
        val pots = mutableListOf<Pot>()

        while (workingContributions.values.any { it > 0 }) {
            // Find lowest positive contribution among eligible players
            val activeContributions = workingContributions.filter { it.key in activePlayers && it.value > 0 }
            if (activeContributions.isEmpty()) {
                // Leftover dead money from folded players
                val remainingDeadMoney = workingContributions.values.sum()
                if (remainingDeadMoney > 0 && pots.isNotEmpty()) {
                    // Add dead money to the last pot
                    val lastPot = pots.removeAt(pots.size - 1)
                    pots.add(lastPot.copy(amount = lastPot.amount + remainingDeadMoney))
                }
                break
            }

            val minContribution = activeContributions.values.minOrNull() ?: 0
            if (minContribution == 0) break

            var potAmount = 0
            val eligibleForThisPot = mutableSetOf<String>()

            for ((playerId, contr) in workingContributions.toList()) {
                if (contr > 0) {
                    val takes = minOf(contr, minContribution)
                    potAmount += takes
                    workingContributions[playerId] = contr - takes
                    if (playerId in activePlayers) {
                        eligibleForThisPot.add(playerId)
                    }
                }
            }

            if (potAmount > 0 && eligibleForThisPot.isNotEmpty()) {
                pots.add(Pot(amount = potAmount, eligiblePlayerIds = eligibleForThisPot))
            }
        }

        return pots
    }
}
