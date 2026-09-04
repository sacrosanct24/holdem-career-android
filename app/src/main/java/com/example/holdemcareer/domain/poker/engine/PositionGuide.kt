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
            "BTN" -> "BTN (Button): The best seat at the table. You act last on all post-flop streets, giving you maximum information and pot control.\n\n" +
                    "💡 Origin Lore: Named after the physical white plastic dealer button placed on live poker tables to track dealer rotation."
            "SB" -> "SB (Small Blind): Posts half a blind pre-flop. Acts second-to-last pre-flop but FIRST on all post-flop streets.\n\n" +
                    "💡 Origin Lore: Called 'blind' because this forced bet must be posted into the pot blindly before seeing your hole cards."
            "BB" -> "BB (Big Blind): Posts the full blind pre-flop. Acts last pre-flop when facing limps, but second post-flop.\n\n" +
                    "💡 Origin Lore: The main forced bet establishing the stakes of the table."
            "UTG" -> "UTG (Under The Gun): The first player to act pre-flop.\n\n" +
                    "💡 Origin Lore: Named 'Under The Gun' because you are forced to act first pre-flop before anyone else, putting you under intense pressure with 5 players waiting behind you."
            "MP" -> "MP (Middle Position): Acts after UTG. Can open slightly wider ranges than UTG but still faces late-position raises."
            "CO" -> "CO (Cutoff): The second-best seat, sitting right before the Button.\n\n" +
                    "💡 Origin Lore: Called 'Cutoff' because a raise from this seat 'cuts off' the Button from taking positional control of the hand!"
            else -> "Early Position: Requires tight, disciplined hand selection."
        }
    }
}
