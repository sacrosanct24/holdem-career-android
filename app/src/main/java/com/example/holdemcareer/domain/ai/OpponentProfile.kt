package com.example.holdemcareer.domain.ai

data class OpponentProfile(
    val id: String,
    val name: String,
    val title: String,
    val bio: String,
    val playStyleDescription: String,
    val vpip: Float,            // e.g. 0.18 (tight) to 0.65 (loose)
    val pfr: Float,             // e.g. 0.05 (passive) to 0.35 (aggressive)
    val postFlopAggression: Float,
    val bluffFrequency: Float,
    val tiltSensitivity: Float, // Higher value = stays on tilt longer
    val secretTell: String,      // Strategy exploit
    val currentTiltHands: Int = 0
)
