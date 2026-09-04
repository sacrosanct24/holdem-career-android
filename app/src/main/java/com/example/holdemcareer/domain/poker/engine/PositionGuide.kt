package com.example.holdemcareer.domain.poker.engine

object PositionGuide {

    fun getPositionLabel(seatIndex: Int, dealerIndex: Int, totalPlayers: Int): String {
        if (totalPlayers <= 0) return "BTN"
        val relativePos = (seatIndex - dealerIndex + totalPlayers) % totalPlayers
        return when (relativePos) {
            0 -> "BTN"
            1 -> "SB"
            2 -> "BB"
            3 -> "UTG"
            4 -> "MP"
            5 -> "CO"
            else -> "EP"
        }
    }

    fun getPositionExplanation(label: String): String {
        return when (label) {
            "BTN" -> "BTN (Button): The best position at the table. You act last on all post-flop streets, giving you maximum information and pot control."
            "SB" -> "SB (Small Blind): Posts half a blind pre-flop. Acts second-to-last pre-flop but FIRST on all post-flop streets (out of position)."
            "BB" -> "BB (Big Blind): Posts the full blind pre-flop. Acts last pre-flop when facing limps, but second post-flop."
            "UTG" -> "UTG (Under The Gun): The first player to act pre-flop. Requires tighter starting hands because 5 players act behind you."
            "MP" -> "MP (Middle Position): Acts after UTG. Can open slightly wider ranges than UTG but still faces late-position raises."
            "CO" -> "CO (Cutoff): The second-best seat, right before the Button. Excellent position for isolation raises and stealing blinds."
            else -> "Early Position: Requires tight, disciplined hand selection."
        }
    }
}
