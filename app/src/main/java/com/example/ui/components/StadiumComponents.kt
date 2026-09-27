package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Team
import com.example.ui.theme.*

@Composable
fun StadiumBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        StadiumDeepNavy,
                        StadiumNavy,
                        Color(0xFF0D1B2A),
                        Color(0xFF061A23)
                    )
                )
            )
    ) {
        // Stadium floodlight glow top accent
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x2210B981),
                            Color(0x112563EB),
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}

@Composable
fun TeamBadge(
    team: Team,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    showName: Boolean = false,
    fontSize: Int = 14
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        listOf(team.primaryColor, team.secondaryColor)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = team.flagEmoji,
                fontSize = (size.value * 0.52f).sp
            )
        }

        if (showName) {
            Column {
                Text(
                    text = team.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = fontSize.sp,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "#${team.worldRanking} World Rank",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
