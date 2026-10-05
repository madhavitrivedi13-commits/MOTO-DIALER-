package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.example.ui.theme.AvatarPalette

@Composable
fun ContactAvatar(
    name: String,
    initials: String,
    avatarColorIndex: Int,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    val palette = AvatarPalette[kotlin.math.abs(avatarColorIndex) % AvatarPalette.size]
    val gradientBrush = Brush.linearGradient(
        colors = listOf(palette.first, palette.second)
    )

    val fontSize = (size.value * 0.42f).sp

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials.ifBlank { name.take(1).uppercase().ifBlank { "?" } },
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
