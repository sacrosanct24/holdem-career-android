package com.example.holdemcareer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.holdemcareer.domain.poker.engine.BettingRound
import com.example.holdemcareer.domain.poker.engine.GameState
import com.example.holdemcareer.domain.poker.engine.PlayerAction
import com.example.holdemcareer.domain.poker.engine.PlayerState
import com.example.holdemcareer.ui.components.BettingControlsView
import com.example.holdemcareer.ui.components.PlayingCardView

@Composable
fun TableScreen(
    gameState: GameState,
    humanPlayerId: String,
    onActionSelected: (PlayerAction) -> Unit,
    onNextHandClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activePlayer = gameState.players.getOrNull(gameState.activePlayerIndex)
    val isHumanTurn = activePlayer?.id == humanPlayerId && gameState.currentRound != BettingRound.ENDED
    var showWhatHappenedDialog by remember { mutableStateOf(false) }

    val feltGradient = Brush.radialGradient(
        colors = listOf(Color(0xFF1B4D24), Color(0xFF0A240E)),
        radius = 800f
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF111111))
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Hold'em Career — $50 Table",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Oval Felt Table Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(100.dp))
                    .background(feltGradient)
                    .border(8.dp, Color(0xFF3E2723), RoundedCornerShape(100.dp)) // Wooden rail
                    .border(10.dp, Color(0xFFFFD700).copy(alpha = 0.3f), RoundedCornerShape(100.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Table Center: Pot & Community Cards
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x99000000))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "POT: $${gameState.totalPot}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFD700)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (gameState.communityCards.isEmpty()) {
                            Text("Waiting for flop...", color = Color.LightGray.copy(alpha = 0.6f), fontSize = 12.sp)
                        } else {
                            for (card in gameState.communityCards) {
                                PlayingCardView(card = card, isFaceUp = true)
                            }
                        }
                    }
                }

                // Radially placed seats (6-Max Layout)
                val seats = gameState.players
                if (seats.isNotEmpty()) {
                    // Seat 0: Human Player (Bottom Center)
                    val p0 = seats.getOrNull(0)
                    if (p0 != null) {
                        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                            PlayerSeatView(
                                player = p0,
                                isDealer = gameState.dealerIndex == 0,
                                isActive = gameState.activePlayerIndex == 0 && gameState.currentRound != BettingRound.ENDED,
                                isShowdown = gameState.currentRound == BettingRound.SHOWDOWN || gameState.currentRound == BettingRound.ENDED,
                                humanPlayerId = humanPlayerId
                            )
                        }
                    }

                    // Seat 1: Top Left
                    val p1 = seats.getOrNull(1)
                    if (p1 != null) {
                        Box(modifier = Modifier.align(Alignment.TopStart)) {
                            PlayerSeatView(
                                player = p1,
                                isDealer = gameState.dealerIndex == 1,
                                isActive = gameState.activePlayerIndex == 1 && gameState.currentRound != BettingRound.ENDED,
                                isShowdown = gameState.currentRound == BettingRound.SHOWDOWN || gameState.currentRound == BettingRound.ENDED,
                                humanPlayerId = humanPlayerId
                            )
                        }
                    }

                    // Seat 2: Top Center
                    val p2 = seats.getOrNull(2)
                    if (p2 != null) {
                        Box(modifier = Modifier.align(Alignment.TopCenter)) {
                            PlayerSeatView(
                                player = p2,
                                isDealer = gameState.dealerIndex == 2,
                                isActive = gameState.activePlayerIndex == 2 && gameState.currentRound != BettingRound.ENDED,
                                isShowdown = gameState.currentRound == BettingRound.SHOWDOWN || gameState.currentRound == BettingRound.ENDED,
                                humanPlayerId = humanPlayerId
                            )
                        }
                    }

                    // Seat 3: Top Right
                    val p3 = seats.getOrNull(3)
                    if (p3 != null) {
                        Box(modifier = Modifier.align(Alignment.TopEnd)) {
                            PlayerSeatView(
                                player = p3,
                                isDealer = gameState.dealerIndex == 3,
                                isActive = gameState.activePlayerIndex == 3 && gameState.currentRound != BettingRound.ENDED,
                                isShowdown = gameState.currentRound == BettingRound.SHOWDOWN || gameState.currentRound == BettingRound.ENDED,
                                humanPlayerId = humanPlayerId
                            )
                        }
                    }

                    // Seat 4: Bottom Right
                    val p4 = seats.getOrNull(4)
                    if (p4 != null) {
                        Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                            PlayerSeatView(
                                player = p4,
                                isDealer = gameState.dealerIndex == 4,
                                isActive = gameState.activePlayerIndex == 4 && gameState.currentRound != BettingRound.ENDED,
                                isShowdown = gameState.currentRound == BettingRound.SHOWDOWN || gameState.currentRound == BettingRound.ENDED,
                                humanPlayerId = humanPlayerId
                            )
                        }
                    }

                    // Seat 5: Bottom Left
                    val p5 = seats.getOrNull(5)
                    if (p5 != null) {
                        Box(modifier = Modifier.align(Alignment.BottomStart)) {
                            PlayerSeatView(
                                player = p5,
                                isDealer = gameState.dealerIndex == 5,
                                isActive = gameState.activePlayerIndex == 5 && gameState.currentRound != BettingRound.ENDED,
                                isShowdown = gameState.currentRound == BettingRound.SHOWDOWN || gameState.currentRound == BettingRound.ENDED,
                                humanPlayerId = humanPlayerId
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Controls or Between Hands Summary
            if (gameState.currentRound == BettingRound.ENDED) {
                val winnerInfo = gameState.winnersSummary.firstOrNull()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF222222), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (winnerInfo != null) {
                        Text(
                            text = "🎉 ${winnerInfo.playerName} wins $${winnerInfo.amountWon}!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                        Text(
                            text = winnerInfo.handDescription,
                            fontSize = 13.sp,
                            color = Color.LightGray,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = { showWhatHappenedDialog = true }
                        ) {
                            Text("What happened?", color = Color.White)
                        }

                        Button(
                            onClick = onNextHandClicked,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                        ) {
                            Text("DEAL NEXT HAND", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (isHumanTurn) {
                val humanPlayer = gameState.players.firstOrNull { it.id == humanPlayerId }
                if (humanPlayer != null) {
                    BettingControlsView(
                        humanPlayerId = humanPlayerId,
                        currentHighestBet = gameState.currentHighestBet,
                        humanCurrentBet = humanPlayer.currentBet,
                        humanChips = humanPlayer.chips,
                        pot = gameState.totalPot,
                        onActionSelected = onActionSelected
                    )
                }
            }
        }

        // "What Happened?" Explain Replay Dialog
        if (showWhatHappenedDialog) {
            AlertDialog(
                onDismissRequest = { showWhatHappenedDialog = false },
                title = { Text("Hand Replay & AI Explain", fontWeight = FontWeight.Bold) },
                text = {
                    LazyColumn(modifier = Modifier.height(300.dp)) {
                        items(gameState.actionLogs) { log ->
                            Text(
                                text = log,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showWhatHappenedDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
fun PlayerSeatView(
    player: PlayerState,
    isDealer: Boolean,
    isActive: Boolean,
    isShowdown: Boolean,
    humanPlayerId: String
) {
    val isHuman = player.id == humanPlayerId
    val borderColor = if (isActive) Color(0xFFFFD700) else Color(0x44FFFFFF)

    Box(
        modifier = Modifier
            .width(96.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (player.isFolded) Color(0x55000000) else Color(0xDD1E1E1E))
            .border(2.dp, borderColor, RoundedCornerShape(10.dp))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = player.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.White
                )
                if (isDealer) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD700)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("D", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }

            Text(
                text = "$${player.chips}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                val showCards = isHuman || isShowdown
                for (card in player.holeCards) {
                    PlayingCardView(card = card, isFaceUp = showCards)
                }
            }

            if (player.currentBet > 0) {
                Text(
                    text = "Bet: $${player.currentBet}",
                    fontSize = 10.sp,
                    color = Color(0xFF81C784)
                )
            }
        }
    }
}
