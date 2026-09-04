package com.example.holdemcareer.domain.ai

object OpponentCatalog {

    val allOpponents: List<OpponentProfile> = listOf(
        OpponentProfile(
            id = "marcus",
            name = "Marcus",
            title = "Retired Accountant",
            bio = "Plays 9 hands a night and has never bluffed a river in his life.",
            playStyleDescription = "Nit / Extremely Tight-Passive",
            vpip = 0.12f, pfr = 0.05f, postFlopAggression = 0.20f, bluffFrequency = 0.02f,
            tiltSensitivity = 0.1f,
            secretTell = "When Marcus bets the river, fold everything except the nuts."
        ),
        OpponentProfile(
            id = "dolores",
            name = "Dolores",
            title = "The Curious Station",
            bio = "Wants to see every flop and will pay you for the privilege.",
            playStyleDescription = "Calling Station / Loose-Passive",
            vpip = 0.65f, pfr = 0.08f, postFlopAggression = 0.25f, bluffFrequency = 0.05f,
            tiltSensitivity = 0.2f,
            secretTell = "Never bluff Dolores. Value-bet top pair thinly on all three streets."
        ),
        OpponentProfile(
            id = "vince",
            name = "Vince",
            title = "Hothead Pro",
            bio = "Solid game until he gets stacked, then watch out for 20 wild orbits.",
            playStyleDescription = "TAG / Tilt-Prone Aggressor",
            vpip = 0.24f, pfr = 0.19f, postFlopAggression = 0.65f, bluffFrequency = 0.22f,
            tiltSensitivity = 0.9f,
            secretTell = "Stack Vince once and check-raise him light for the next 20 hands while he steams."
        ),
        OpponentProfile(
            id = "elena",
            name = "Elena",
            title = "The Math Prodigy",
            bio = "Runs unexploitable ranges and punishes loose limpers brutally.",
            playStyleDescription = "GTO / Tight-Aggressive",
            vpip = 0.22f, pfr = 0.18f, postFlopAggression = 0.70f, bluffFrequency = 0.25f,
            tiltSensitivity = 0.05f,
            secretTell = "Elena over-folds to check-raises on dry flop boards when out of position."
        ),
        OpponentProfile(
            id = "slick_sam",
            name = "Slick Sam",
            title = "The River Gambler",
            bio = "Loves double-barrel bluffs and high-stakes pressure.",
            playStyleDescription = "Maniac / Loose-Aggressive",
            vpip = 0.50f, pfr = 0.38f, postFlopAggression = 0.85f, bluffFrequency = 0.40f,
            tiltSensitivity = 0.6f,
            secretTell = "Trap Sam with top pair by checking the turn to induce a river shove."
        ),
        OpponentProfile(
            id = "penny",
            name = "Penny",
            title = "The Cautious Student",
            bio = "Reads poker books religiously but panics when the pot gets big.",
            playStyleDescription = "Fit-or-Fold / Weak-Tight",
            vpip = 0.18f, pfr = 0.10f, postFlopAggression = 0.30f, bluffFrequency = 0.08f,
            tiltSensitivity = 0.3f,
            secretTell = "Bet half the pot on the flop whenever Penny checks; she folds 80% of hands."
        ),
        OpponentProfile(
            id = "big_dave",
            name = "Big Dave",
            title = "The Local Legend",
            bio = "Plays by gut feel and loves suited connectors.",
            playStyleDescription = "Loose-Passive Gambler",
            vpip = 0.42f, pfr = 0.12f, postFlopAggression = 0.45f, bluffFrequency = 0.15f,
            tiltSensitivity = 0.4f,
            secretTell = "Dave inflates pots with draws; raise him heavily pre-flop to isolate."
        ),
        OpponentProfile(
            id = "charlotte",
            name = "Charlotte",
            title = "The Ice Queen",
            bio = "Never shows emotion. Plays premium hands with total clinical precision.",
            playStyleDescription = "Rock / Tight-Aggressive",
            vpip = 0.16f, pfr = 0.14f, postFlopAggression = 0.60f, bluffFrequency = 0.10f,
            tiltSensitivity = 0.0f,
            secretTell = "When Charlotte calls a raise, she has a set or better. Proceed with caution."
        ),
        OpponentProfile(
            id = "rex",
            name = "Rex",
            title = "The Cowboy",
            bio = "Raises any two cards on the button if it's folded to him.",
            playStyleDescription = "Positional Bully",
            vpip = 0.35f, pfr = 0.28f, postFlopAggression = 0.75f, bluffFrequency = 0.30f,
            tiltSensitivity = 0.5f,
            secretTell = "3-bet Rex from the blinds to neutralize his button aggression."
        ),
        OpponentProfile(
            id = "maya",
            name = "Maya",
            title = "The Trap Master",
            bio = "Loves slowplaying big hands pre-flop and springing check-raises.",
            playStyleDescription = "Tricky / Selective-Aggressive",
            vpip = 0.20f, pfr = 0.12f, postFlopAggression = 0.55f, bluffFrequency = 0.18f,
            tiltSensitivity = 0.2f,
            secretTell = "Check behind Maya on the flop when she limps pre-flop with monster hands."
        )
    ) + (11..33).map { index ->
        OpponentProfile(
            id = "opp_$index",
            name = "Player $index",
            title = "Circuit Grinder #$index",
            bio = "A seasoned regular at the $index ladder.",
            playStyleDescription = "Standard Semi-TAG",
            vpip = 0.22f + (index % 5) * 0.03f,
            pfr = 0.12f + (index % 4) * 0.03f,
            postFlopAggression = 0.45f + (index % 3) * 0.10f,
            bluffFrequency = 0.12f + (index % 4) * 0.05f,
            tiltSensitivity = 0.3f,
            secretTell = "Watch out for Player $index's river check-raises."
        )
    }

    fun getById(id: String): OpponentProfile {
        return allOpponents.firstOrNull { it.id == id } ?: allOpponents.first()
    }
}
