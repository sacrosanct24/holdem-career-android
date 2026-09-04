package com.example.holdemcareer.domain.poker.engine

import com.example.holdemcareer.domain.poker.evaluator.HandEvaluator
import com.example.holdemcareer.domain.poker.model.Deck

class GameEngine {
    var state: GameState = GameState(
        handSeed = 0L,
        players = emptyList()
    )
        private set

    private var deck: Deck = Deck()
    private val potManager = PotManager()
    private val playersToActInRound = mutableSetOf<String>()

    fun startNewHand(
        initialPlayers: List<PlayerState>,
        dealerIndex: Int,
        smallBlind: Int,
        bigBlind: Int,
        seed: Long = System.currentTimeMillis()
    ) {
        require(initialPlayers.size >= 2) { "Need at least 2 players to start a hand" }

        deck = Deck(seed)
        potManager.reset()

        val numPlayers = initialPlayers.size
        val sbIndex = (dealerIndex + 1) % numPlayers
        val bbIndex = (dealerIndex + 2) % numPlayers

        // Deal 2 hole cards to each player
        val updatedPlayers = initialPlayers.map { player ->
            player.copy(
                holeCards = deck.deal(2),
                currentBet = 0,
                isFolded = false,
                isAllIn = false
            )
        }.toMutableList()

        // Post blinds
        val sbPlayer = updatedPlayers[sbIndex]
        val sbActual = minOf(sbPlayer.chips, smallBlind)
        updatedPlayers[sbIndex] = sbPlayer.copy(
            chips = sbPlayer.chips - sbActual,
            currentBet = sbActual,
            isAllIn = sbPlayer.chips - sbActual == 0
        )
        potManager.recordBet(sbPlayer.id, sbActual)

        val bbPlayer = updatedPlayers[bbIndex]
        val bbActual = minOf(bbPlayer.chips, bigBlind)
        updatedPlayers[bbIndex] = bbPlayer.copy(
            chips = bbPlayer.chips - bbActual,
            currentBet = bbActual,
            isAllIn = bbPlayer.chips - bbActual == 0
        )
        potManager.recordBet(bbPlayer.id, bbActual)

        playersToActInRound.clear()
        updatedPlayers.filter { !it.isFolded && !it.isAllIn }.forEach { playersToActInRound.add(it.id) }

        val startActorCandidate = if (numPlayers == 2) sbIndex else (dealerIndex + 3) % numPlayers
        val validFirstActor = findNextActivePlayerIndex(updatedPlayers, (startActorCandidate + numPlayers - 1) % numPlayers)

        val logs = mutableListOf<String>()
        logs.add("Hand #${seed} started. Dealer: ${updatedPlayers[dealerIndex].name}")
        logs.add("${updatedPlayers[sbIndex].name} posts SB ($sbActual)")
        logs.add("${updatedPlayers[bbIndex].name} posts BB ($bbActual)")

        state = GameState(
            handSeed = seed,
            players = updatedPlayers,
            communityCards = emptyList(),
            currentRound = BettingRound.PRE_FLOP,
            currentHighestBet = maxOf(sbActual, bbActual),
            totalPot = potManager.getTotalPot(),
            activePlayerIndex = validFirstActor,
            dealerIndex = dealerIndex,
            smallBlindIndex = sbIndex,
            bigBlindIndex = bbIndex,
            smallBlindAmount = smallBlind,
            bigBlindAmount = bigBlind,
            actionLogs = logs
        )

        checkAndAdvanceRoundIfNeeded()
    }

    fun applyAction(action: PlayerAction): Boolean {
        if (state.currentRound == BettingRound.ENDED || state.currentRound == BettingRound.SHOWDOWN) return false

        val activePlayer = state.players.getOrNull(state.activePlayerIndex) ?: return false
        if (activePlayer.id != action.playerId) return false
        if (activePlayer.isFolded || activePlayer.isAllIn) return false

        val updatedPlayers = state.players.toMutableList()
        val logs = state.actionLogs.toMutableList()
        var highestBet = state.currentHighestBet

        when (action.type) {
            ActionType.FOLD -> {
                updatedPlayers[state.activePlayerIndex] = activePlayer.copy(isFolded = true)
                potManager.foldPlayer(activePlayer.id)
                logs.add("${activePlayer.name} folds${reasonSuffix(action.reason)}")
            }
            ActionType.CHECK -> {
                logs.add("${activePlayer.name} checks${reasonSuffix(action.reason)}")
            }
            ActionType.CALL -> {
                val callAmount = highestBet - activePlayer.currentBet
                val actualBet = minOf(activePlayer.chips, callAmount)
                val newBetTotal = activePlayer.currentBet + actualBet
                val isAllIn = activePlayer.chips - actualBet == 0

                updatedPlayers[state.activePlayerIndex] = activePlayer.copy(
                    chips = activePlayer.chips - actualBet,
                    currentBet = newBetTotal,
                    isAllIn = isAllIn
                )
                potManager.recordBet(activePlayer.id, actualBet)
                logs.add("${activePlayer.name} calls $actualBet${reasonSuffix(action.reason)}")
            }
            ActionType.BET, ActionType.RAISE -> {
                val addBet = action.amount - activePlayer.currentBet
                val actualBet = minOf(activePlayer.chips, addBet)
                val newBetTotal = activePlayer.currentBet + actualBet
                val isAllIn = activePlayer.chips - actualBet == 0

                updatedPlayers[state.activePlayerIndex] = activePlayer.copy(
                    chips = activePlayer.chips - actualBet,
                    currentBet = newBetTotal,
                    isAllIn = isAllIn
                )
                potManager.recordBet(activePlayer.id, actualBet)
                highestBet = maxOf(highestBet, newBetTotal)

                // Re-open action for active players
                playersToActInRound.clear()
                updatedPlayers.filter { !it.isFolded && !it.isAllIn && it.id != activePlayer.id }
                    .forEach { playersToActInRound.add(it.id) }

                val actionName = if (action.type == ActionType.BET) "bets" else "raises to"
                logs.add("${activePlayer.name} $actionName $newBetTotal${reasonSuffix(action.reason)}")
            }
            ActionType.ALL_IN -> {
                val actualBet = activePlayer.chips
                val newBetTotal = activePlayer.currentBet + actualBet

                updatedPlayers[state.activePlayerIndex] = activePlayer.copy(
                    chips = 0,
                    currentBet = newBetTotal,
                    isAllIn = true
                )
                potManager.recordBet(activePlayer.id, actualBet)

                if (newBetTotal > highestBet) {
                    highestBet = newBetTotal
                    playersToActInRound.clear()
                    updatedPlayers.filter { !it.isFolded && !it.isAllIn && it.id != activePlayer.id }
                        .forEach { playersToActInRound.add(it.id) }
                }

                logs.add("${activePlayer.name} goes ALL-IN for $newBetTotal${reasonSuffix(action.reason)}")
            }
        }

        playersToActInRound.remove(activePlayer.id)

        // Find next active player
        val nextIndex = findNextActivePlayerIndex(updatedPlayers, state.activePlayerIndex)

        state = state.copy(
            players = updatedPlayers,
            currentHighestBet = highestBet,
            totalPot = potManager.getTotalPot(),
            activePlayerIndex = nextIndex,
            actionLogs = logs
        )

        checkAndAdvanceRoundIfNeeded()
        return true
    }

    private fun checkAndAdvanceRoundIfNeeded() {
        val activeNotFolded = state.players.filter { !it.isFolded }

        // 1. If only 1 player remains, everyone else folded -> award pot immediately
        if (activeNotFolded.size == 1) {
            awardUncontestedPot(activeNotFolded.first())
            return
        }

        // 2. Check if betting round is completed
        val canAct = activeNotFolded.filter { !it.isAllIn }
        val roundComplete = playersToActInRound.isEmpty() || canAct.isEmpty() || (canAct.size == 1 && activeNotFolded.count { it.isAllIn } == activeNotFolded.size - 1)

        if (roundComplete) {
            advanceToNextRound()
        }
    }

    private fun advanceToNextRound() {
        val updatedPlayers = state.players.map { it.copy(currentBet = 0) }.toMutableList()
        val community = state.communityCards.toMutableList()
        val logs = state.actionLogs.toMutableList()

        val nextRound = when (state.currentRound) {
            BettingRound.PRE_FLOP -> {
                community.addAll(deck.deal(3))
                logs.add("--- FLOP: ${community.joinToString(" ")} ---")
                BettingRound.FLOP
            }
            BettingRound.FLOP -> {
                val turnCard = deck.deal()
                community.add(turnCard)
                logs.add("--- TURN: $turnCard (Board: ${community.joinToString(" ")}) ---")
                BettingRound.TURN
            }
            BettingRound.TURN -> {
                val riverCard = deck.deal()
                community.add(riverCard)
                logs.add("--- RIVER: $riverCard (Board: ${community.joinToString(" ")}) ---")
                BettingRound.RIVER
            }
            BettingRound.RIVER -> {
                BettingRound.SHOWDOWN
            }
            BettingRound.SHOWDOWN, BettingRound.ENDED -> BettingRound.ENDED
        }

        if (nextRound == BettingRound.SHOWDOWN) {
            state = state.copy(
                players = updatedPlayers,
                communityCards = community,
                currentRound = nextRound,
                currentHighestBet = 0,
                actionLogs = logs
            )
            evaluateShowdown()
            return
        }

        playersToActInRound.clear()
        updatedPlayers.filter { !it.isFolded && !it.isAllIn }.forEach { playersToActInRound.add(it.id) }

        val firstActor = findNextActivePlayerIndex(updatedPlayers, state.dealerIndex)

        state = state.copy(
            players = updatedPlayers,
            communityCards = community,
            currentRound = nextRound,
            currentHighestBet = 0,
            activePlayerIndex = firstActor,
            actionLogs = logs
        )

        // If everyone left is all-in or no actions remain, advance automatically
        val canAct = updatedPlayers.filter { !it.isFolded && !it.isAllIn }
        if (canAct.isEmpty() || (canAct.size == 1 && updatedPlayers.count { !it.isFolded && it.isAllIn } == updatedPlayers.count { !it.isFolded } - 1)) {
            advanceToNextRound()
        }
    }

    private fun evaluateShowdown() {
        val pots = potManager.calculatePots(state.players.map { it.id }.toSet())
        val winnersSummary = mutableListOf<WinnerInfo>()
        val updatedPlayers = state.players.toMutableList()
        val logs = state.actionLogs.toMutableList()

        logs.add("--- SHOWDOWN ---")

        for ((index, pot) in pots.withIndex()) {
            val eligiblePlayers = updatedPlayers.filter { it.id in pot.eligiblePlayerIds && !it.isFolded }
            if (eligiblePlayers.isEmpty()) continue

            // Evaluate each eligible player's hand
            val evaluated = eligiblePlayers.map { player ->
                val fullHand = player.holeCards + state.communityCards
                val result = HandEvaluator.evaluate(fullHand)
                Pair(player, result)
            }

            val maxResult = evaluated.maxOf { it.second }
            val winningPlayers = evaluated.filter { it.second.compareTo(maxResult) == 0 }

            val potShare = pot.amount / winningPlayers.size
            for ((winner, result) in winningPlayers) {
                val pIdx = updatedPlayers.indexOfFirst { it.id == winner.id }
                if (pIdx != -1) {
                    val p = updatedPlayers[pIdx]
                    updatedPlayers[pIdx] = p.copy(chips = p.chips + potShare)
                }
                val potLabel = if (index == 0) "Main Pot" else "Side Pot #$index"
                logs.add("${winner.name} wins $potLabel ($potShare) with ${result.description}")
                winnersSummary.add(
                    WinnerInfo(
                        playerId = winner.id,
                        playerName = winner.name,
                        amountWon = potShare,
                        handDescription = result.description
                    )
                )
            }
        }

        state = state.copy(
            players = updatedPlayers,
            currentRound = BettingRound.ENDED,
            winnersSummary = winnersSummary,
            actionLogs = logs
        )
    }

    private fun awardUncontestedPot(winner: PlayerState) {
        val potAmount = potManager.getTotalPot()
        val updatedPlayers = state.players.toMutableList()
        val pIdx = updatedPlayers.indexOfFirst { it.id == winner.id }
        if (pIdx != -1) {
            updatedPlayers[pIdx] = winner.copy(chips = winner.chips + potAmount)
        }

        val logs = state.actionLogs.toMutableList()
        logs.add("${winner.name} wins $potAmount (uncontested)")

        state = state.copy(
            players = updatedPlayers,
            currentRound = BettingRound.ENDED,
            winnersSummary = listOf(
                WinnerInfo(
                    playerId = winner.id,
                    playerName = winner.name,
                    amountWon = potAmount,
                    handDescription = "Everyone else folded"
                )
            ),
            actionLogs = logs
        )
    }

    private fun findNextActivePlayerIndex(players: List<PlayerState>, currentIndex: Int): Int {
        val numPlayers = players.size
        for (i in 1..numPlayers) {
            val idx = (currentIndex + i) % numPlayers
            val p = players[idx]
            if (!p.isFolded && !p.isAllIn) {
                return idx
            }
        }
        return currentIndex
    }

    private fun reasonSuffix(reason: String?): String {
        return if (!reason.isNull_orEmpty()) " ($reason)" else ""
    }
}

private fun String?.isNull_orEmpty(): Boolean = this == null || this.isEmpty()
