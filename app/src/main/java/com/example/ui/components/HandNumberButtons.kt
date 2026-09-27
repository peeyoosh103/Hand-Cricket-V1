package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun HandNumberButtons(
    onNumberSelected: (Int) -> Unit,
    isEnabled: Boolean = true,
    isBatting: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: 1, 2, 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (num in 1..3) {
                HandButton(
                    number = num,
                    onClick = { onNumberSelected(num) },
                    enabled = isEnabled,
                    isBatting = isBatting,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Row 2: 4, 5, 6
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (num in 4..6) {
                HandButton(
                    number = num,
                    onClick = { onNumberSelected(num) },
                    enabled = isEnabled,
                    isBatting = isBatting,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HandButton(
    number: Int,
    onClick: () -> Unit,
    enabled: Boolean,
    isBatting: Boolean,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(),
        label = "button_scale"
    )

    // Vibrant number-specific sports gradients
    val (buttonGradient, accentColor, gestureIcon) = when (number) {
        1 -> Triple(
            listOf(Color(0xFF0F766E), Color(0xFF14B8A6)),
            Color(0xFF5EEAD4),
            "☝️"
        )
        2 -> Triple(
            listOf(Color(0xFF0369A1), Color(0xFF0EA5E9)),
            Color(0xFF7DD3FC),
            "✌️"
        )
        3 -> Triple(
            listOf(Color(0xFF4338CA), Color(0xFF6366F1)),
            Color(0xFFA5B4FC),
            "🤟"
        )
        4 -> Triple(
            listOf(Color(0xFF6D28D9), Color(0xFF8B5CF6)),
            Color(0xFFC4B5FD),
            "🖖"
        )
        5 -> Triple(
            listOf(Color(0xFFB45309), Color(0xFFF59E0B)),
            Color(0xFFFDE68A),
            "🖐️"
        )
        6 -> Triple(
            listOf(Color(0xFFBE123C), Color(0xFFF43F5E)),
            Color(0xFFFECDD3),
            "🤙"
        )
        else -> Triple(
            listOf(StadiumCardNavy, StadiumNavy),
            CricketPitchLightGreen,
            "✋"
        )
    }

    Card(
        modifier = modifier
            .height(78.dp)
            .scale(scale)
            .testTag("hand_button_$number")
            .shadow(
                elevation = if (enabled) 6.dp else 1.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = accentColor.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        if (enabled) buttonGradient else listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                    )
                )
                .border(
                    width = 1.5.dp,
                    color = if (enabled) accentColor.copy(alpha = 0.6f) else Color(0xFF334155),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "$number",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 28.sp,
                            color = if (enabled) Color.White else TextTertiary
                        )
                    )
                    Text(
                        text = gestureIcon,
                        fontSize = 18.sp
                    )
                }

                Text(
                    text = when {
                        number == 6 -> "MAX SIX"
                        number == 4 -> "FOUR"
                        number == 1 -> "SINGLE"
                        else -> "$number RUNS"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = if (enabled) accentColor else TextTertiary
                    )
                )
            }
        }
    }
}
