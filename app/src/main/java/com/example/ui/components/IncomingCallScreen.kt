package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MotoCyan
import com.example.ui.theme.MotoGreen
import com.example.ui.theme.MotoGreenGlow
import com.example.ui.theme.MotoRed
import com.example.ui.theme.MotoRedGlow
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

data class IncomingCallSession(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val avatarColorIndex: Int = 0
)

@Composable
fun IncomingCallScreen(
    session: IncomingCallSession,
    onAnswer: () -> Unit,
    onDecline: () -> Unit,
    onQuickMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onDecline()
    }

    // Liquid organic breathing and pulsating animations
    val infiniteTransition = rememberInfiniteTransition(label = "liquid_incoming_anim")

    // Ripple 1
    val waveScale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )
    val waveAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveAlpha1"
    )

    // Ripple 2
    val waveScale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, delayMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2"
    )
    val waveAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, delayMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveAlpha2"
    )

    // Ambient background liquid orb shift
    val ambientPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambientPulse"
    )

    // Shimmering chevrons on slide track
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
            .testTag("incoming_call_overlay")
    ) {
        // --- 1. Liquid Ambient Mesh Background with Gaussian Blur ---
        Box(modifier = Modifier.fillMaxSize()) {
            // Ambient Top Cyan Orb
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .offset(x = (-40).dp, y = (-20).dp)
                    .scale(ambientPulse)
                    .blur(64.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF0E7490).copy(alpha = 0.55f),
                                Color(0xFF083344).copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // Ambient Center Emerald Orb (Behind Avatar)
            Box(
                modifier = Modifier
                    .size(360.dp)
                    .align(Alignment.Center)
                    .scale(1.2f - (ambientPulse - 0.85f) * 0.5f)
                    .blur(72.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF059669).copy(alpha = 0.4f),
                                Color(0xFF042F2E).copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // Ambient Bottom Deep Indigo / Azure Orb
            Box(
                modifier = Modifier
                    .size(340.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 60.dp, y = 80.dp)
                    .blur(64.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1E3A8A).copy(alpha = 0.5f),
                                Color(0xFF0F172A).copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // Liquid Frosted Vignette Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x66000000),
                                Color(0x22000000),
                                Color(0xAA000000)
                            )
                        )
                    )
            )
        }

        // --- 2. Foreground Content Layout with Edge-to-Edge Insets ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- TOP HEADER: Floating Frosted Status Capsule & Caller Details ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                // Floating Liquid Pill Badge
                Surface(
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Brush.horizontalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.45f),
                                Color.White.copy(alpha = 0.15f)
                            )
                        )
                    ),
                    modifier = Modifier.shadow(8.dp, RoundedCornerShape(20.dp), spotColor = MotoCyan)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MotoGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "INCOMING CALL • HD VOICE",
                            color = Color.White.copy(alpha = 0.95f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Caller Name
                Text(
                    text = session.name,
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp,
                    lineHeight = 40.sp,
                    modifier = Modifier.testTag("incoming_caller_name")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Caller Phone Number
                Text(
                    text = session.phoneNumber,
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 1.sp,
                    modifier = Modifier.testTag("incoming_caller_phone")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Carrier / Location Tag
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.PhoneInTalk,
                        contentDescription = null,
                        tint = MotoCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Cellular • Mobile Call",
                        color = MotoCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // --- CENTER: Concentric Liquid Ripples & Glass Contact Avatar ---
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(240.dp)
                    .padding(vertical = 10.dp)
            ) {
                // Liquid Wave 2 (Outer Ripple)
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(waveScale2)
                        .border(
                            width = 2.dp,
                            brush = Brush.radialGradient(
                                colors = listOf(MotoCyan.copy(alpha = waveAlpha2), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )

                // Liquid Wave 1 (Inner Ripple)
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(waveScale1)
                        .border(
                            width = 2.5.dp,
                            brush = Brush.radialGradient(
                                colors = listOf(MotoGreenGlow.copy(alpha = waveAlpha1), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )

                // Glassmorphic Outer Halo Ring
                Box(
                    modifier = Modifier
                        .size(152.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.06f))
                        .border(
                            width = 1.5.dp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.4f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Main Avatar Container with Elevation & Specular Reflection
                Box(
                    modifier = Modifier
                        .size(136.dp)
                        .shadow(24.dp, CircleShape, spotColor = MotoCyan)
                        .clip(CircleShape)
                        .border(2.5.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    ContactAvatar(
                        name = session.name,
                        initials = session.name.take(2).uppercase(),
                        avatarColorIndex = session.avatarColorIndex,
                        size = 136.dp
                    )
                }
            }

            // --- BOTTOM CONTROLS: Quick Actions + "IN ONE SLIDE" UNIFIED SLIDER ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                // Secondary Quick Actions (Remind Me & Quick Message)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuickCallAction(
                        icon = Icons.Filled.Alarm,
                        label = "Remind Me",
                        onClick = { onDecline() }
                    )

                    QuickCallAction(
                        icon = Icons.Filled.Message,
                        label = "Message",
                        onClick = { onQuickMessage("I'll call you back soon.") }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- IN ONE SLIDE: UNIFIED BIDIRECTIONAL SLIDER ---
                // Slide Left to Decline (Red) | Slide Right to Answer (Green)
                LiquidUnifiedSlider(
                    shimmerOffset = shimmerOffset,
                    onAnswerConfirmed = onAnswer,
                    onDeclineConfirmed = onDecline
                )
            }
        }
    }
}

/**
 * "In One Slide" - Unified Bidirectional Liquid Slider following Motorola Liquid OS.
 * A single ergonomic frosted glass pill track with:
 * - Slide Left ❮❮❮ Decline (Crimson Coral Glow & call rejection)
 * - Slide Right ❯❯❯ Answer (Emerald Green Glow & call connection)
 * - Smooth spring-physics handle that resets to center if released early.
 * - Fluid progressive liquid trails expanding in the direction of the drag.
 */
@Composable
private fun LiquidUnifiedSlider(
    shimmerOffset: Float,
    onAnswerConfirmed: () -> Unit,
    onDeclineConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val dragOffset = remember { Animatable(0f) }
    var maxHalfDragPx by remember { mutableFloatStateOf(0f) }

    val trackHeight = 74.dp
    val thumbSize = 62.dp
    val padding = 6.dp

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(trackHeight)
            .clip(RoundedCornerShape(37.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.10f)
                    )
                ),
                shape = RoundedCornerShape(37.dp)
            )
            .shadow(16.dp, RoundedCornerShape(37.dp), spotColor = Color(0x33000000))
            .testTag("unified_slide_track"),
        contentAlignment = Alignment.Center
    ) {
        val totalWidthPx = with(density) { maxWidth.toPx() }
        val thumbSizePx = with(density) { thumbSize.toPx() }
        val paddingPx = with(density) { padding.toPx() }

        // Maximum drag distance from center to left or right end
        maxHalfDragPx = ((totalWidthPx - thumbSizePx - (paddingPx * 2)) / 2f).coerceAtLeast(0f)

        val currentOffset = dragOffset.value
        val isSlidingRight = currentOffset > 0f
        val isSlidingLeft = currentOffset < 0f
        val rightProgress = if (maxHalfDragPx > 0f) (currentOffset / maxHalfDragPx).coerceIn(0f, 1f) else 0f
        val leftProgress = if (maxHalfDragPx > 0f) ((-currentOffset) / maxHalfDragPx).coerceIn(0f, 1f) else 0f

        // 1. Dynamic Liquid Glowing Trail from center
        if (isSlidingRight && rightProgress > 0.02f) {
            val trailWidthDp = with(density) { (currentOffset + (thumbSizePx / 2f)).toDp() }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = with(density) { ((totalWidthPx / 2f) - (thumbSizePx / 4f)).toDp() })
                    .width(trailWidthDp)
                    .height(trackHeight)
                    .clip(RoundedCornerShape(37.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MotoGreen.copy(alpha = 0.15f),
                                MotoGreenGlow.copy(alpha = 0.55f * rightProgress)
                            )
                        )
                    )
            )
        } else if (isSlidingLeft && leftProgress > 0.02f) {
            val trailWidthDp = with(density) { ((-currentOffset) + (thumbSizePx / 2f)).toDp() }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = with(density) { -((totalWidthPx / 2f) - (thumbSizePx / 4f)).toDp() })
                    .width(trailWidthDp)
                    .height(trackHeight)
                    .clip(RoundedCornerShape(37.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MotoRedGlow.copy(alpha = 0.55f * leftProgress),
                                MotoRed.copy(alpha = 0.15f)
                            )
                        )
                    )
            )
        }

        // 2. Dual Directional Track Guides: Left = Decline, Right = Answer
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // LEFT ZONE: DECLINE
            val declineAlpha = (1f - (rightProgress * 1.8f)).coerceIn(0f, 1f)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onDeclineConfirmed() }
                    .padding(4.dp)
                    .testTag("slide_decline_zone")
            ) {
                // Chevrons pointing Left
                repeat(3) { index ->
                    val chevronAlpha = ((shimmerOffset + ((2 - index) * 0.33f)) % 1f) * declineAlpha
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = MotoRed.copy(alpha = chevronAlpha.coerceIn(0.2f, 1f)),
                        modifier = Modifier
                            .size(16.dp)
                            .offset(x = (index * 3).dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Decline",
                    color = MotoRedGlow.copy(alpha = (0.85f + (leftProgress * 0.15f)) * declineAlpha),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }

            // RIGHT ZONE: ANSWER
            val answerAlpha = (1f - (leftProgress * 1.8f)).coerceIn(0f, 1f)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onAnswerConfirmed() }
                    .padding(4.dp)
                    .testTag("slide_answer_zone")
            ) {
                Text(
                    text = "Answer",
                    color = MotoGreenGlow.copy(alpha = (0.85f + (rightProgress * 0.15f)) * answerAlpha),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                // Chevrons pointing Right
                repeat(3) { index ->
                    val chevronAlpha = ((shimmerOffset + (index * 0.33f)) % 1f) * answerAlpha
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MotoGreen.copy(alpha = chevronAlpha.coerceIn(0.2f, 1f)),
                        modifier = Modifier
                            .size(16.dp)
                            .offset(x = (-index * 3).dp)
                    )
                }
            }
        }

        // 3. Center Draggable Liquid Handle with Spring Physics
        // Transitions from Neutral Cyan/White -> Emerald Green (Right) or Coral Red (Left)
        val handleGlowColor = when {
            isSlidingRight -> MotoGreenGlow
            isSlidingLeft -> MotoRedGlow
            else -> MotoCyan
        }

        val handleGradient = when {
            isSlidingRight -> listOf(
                Color(0xFF6EE7B7),
                MotoGreenGlow,
                MotoGreen,
                Color(0xFF047857)
            )
            isSlidingLeft -> listOf(
                Color(0xFFFCA5A5),
                MotoRedGlow,
                MotoRed,
                Color(0xFF991B1B)
            )
            else -> listOf(
                Color(0xFF38BDF8),
                MotoCyan,
                Color(0xFF0284C7),
                Color(0xFF0369A1)
            )
        }

        val handleIcon = when {
            isSlidingLeft -> Icons.Filled.CallEnd
            else -> Icons.Filled.Call
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(x = dragOffset.value.roundToInt(), y = 0) }
                .size(thumbSize)
                .shadow(16.dp, CircleShape, spotColor = handleGlowColor)
                .clip(CircleShape)
                .background(Brush.radialGradient(colors = handleGradient))
                .border(2.dp, Color.White.copy(alpha = 0.7f), CircleShape)
                .pointerInput(maxHalfDragPx) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            coroutineScope.launch {
                                val threshold = maxHalfDragPx * 0.65f
                                when {
                                    // Dragged past right threshold -> ANSWER
                                    dragOffset.value >= threshold -> {
                                        dragOffset.animateTo(
                                            targetValue = maxHalfDragPx,
                                            animationSpec = tween(120, easing = FastOutSlowInEasing)
                                        )
                                        onAnswerConfirmed()
                                    }
                                    // Dragged past left threshold -> DECLINE
                                    dragOffset.value <= -threshold -> {
                                        dragOffset.animateTo(
                                            targetValue = -maxHalfDragPx,
                                            animationSpec = tween(120, easing = FastOutSlowInEasing)
                                        )
                                        onDeclineConfirmed()
                                    }
                                    // Released near center -> SPRING BACK TO ZERO
                                    else -> {
                                        dragOffset.animateTo(
                                            targetValue = 0f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        )
                                    }
                                }
                            }
                        },
                        onDragCancel = {
                            coroutineScope.launch {
                                dragOffset.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            coroutineScope.launch {
                                val newOffset = (dragOffset.value + dragAmount).coerceIn(-maxHalfDragPx, maxHalfDragPx)
                                dragOffset.snapTo(newOffset)
                            }
                        }
                    )
                }
                .testTag("unified_slide_thumb"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = handleIcon,
                contentDescription = "Slide to Answer or Decline",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun QuickCallAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .liquidPress(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag("quick_action_${label.lowercase().replace(" ", "_")}")
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(10.dp, CircleShape, spotColor = MotoCyan)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.16f),
                            Color.White.copy(alpha = 0.08f)
                        )
                    )
                )
                .border(
                    width = 1.2.dp,
                    color = Color.White.copy(alpha = 0.25f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
