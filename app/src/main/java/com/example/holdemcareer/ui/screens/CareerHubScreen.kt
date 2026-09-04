package com.example.holdemcareer.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.holdemcareer.domain.career.CareerState
import com.example.holdemcareer.domain.poker.engine.GameState
import com.example.holdemcareer.domain.poker.engine.PlayerAction

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
