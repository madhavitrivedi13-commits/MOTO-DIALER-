package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Contact
import com.example.ui.components.ContactAvatar
import com.example.ui.components.liquidPress
import com.example.ui.theme.MotoGreen
import com.example.ui.theme.MotoGreenDark
import com.example.ui.theme.MotoGreenGlow

data class KeypadKey(
    val digit: String,
    val letters: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeypadScreen(
    input: String,
    t9Suggestions: List<Contact>,
    onDigitClick: (String) -> Unit,
    onZeroLongPress: () -> Unit,
    onBackspaceClick: () -> Unit,
    onBackspaceLongPress: () -> Unit,
    onCallClick: (String) -> Unit,
    onSelectContact: (Contact) -> Unit,
    onAddNewContact: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val keypadGrid = listOf(
        listOf(KeypadKey("1", " "), KeypadKey("2", "ABC"), KeypadKey("3", "DEF")),
        listOf(KeypadKey("4", "GHI"), KeypadKey("5", "JKL"), KeypadKey("6", "MNO")),
        listOf(KeypadKey("7", "PQRS"), KeypadKey("8", "TUV"), KeypadKey("9", "WXYZ")),
        listOf(KeypadKey("*", ""), KeypadKey("0", "+"), KeypadKey("#", ""))
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Area: Dialed Number Display + T9 Pill Bar
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            // T9 Floating Glass Capsules
            AnimatedVisibility(
                visible = t9Suggestions.isNotEmpty() && input.isNotEmpty(),
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(t9Suggestions, key = { it.id }) { contact ->
                        Surface(
                            onClick = { onSelectContact(contact) },
                            color = if (isDark) Color(0x991E293B) else Color(0xE6FFFFFF),
                            shape = RoundedCornerShape(22.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isDark) Color(0x3338BDF8) else Color(0x40CBD5E1)
                            ),
                            shadowElevation = 4.dp,
                            modifier = Modifier.testTag("t9_suggestion_${contact.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                ContactAvatar(
                                    name = contact.name,
                                    initials = contact.initials,
                                    avatarColorIndex = contact.avatarColorIndex,
                                    size = 32.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = contact.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = contact.phoneNumber,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Input Display Text (Dynamic Clean Typography)
            val fontSize = when {
                input.length > 16 -> 24.sp
                input.length > 12 -> 28.sp
                input.length > 8 -> 34.sp
                else -> 42.sp
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                contentAlignment = Alignment.Center
            ) {
                if (input.isEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MotoGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MOTO DIALER • CELLULAR READY",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.5.sp
                        )
                    }
                } else {
                    Text(
                        text = input,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = fontSize,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        letterSpacing = 1.sp,
                        modifier = Modifier.testTag("keypad_display_text")
                    )
                }
            }

            // Floating Action Pills (Add to Contacts + WhatsApp)
            AnimatedVisibility(visible = input.isNotBlank()) {
                val context = androidx.compose.ui.platform.LocalContext.current
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Surface(
                        onClick = { onAddNewContact(input) },
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.testTag("add_number_to_contacts")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PersonAdd,
                                contentDescription = "Add to Contacts",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Save",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // WhatsApp Instant Chat Pill
                    Surface(
                        onClick = {
                            com.example.util.WhatsAppHelper.openWhatsAppChat(context, input)
                        },
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF25D366).copy(alpha = 0.14f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0xFF25D366).copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.testTag("keypad_whatsapp_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Message,
                                contentDescription = "WhatsApp",
                                tint = Color(0xFF25D366),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "WhatsApp",
                                color = Color(0xFF25D366),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Liquid Floating Keypad Grid
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            keypadGrid.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(22.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEach { key ->
                        LiquidKeypadButton(
                            digit = key.digit,
                            letters = key.letters,
                            onClick = { onDigitClick(key.digit) },
                            onLongClick = if (key.digit == "0") onZeroLongPress else null
                        )
                    }
                }
            }

            // Bottom action row: Empty spacer, Glowing Liquid Call Button, Liquid Backspace Button
            Row(
                horizontalArrangement = Arrangement.spacedBy(22.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 6.dp)
            ) {
                // Left spacer
                Box(modifier = Modifier.size(74.dp))

                // Floating Radiant Liquid Call Button
                val callInteraction = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .liquidPress(callInteraction, pressedScale = 0.92f)
                        .size(76.dp)
                        .shadow(16.dp, CircleShape, spotColor = MotoGreenGlow)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(MotoGreenGlow, MotoGreen, MotoGreenDark)
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            color = Color.White.copy(alpha = 0.45f),
                            shape = CircleShape
                        )
                        .combinedClickable(
                            interactionSource = callInteraction,
                            indication = null,
                            onClick = {
                                if (input.isNotBlank()) {
                                    onCallClick(input)
                                }
                            }
                        )
                        .testTag("keypad_call_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Phone,
                        contentDescription = "Place Call",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Liquid Backspace button
                Box(
                    modifier = Modifier.size(74.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (input.isNotEmpty()) {
                        val backspaceInteraction = remember { MutableInteractionSource() }
                        Box(
                            modifier = Modifier
                                .liquidPress(backspaceInteraction, pressedScale = 0.9f)
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isDark) Color(0x331E293B) else Color(0x66E2E8F0)
                                )
                                .border(
                                    1.dp,
                                    if (isDark) Color(0x2638BDF8) else Color(0x40CBD5E1),
                                    CircleShape
                                )
                                .combinedClickable(
                                    interactionSource = backspaceInteraction,
                                    indication = null,
                                    onClick = onBackspaceClick,
                                    onLongClick = onBackspaceLongPress
                                )
                                .testTag("keypad_backspace"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Backspace,
                                contentDescription = "Delete digit",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LiquidKeypadButton(
    digit: String,
    letters: String,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    val isDark = isSystemInDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }

    val bgBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x59223048), // Translucent obsidian glass
                Color(0x33152033)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xF2FFFFFF), // Translucent frosted white glass
                Color(0xD9E9EEF5)
            )
        )
    }

    val rimBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color(0x4D38BDF8),
                Color(0x1A0070F3),
                Color(0x08FFFFFF)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xB3FFFFFF),
                Color(0x40CBD5E1)
            )
        )
    }

    Box(
        modifier = Modifier
            .liquidPress(interactionSource, pressedScale = 0.93f)
            .size(74.dp)
            .shadow(
                elevation = 4.dp,
                shape = CircleShape,
                spotColor = if (isDark) Color(0x4000C7D7) else Color(0x260070F3)
            )
            .clip(CircleShape)
            .background(bgBrush)
            .border(width = 1.dp, brush = rimBrush, shape = CircleShape)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick
            )
            .testTag("keypad_key_$digit"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = digit,
                fontSize = if (digit == "*" || digit == "#") 34.sp else 29.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 29.sp
            )
            if (letters.isNotBlank()) {
                Text(
                    text = letters,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}
