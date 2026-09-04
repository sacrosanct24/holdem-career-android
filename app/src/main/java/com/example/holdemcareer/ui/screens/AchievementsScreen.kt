package com.example.holdemcareer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.holdemcareer.domain.career.AchievementCatalog

@Composable
fun AchievementsScreen(
    modifier: Modifier = Modifier,
    unlockedIds: List<String> = emptyList()
) {
    val achievements = AchievementCatalog.allAchievements

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF111111))
            .padding(12.dp)
    ) {
        Text(
            text = "46 Local Achievements (${unlockedIds.size}/46 Unlocked)",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD700),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(achievements) { ach ->
                val isUnlocked = ach.id in unlockedIds || ach.id == "a1" // Default first unlocked for test
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUnlocked) Color(0xFF1B3621) else Color(0xFF222222)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ach.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isUnlocked) Color(0xFFFFD700) else Color.White
                            )
                            Text(
                                text = ach.description,
                                fontSize = 12.sp,
                                color = Color.LightGray,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Text(
                            text = if (isUnlocked) "UNLOCKED" else "LOCKED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) Color(0xFF81C784) else Color.Gray,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
