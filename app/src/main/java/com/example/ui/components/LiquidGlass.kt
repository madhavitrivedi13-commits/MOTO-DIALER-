package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkLiquidBorder
import com.example.ui.theme.DarkLiquidCard
import com.example.ui.theme.GlassWhiteHigh
import com.example.ui.theme.GlassWhiteLow
import com.example.ui.theme.GlassWhiteMid
import com.example.ui.theme.LightLiquidBorder
import com.example.ui.theme.LightLiquidCard

/**
 * Applies a Liquid OS glass refraction background and specular rim highlight
 */
@Composable
fun Modifier.liquidGlassBackground(
    shape: Shape = RoundedCornerShape(20.dp),
    isDark: Boolean = isSystemInDarkTheme(),
    elevation: Dp = 4.dp
): Modifier {
    val bgBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xCC131D31), // 80% obsidian glass
                Color(0x990D1422)  // 60% obsidian glass
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xF0FFFFFF), // 94% white frosted glass
                Color(0xD9F1F5F9)  // 85% slate white glass
            )
        )
    }

    val rimBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color(0x4D38BDF8), // Electric cyan specular rim highlight on top-left
                Color(0x1A0070F3),
                Color(0x0DFFFFFF)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0x80FFFFFF), // High white rim on top-left
                Color(0x40CBD5E1),
                Color(0x1AE2E8F0)
            )
        )
    }

    return this
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = if (isDark) Color(0x66000000) else Color(0x1A000000),
            spotColor = if (isDark) Color(0x3300C7D7) else Color(0x260070F3)
        )
        .clip(shape)
        .background(bgBrush)
        .border(
            width = 1.dp,
            brush = rimBrush,
            shape = shape
        )
}

/**
 * Spring-based tactile liquid press interaction
 */
@Composable
fun Modifier.liquidPress(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.94f
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "liquid_scale"
    )
    return this.scale(scale)
}

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    onClick: (() -> Unit)? = null,
    elevation: Dp = 4.dp,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val baseModifier = if (onClick != null) {
        modifier
            .liquidPress(interactionSource)
            .liquidGlassBackground(shape = shape, elevation = elevation)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    } else {
        modifier.liquidGlassBackground(shape = shape, elevation = elevation)
    }

    Box(modifier = baseModifier) {
        content()
    }
}
