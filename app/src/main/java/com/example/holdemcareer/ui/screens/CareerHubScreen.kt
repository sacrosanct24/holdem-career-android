package com.example.holdemcareer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.holdemcareer.domain.career.CareerState
import com.example.holdemcareer.domain.poker.engine.GameState
import com.example.holdemcareer.domain.poker.engine.PlayerAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerHubScreen(
    careerState: CareerState,
    gameState: GameState,
    humanPlayerId: String,
    onActionSelected: (PlayerAction) -> Unit,
    onNextHandClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bankroll: $${careerState.currentRunBankroll}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFFFD700)
                        )
                        Text(
                            text = "Peak: $${careerState.highestBankrollReached}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = Color.LightGray
                        )
                        Text(
                            text = "Hands: ${careerState.totalHandsPlayedAllTime}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B1B1B))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF111111)) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {},
                    label = { Text("Table", fontWeight = FontWeight.Bold, color = if (selectedTab == 0) Color(0xFFFFD700) else Color.Gray) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {},
                    label = { Text("Dossiers", fontWeight = FontWeight.Bold, color = if (selectedTab == 1) Color(0xFFFFD700) else Color.Gray) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {},
                    label = { Text("Events", fontWeight = FontWeight.Bold, color = if (selectedTab == 2) Color(0xFFFFD700) else Color.Gray) }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {},
                    label = { Text("Achievements", fontWeight = FontWeight.Bold, color = if (selectedTab == 3) Color(0xFFFFD700) else Color.Gray) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> TableScreen(
                    gameState = gameState,
                    humanPlayerId = humanPlayerId,
                    onActionSelected = onActionSelected,
                    onNextHandClicked = onNextHandClicked
                )
                1 -> DossiersScreen()
                2 -> EventsScreen()
                3 -> AchievementsScreen(unlockedIds = careerState.unlockedMilestones)
            }
        }
    }
}
