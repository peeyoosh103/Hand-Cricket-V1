package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BallEvent
import com.example.ui.theme.*

@Composable
fun BallHistoryRow(
    ballEvents: List<BallEvent>,
    modifier: Modifier = Modifier
) {
    val recentBalls = ballEvents.takeLast(12).reversed()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "THIS OVER:",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                fontSize = 10.sp
            )
        )

        if (recentBalls.isEmpty()) {
            Text(
                text = "First ball coming up...",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextTertiary,
                    fontSize = 12.sp
                )
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(recentBalls) { event ->
                    BallChip(event = event)
                }
            }
        }
    }
}

@Composable
fun BallChip(
    event: BallEvent,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when {
        event.isWicket -> Triple(CricketWicketRed, Color.White, "W")
        event.runsScored == 6 -> Triple(CricketGold, Color.Black, "6")
        event.runsScored == 4 -> Triple(CricketBoundaryPurple, Color.White, "4")
        event.runsScored == 0 -> Triple(Color(0xFF334155), Color.White, "•")
        else -> Triple(CricketPitchGreen, Color.Black, "${event.runsScored}")
    }

    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = textColor,
                fontSize = 12.sp
            )
        )
    }
}
