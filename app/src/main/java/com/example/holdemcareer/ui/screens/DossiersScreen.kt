package com.example.holdemcareer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.holdemcareer.domain.ai.OpponentCatalog
import com.example.holdemcareer.domain.ai.OpponentProfile

@Composable
fun DossiersScreen(
    modifier: Modifier = Modifier
) {
    val opponents = OpponentCatalog.allOpponents

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF111111))
            .padding(12.dp)
    ) {
        Text(
            text = "33 Opponent Dossiers",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD700),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(opponents) { opp ->
                OpponentDossierCard(opp)
            }
        }
    }
}

@Composable
fun OpponentDossierCard(profile: OpponentProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${profile.name} — ${profile.title}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                text = "Style: ${profile.playStyleDescription}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFFD700),
                modifier = Modifier.padding(vertical = 2.dp)
            )
            Text(
                text = profile.bio,
                fontSize = 12.sp,
                color = Color.LightGray
            )

            Text(
                text = "Secret Tell: ${profile.secretTell}",
                fontSize = 12.sp,
                color = Color(0xFF81C784),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
