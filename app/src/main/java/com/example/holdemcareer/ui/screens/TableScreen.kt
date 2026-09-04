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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F3818)) // Dark green felt
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header / Pot Info
            Text(
                text = "Hold'em Career — $50 Table",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700)
            )
            Text(
                text = "Total Pot: $${gameState.totalPot}",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Community Cards
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .background(Color(0x33000000), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                if (gameState.communityCards.isEmpty()) {
                    Text("Community Cards", color = Color.LightGray, fontSize = 12.sp)
                } else {
                    for (card in gameState.communityCards) {
                        PlayingCardView(card = card, isFaceUp = true)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // Players seats grid
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val playerRows = gameState.players.chunked(3)
                for (row in playerRows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (p in row) {
                            PlayerSeatView(
                                player = p,
                                isActive = gameState.players.indexOf(p) == gameState.activePlayerIndex && gameState.currentRound != BettingRound.ENDED,
                                isShowdown = gameState.currentRound == BettingRound.SHOWDOWN || gameState.currentRound == BettingRound.ENDED,
                                humanPlayerId = humanPlayerId
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.2f))

            // Action Logs Drawer
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x99000000))
            ) {
                LazyColumn(
                    modifier = Modifier.padding(6.dp),
                    reverseLayout = true
                ) {
                    items(gameState.actionLogs.reversed()) { log ->
                        Text(text = log, color = Color.White, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Controls or Next Hand Button
            if (gameState.currentRound == BettingRound.ENDED) {
                Button(
                    onClick = onNextHandClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("NEXT HAND", color = Color.Black, fontWeight = FontWeight.Bold)
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
    }
}

@Composable
fun PlayerSeatView(
    player: PlayerState,
    isActive: Boolean,
    isShowdown: Boolean,
    humanPlayerId: String
) {
    val borderColor = if (isActive) Color(0xFFFFD700) else Color.Transparent
    val isHuman = player.id == humanPlayerId

    Box(
        modifier = Modifier
            .width(110.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (player.isFolded) Color(0x44000000) else Color(0xAA111111))
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = player.name,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.White
            )
            Text(
                text = "$${player.chips}",
                fontSize = 11.sp,
                color = Color(0xFFFFD700)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                val showFaceUp = isHuman || isShowdown
                for (card in player.holeCards) {
                    PlayingCardView(card = card, isFaceUp = showFaceUp)
                }
            }

            if (player.currentBet > 0) {
                Text(
                    text = "Bet: $${player.currentBet}",
                    fontSize = 10.sp,
                    color = Color.LightGray
                )
            }
        }
    }
}
