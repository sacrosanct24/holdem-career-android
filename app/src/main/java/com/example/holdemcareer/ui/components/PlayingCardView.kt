package com.example.holdemcareer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.holdemcareer.domain.poker.model.Card

@Composable
fun PlayingCardView(
    card: Card?,
    modifier: Modifier = Modifier,
    isFaceUp: Boolean = true
) {
    val shape = RoundedCornerShape(6.dp)

    if (card == null || !isFaceUp) {
        // Face down card back
        Box(
            modifier = modifier
                .width(44.dp)
                .height(62.dp)
                .clip(shape)
                .background(Color(0xFF1B365D))
                .border(1.5.dp, Color.White, shape)
        )
        return
    }

    val textColor = if (card.suit.isRed) Color(0xFFD32F2F) else Color(0xFF111111)

    Box(
        modifier = modifier
            .width(44.dp)
            .height(62.dp)
            .clip(shape)
            .background(Color.White)
            .border(1.dp, Color(0xFFCCCCCC), shape)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = card.rank.symbol,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = card.suit.symbol.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}
