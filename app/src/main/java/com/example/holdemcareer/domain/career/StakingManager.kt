package com.example.holdemcareer.domain.career

import com.example.holdemcareer.domain.ai.OpponentProfile

data class StakingOffer(
    val staker: OpponentProfile,
    val stakedAmount: Int = 50,
    val profitCutPercent: Float = 0.30f, // 30% cut
    val termsMessage: String
)

object StakingManager {

    fun generateOfferForBustedPlayer(staker: OpponentProfile): StakingOffer {
        val cutText = "${(0.30f * 100).toInt()}%"
        return StakingOffer(
            staker = staker,
            stakedAmount = 50,
            profitCutPercent = 0.30f,
            termsMessage = "${staker.name} says: 'Tough night kid. I'll stake your $50 buy-in right now, but I get $cutText of every winning pot until you pay me back. Deal?'"
        )
    }
}
