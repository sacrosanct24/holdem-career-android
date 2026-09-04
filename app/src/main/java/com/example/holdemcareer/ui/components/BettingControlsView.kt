package com.example.holdemcareer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.holdemcareer.domain.poker.engine.ActionType
import com.example.holdemcareer.domain.poker.engine.PlayerAction

@Composable
fun BettingControlsView(
    humanPlayerId: String,
    currentHighestBet: Int,
    humanCurrentBet: Int,
    humanChips: Int,
    pot: Int,
    onActionSelected: (PlayerAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val callAmount = currentHighestBet - humanCurrentBet
    val minBet = maxOf(2, currentHighestBet * 2)
    val maxBet = humanCurrentBet + humanChips

    var sliderValue by remember(minBet, maxBet) {
        mutableFloatStateOf(minBet.coerceIn(0, maxBet).toFloat())
    }

    val selectedBetAmount = sliderValue.toInt()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (humanChips > callAmount && minBet < maxBet) {
            // Quick pot buttons row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = { sliderValue = minOf(maxBet.toFloat(), maxOf(minBet.toFloat(), (pot / 2).toFloat())) }
                ) {
                    Text("1/2 Pot", fontSize = 11.sp, color = Color.White)
                }

                OutlinedButton(
                    onClick = { sliderValue = minOf(maxBet.toFloat(), maxOf(minBet.toFloat(), pot.toFloat())) }
                ) {
                    Text("Pot", fontSize = 11.sp, color = Color.White)
                }

                OutlinedButton(
                    onClick = { sliderValue = maxBet.toFloat() }
                ) {
                    Text("All-In", fontSize = 11.sp, color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                }
            }

            // Bet / Raise Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Amount: $$selectedBetAmount",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = minBet.toFloat()..maxBet.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFFD700),
                        activeTrackColor = Color(0xFFFFD700)
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Primary Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Fold
            Button(
                onClick = { onActionSelected(PlayerAction(humanPlayerId, ActionType.FOLD)) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
            ) {
                Text("FOLD", fontWeight = FontWeight.Bold)
            }

            // Check or Call
            if (callAmount <= 0) {
                Button(
                    onClick = { onActionSelected(PlayerAction(humanPlayerId, ActionType.CHECK)) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("CHECK", fontWeight = FontWeight.Bold)
                }
            } else {
                val callText = if (callAmount >= humanChips) "ALL-IN ($humanChips)" else "CALL ($callAmount)"
                Button(
                    onClick = { onActionSelected(PlayerAction(humanPlayerId, ActionType.CALL, callAmount)) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Text(callText, fontWeight = FontWeight.Bold)
                }
            }

            // Bet / Raise
            if (humanChips > callAmount && selectedBetAmount > 0) {
                val actionType = if (currentHighestBet == 0) ActionType.BET else ActionType.RAISE
                val label = if (actionType == ActionType.BET) "BET $$selectedBetAmount" else "RAISE $$selectedBetAmount"
                Button(
                    onClick = { onActionSelected(PlayerAction(humanPlayerId, actionType, selectedBetAmount)) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57F17))
                ) {
                    Text(label, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
