package com.example.holdemcareer.domain.career

data class CoachFeedback(
    val title: String,
    val summary: String,
    val severeLeaksCount: Int,
    val tips: List<String>
)

object CoachAnalyzer {

    fun analyzeSession(
        handsPlayed: Int,
        vpip: Float,
        pfr: Float,
        netProfit: Int
    ): CoachFeedback {
        val tips = mutableListOf<String>()
        var severeLeaks = 0

        if (vpip > 0.45f) {
            tips.add("Your VPIP is ${(vpip * 100).toInt()}%. You are playing too many hands pre-flop. Tighten up your starting hand selection.")
            severeLeaks++
        } else if (vpip < 0.15f) {
            tips.add("Your VPIP is ${(vpip * 100).toInt()}%. You are playing too timidly. Look for good position steals with suited connectors.")
        }

        if (vpip > 0.25f && pfr < 0.10f) {
            tips.add("Your PFR (${(pfr * 100).toInt()}%) is much lower than your VPIP (${(vpip * 100).toInt()}%). You are limping too much; raise when entering pots.")
            severeLeaks++
        }

        if (netProfit > 0) {
            tips.add("Great session! You walked away with +$$netProfit profit.")
        } else if (netProfit < 0) {
            tips.add("Session ended -$${-netProfit}. Review your hand replays to check if you called on bad pot odds.")
        }

        return CoachFeedback(
            title = if (severeLeaks == 0) "Solid Performance" else "Leaks Detected",
            summary = "Analyzed $handsPlayed hands. Found $severeLeaks key tactical leak(s).",
            severeLeaksCount = severeLeaks,
            tips = tips
        )
    }
}
