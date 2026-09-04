package com.example.holdemcareer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.holdemcareer.domain.poker.engine.GameMode

@Composable
fun OptionsScreen(
    currentMode: GameMode,
    isKeepInDark: Boolean,
    isFourColorDeck: Boolean,
    onModeChanged: (GameMode) -> Unit,
    onKeepInDarkChanged: (Boolean) -> Unit,
    onFourColorDeckChanged: (Boolean) -> Unit,
    onResetCareerClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isHapticEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF111111))
            .padding(12.dp)
    ) {
        Text(
            text = "Game Menu & Accessibility Options",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD700),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Accessibility Options Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "♿ Accessibility Options",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFFFD700)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Four-Color Deck Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Four-Color Playing Deck", color = Color.White, fontWeight = FontWeight.SemiBold)
                                Text("Hearts: Red, Diamonds: Blue, Clubs: Green, Spades: Black for high contrast visibility.", fontSize = 11.sp, color = Color.LightGray)
                            }
                            Switch(
                                checked = isFourColorDeck,
                                onCheckedChange = onFourColorDeckChanged,
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD700))
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.DarkGray)

                        // Haptic Feedback Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Tactile Haptic Feedback", color = Color.White, fontWeight = FontWeight.SemiBold)
                                Text("Vibrate on bets, folds, and wins.", fontSize = 11.sp, color = Color.LightGray)
                            }
                            Switch(
                                checked = isHapticEnabled,
                                onCheckedChange = { isHapticEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD700))
                            )
                        }
                    }
                }
            }

            // Gameplay & Career Options Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🎮 Game & Career Settings",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFFFD700)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Keep Me in the Dark Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Keep Me in the Dark", color = Color.White, fontWeight = FontWeight.SemiBold)
                                Text("Hide saved opponent dossiers during hands and play everyone as strangers.", fontSize = 11.sp, color = Color.LightGray)
                            }
                            Switch(
                                checked = isKeepInDark,
                                onCheckedChange = onKeepInDarkChanged,
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD700))
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.DarkGray)

                        // Game Mode Selection
                        Text("Active Game Mode", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            GameMode.entries.forEach { mode ->
                                val isSelected = mode == currentMode
                                if (isSelected) {
                                    Button(
                                        onClick = { onModeChanged(mode) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                                    ) {
                                        Text(mode.displayName, color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = { onModeChanged(mode) }
                                    ) {
                                        Text(mode.displayName, color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Reset Career Data Option Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "⚠️ Reset Career Progress",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFD32F2F)
                        )
                        Text(
                            text = "Reset starting bankroll, dossiers, and hand replays to start a brand new run.",
                            fontSize = 11.sp,
                            color = Color.LightGray,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )

                        Button(
                            onClick = onResetCareerClicked,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("RESET CAREER", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
