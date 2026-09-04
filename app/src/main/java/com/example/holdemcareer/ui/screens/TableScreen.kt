package com.example.holdemcareer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.holdemcareer.domain.poker.engine.BettingRound
import com.example.holdemcareer.domain.poker.engine.GameState
import com.example.holdemcareer.domain.poker.engine.HandLoreGuide
import com.example.holdemcareer.domain.poker.engine.PlayerAction
import com.example.holdemcareer.domain.poker.engine.PlayerState
import com.example.holdemcareer.domain.poker.engine.PositionGuide
import com.example.holdemcareer.domain.poker.evaluator.HandEvaluator
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
    var selectedPositionExplanation by remember { mutableStateOf<String?>(null) }
    var showHandHelpDialog by remember { mutableStateOf(false) }

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
                text = "Hold'em Career — $50 Table (${gameState.gameMode.displayName})",
                fontSize = 15.sp,
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
                val totalPlayers = seats.size
                if (seats.isNotEmpty()) {
                    val seatAlignments = listOf(
                        Alignment.BottomCenter,
                        Alignment.TopStart,
                        Alignment.TopCenter,
                        Alignment.TopEnd,
                        Alignment.BottomEnd,
                        Alignment.BottomStart
                    )

                    seats.forEachIndexed { index, p ->
                        val align = seatAlignments.getOrElse(index) { Alignment.Center }
                        val posLabel = PositionGuide.getPositionLabel(index, gameState.dealerIndex, totalPlayers)
                        Box(modifier = Modifier.align(align)) {
                            PlayerSeatView(
                                player = p,
                                positionLabel = posLabel,
                                isDealer = gameState.dealerIndex == index,
                                isActive = gameState.activePlayerIndex == index && gameState.currentRound != BettingRound.ENDED,
                                isShowdown = gameState.currentRound == BettingRound.SHOWDOWN || gameState.currentRound == BettingRound.ENDED,
                                humanPlayerId = humanPlayerId,
                                onPositionPillClicked = { label ->
                                    selectedPositionExplanation = PositionGuide.getPositionExplanation(label)
                                },
                                onHandHelpClicked = {
                                    showHandHelpDialog = true
                                }
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

        // Position Pill Explanation Tooltip Dialog
        if (selectedPositionExplanation != null) {
            AlertDialog(
                onDismissRequest = { selectedPositionExplanation = null },
                title = { Text("Position Guide", fontWeight = FontWeight.Bold, color = Color(0xFFFFD700)) },
                text = {
                    Text(
                        text = selectedPositionExplanation!!,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { selectedPositionExplanation = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                    ) {
                        Text("Got it", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // Hand Strength "?" Help Dialog
        if (showHandHelpDialog) {
            val human = gameState.players.firstOrNull { it.id == humanPlayerId }
            val holeCards = human?.holeCards ?: emptyList()
            val fullCards = holeCards + gameState.communityCards
            val eval = if (fullCards.size >= 5) HandEvaluator.evaluate(fullCards) else null
            val lore = HandLoreGuide.getHandNicknameAndLore(holeCards)

            AlertDialog(
                onDismissRequest = { showHandHelpDialog = false },
                title = { Text("Your Hand & Lore Guide", fontWeight = FontWeight.Bold, color = Color(0xFFFFD700)) },
                text = {
                    Column {
                        if (lore != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF2E2A12))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text("🌟 Hand Nickname: ${lore.first}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFFFD700))
                                    Text(lore.second, fontSize = 11.sp, color = Color.LightGray, modifier = Modifier.padding(top = 2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (eval != null) {
                            Text("Current Combination: ${eval.description}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            Text("Hand Rank: ${eval.handRank.displayName}", fontSize = 12.sp, color = Color(0xFF81C784), modifier = Modifier.padding(vertical = 2.dp))
                        } else {
                            Text("Hole cards dealt. Community cards will determine your 5-card combination.", fontSize = 12.sp, color = Color.LightGray)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Texas Hold'em Hand Hierarchy:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFFFD700))
                        Text("1. Royal Flush\n2. Straight Flush\n3. Four of a Kind\n4. Full House\n5. Flush\n6. Straight\n7. Three of a Kind\n8. Two Pair\n9. One Pair\n10. High Card", fontSize = 11.sp, color = Color.LightGray)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showHandHelpDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                    ) {
                        Text("Close", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun PlayerSeatView(
    player: PlayerState,
    positionLabel: String,
    isDealer: Boolean,
    isActive: Boolean,
    isShowdown: Boolean,
    humanPlayerId: String,
    onPositionPillClicked: (String) -> Unit = {},
    onHandHelpClicked: () -> Unit = {}
) {
    val isHuman = player.id == humanPlayerId
    val borderColor = if (isActive) Color(0xFFFFD700) else Color(0x44FFFFFF)

    Box(
        modifier = Modifier
            .width(100.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (player.isFolded) Color(0x55000000) else Color(0xDD1E1E1E))
            .border(2.dp, borderColor, RoundedCornerShape(10.dp))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Position Pill & Dealer Badge Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Position Pill (Clickable for explanation)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF333333))
                        .clickable { onPositionPillClicked(positionLabel) }
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = positionLabel,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFD700)
                    )
                }

                Text(
                    text = player.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.White
                )

                if (isDealer) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD700)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("D", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }

            Text(
                text = "$${player.chips}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700)
            )

            // Hole cards row with "?" help button for human
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                val showCards = isHuman || isShowdown
                for (card in player.holeCards) {
                    PlayingCardView(card = card, isFaceUp = showCards)
                }

                if (isHuman) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1565C0))
                            .clickable { onHandHelpClicked() }
                            .semantics { contentDescription = "Hand help and rankings guide" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("?", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
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
