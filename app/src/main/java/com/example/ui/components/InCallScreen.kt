package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ActiveCallSession
import com.example.ui.CallStatus
import com.example.ui.theme.MotoCyan
import com.example.ui.theme.MotoGreen
import com.example.ui.theme.MotoGreenGlow
import com.example.ui.theme.MotoRed
import com.example.ui.theme.MotoRedGlow

@Composable
fun InCallScreen(
    session: ActiveCallSession,
    onEndCall: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onToggleHold: () -> Unit,
    onToggleKeypad: () -> Unit,
    onDtmfDigit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    BackHandler {
        onEndCall()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (session.status == CallStatus.DIALING) 1.2f else 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val mins = session.durationSeconds / 60
    val secs = session.durationSeconds % 60
    val durationText = String.format("%02d:%02d", mins, secs)

    val statusText = when (session.status) {
        CallStatus.DIALING -> "Dialing…"
        CallStatus.CONNECTED -> if (session.isOnHold) "On Hold • $durationText" else "Connected • $durationText"
        CallStatus.ENDED -> "Call Ended"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF16253D),
                        Color(0xFF0A101C),
                        Color(0xFF030509)
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Motorola / iPhone style top indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    when (session.status) {
                                        CallStatus.DIALING -> MotoCyan
                                        CallStatus.CONNECTED -> MotoGreen
                                        CallStatus.ENDED -> MotoRed
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "HD Voice Audio",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    text = session.name,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = session.phoneNumber,
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = statusText,
                    color = if (session.status == CallStatus.CONNECTED) MotoGreen else MotoCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Center Area: Pulsing Avatar or In-Call DTMF Pad
            if (session.showInCallKeypad) {
                // DTMF In-Call Keypad
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    val dtmfButtons = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("*", "0", "#")
                    )
                    dtmfButtons.forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            row.forEach { char ->
                                val dtmfInteraction = remember { MutableInteractionSource() }
                                Box(
                                    modifier = Modifier
                                        .liquidPress(dtmfInteraction)
                                        .padding(horizontal = 10.dp)
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.15f))
                                        .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                                        .clickable(
                                            interactionSource = dtmfInteraction,
                                            indication = null,
                                            onClick = { onDtmfDigit(char) }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = char,
                                        color = Color.White,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Circular Pulsing Handset / Contact Avatar with Liquid depth
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(190.dp)
                ) {
                    // Outer animated pulse ring
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .scale(pulseScale)
                            .border(
                                width = 2.dp,
                                color = if (session.status == CallStatus.CONNECTED)
                                    MotoGreenGlow.copy(alpha = 0.35f)
                                else
                                    MotoCyan.copy(alpha = 0.35f),
                                shape = CircleShape
                            )
                    )

                    ContactAvatar(
                        name = session.name,
                        initials = session.name.take(2).uppercase(),
                        avatarColorIndex = session.avatarColorIndex,
                        size = 118.dp
                    )
                }
            }

            // Bottom In-Call Liquid Controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LiquidCallActionButton(
                        icon = if (session.isMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
                        label = if (session.isMuted) "Unmute" else "Mute",
                        isActive = session.isMuted,
                        onClick = onToggleMute,
                        testTag = "incall_mute"
                    )
                    LiquidCallActionButton(
                        icon = Icons.Filled.Dialpad,
                        label = if (session.showInCallKeypad) "Hide Pad" else "Keypad",
                        isActive = session.showInCallKeypad,
                        onClick = onToggleKeypad,
                        testTag = "incall_keypad"
                    )
                    LiquidCallActionButton(
                        icon = if (session.isSpeakerOn) Icons.Filled.VolumeUp else Icons.Filled.VolumeMute,
                        label = "Speaker",
                        isActive = session.isSpeakerOn,
                        onClick = onToggleSpeaker,
                        testTag = "incall_speaker"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LiquidCallActionButton(
                        icon = if (session.isOnHold) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                        label = if (session.isOnHold) "Resume" else "Hold",
                        isActive = session.isOnHold,
                        onClick = onToggleHold,
                        testTag = "incall_hold"
                    )
                    LiquidCallActionButton(
                        icon = Icons.Filled.Phone,
                        label = "Add Call",
                        isActive = false,
                        onClick = onToggleKeypad,
                        testTag = "incall_add_call"
                    )
                    LiquidCallActionButton(
                        icon = Icons.Filled.Phone,
                        label = "Audio",
                        isActive = session.isSpeakerOn,
                        onClick = onToggleSpeaker,
                        testTag = "incall_audio"
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Big Floating Red End Call Button with Liquid specular rim and shadow
                val endCallInteraction = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .liquidPress(endCallInteraction, pressedScale = 0.92f)
                        .size(76.dp)
                        .shadow(16.dp, CircleShape, spotColor = MotoRedGlow)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(MotoRed, Color(0xFFB91C1C))
                            )
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                        .clickable(
                            interactionSource = endCallInteraction,
                            indication = null,
                            onClick = onEndCall
                        )
                        .testTag("end_call_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun LiquidCallActionButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .liquidPress(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) Color.White else Color.White.copy(alpha = 0.15f)
                )
                .border(
                    1.dp,
                    if (isActive) Color.White else Color.White.copy(alpha = 0.25f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.Black else Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
