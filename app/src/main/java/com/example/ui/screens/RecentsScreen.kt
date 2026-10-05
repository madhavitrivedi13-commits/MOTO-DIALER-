package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneMissed
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CallLogEntry
import com.example.model.CallType
import com.example.ui.RecentsFilter
import com.example.ui.components.BlockNumberBottomSheet
import com.example.ui.components.ContactAvatar
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.liquidGlassBackground
import com.example.ui.components.liquidPress
import com.example.ui.theme.MotoBlue
import com.example.ui.theme.MotoCyan
import com.example.ui.theme.MotoGreen
import com.example.ui.theme.MotoRed
import com.example.ui.theme.MotoRedGlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecentsScreen(
    callLogs: List<CallLogEntry>,
    filter: RecentsFilter,
    onFilterChange: (RecentsFilter) -> Unit,
    onCallClick: (String, String) -> Unit,
    onLogDetailClick: (CallLogEntry) -> Unit,
    onClearAllLogs: () -> Unit,
    onBlockNumber: (String, String) -> Unit = { _, _ -> },
    onUnblockNumber: (String) -> Unit = {},
    isNumberBlocked: (String) -> Boolean = { false },
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var entryForBlockSheet by remember { mutableStateOf<CallLogEntry?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Recents",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Tap & hold any entry to block/unblock",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (callLogs.isNotEmpty()) {
                IconButton(
                    onClick = { showClearConfirmDialog = true },
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteSweep,
                        contentDescription = "Clear Call History",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Liquid Segmented Tabs Capsule (All Calls | Missed | Blocked)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlassBackground(RoundedCornerShape(18.dp), isDark = isDark, elevation = 2.dp)
                .padding(4.dp)
        ) {
            TabRow(
                selectedTabIndex = when (filter) {
                    RecentsFilter.ALL -> 0
                    RecentsFilter.MISSED -> 1
                    RecentsFilter.BLOCKED -> 2
                },
                containerColor = Color.Transparent,
                indicator = {},
                divider = {}
            ) {
                // Tab 1: All Calls
                val allSelected = filter == RecentsFilter.ALL
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (allSelected) {
                                if (isDark) Color(0x6638BDF8) else Color.White
                            } else Color.Transparent
                        )
                        .combinedClickable(onClick = { onFilterChange(RecentsFilter.ALL) })
                        .padding(vertical = 8.dp)
                        .testTag("recents_tab_all"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "All Calls",
                        fontSize = 12.sp,
                        fontWeight = if (allSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (allSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Tab 2: Missed
                val missedSelected = filter == RecentsFilter.MISSED
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (missedSelected) {
                                if (isDark) Color(0x66EF4444) else Color.White
                            } else Color.Transparent
                        )
                        .combinedClickable(onClick = { onFilterChange(RecentsFilter.MISSED) })
                        .padding(vertical = 8.dp)
                        .testTag("recents_tab_missed"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Missed",
                        fontSize = 12.sp,
                        fontWeight = if (missedSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (missedSelected) MotoRed else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Tab 3: Blocked
                val blockedSelected = filter == RecentsFilter.BLOCKED
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (blockedSelected) {
                                if (isDark) Color(0x66F43F5E) else Color.White
                            } else Color.Transparent
                        )
                        .combinedClickable(onClick = { onFilterChange(RecentsFilter.BLOCKED) })
                        .padding(vertical = 8.dp)
                        .testTag("recents_tab_blocked"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = null,
                            tint = if (blockedSelected) MotoRed else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Blocked",
                            fontSize = 12.sp,
                            fontWeight = if (blockedSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (blockedSelected) MotoRed else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (callLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .liquidGlassBackground(CircleShape, isDark = isDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (filter) {
                                RecentsFilter.BLOCKED -> Icons.Filled.Shield
                                RecentsFilter.MISSED -> Icons.Filled.PhoneMissed
                                else -> Icons.Filled.History
                            },
                            contentDescription = null,
                            tint = if (filter == RecentsFilter.BLOCKED) MotoRed else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = when (filter) {
                            RecentsFilter.BLOCKED -> "No Blocked Calls"
                            RecentsFilter.MISSED -> "No Missed Calls"
                            else -> "No Recent Calls"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = when (filter) {
                            RecentsFilter.BLOCKED -> "Numbers you block will appear here and cannot call you."
                            RecentsFilter.MISSED -> "Missed calls will appear here."
                            else -> "Calls you make or receive will appear here."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(callLogs, key = { it.id }) { log ->
                    val isBlocked = isNumberBlocked(log.phoneNumber) || log.isBlocked || log.type == CallType.BLOCKED
                    LiquidCallLogItem(
                        entry = log,
                        isBlocked = isBlocked,
                        onCall = { onCallClick(log.phoneNumber, log.name) },
                        onDetail = { onLogDetailClick(log) },
                        onLongClick = { entryForBlockSheet = log }
                    )
                }
            }
        }
    }

    // Block Number Bottom Sheet (triggered by tap-and-hold)
    entryForBlockSheet?.let { targetEntry ->
        val isBlocked = isNumberBlocked(targetEntry.phoneNumber) || targetEntry.isBlocked || targetEntry.type == CallType.BLOCKED
        BlockNumberBottomSheet(
            entry = targetEntry,
            isBlocked = isBlocked,
            onDismiss = { entryForBlockSheet = null },
            onBlockNumber = { phone, name ->
                onBlockNumber(phone, name)
            },
            onUnblockNumber = { phone ->
                onUnblockNumber(phone)
            },
            onCall = { phone, name ->
                onCallClick(phone, name)
            },
            onDeleteLog = { id ->
                // Handled via detail
            }
        )
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Clear Call History?") },
            text = { Text("All call logs will be removed. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAllLogs()
                        showClearConfirmDialog = false
                    },
                    modifier = Modifier.testTag("confirm_clear_history")
                ) {
                    Text("Clear All", color = MotoRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LiquidCallLogItem(
    entry: CallLogEntry,
    isBlocked: Boolean,
    onCall: () -> Unit,
    onDetail: () -> Unit,
    onLongClick: () -> Unit
) {
    val isMissed = entry.type == CallType.MISSED
    val colorIndex = kotlin.math.abs(entry.name.hashCode()) % 8

    LiquidGlassCard(
        shape = RoundedCornerShape(20.dp),
        onClick = onDetail,
        elevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(entry.id) {
                detectTapGestures(
                    onTap = { onDetail() },
                    onLongPress = { onLongClick() }
                )
            }
            .testTag("call_log_item_${entry.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contact Avatar with soft liquid depth & blocked badge
            Box(contentAlignment = Alignment.BottomEnd) {
                ContactAvatar(
                    name = entry.name,
                    initials = entry.name.take(2).uppercase(),
                    avatarColorIndex = colorIndex,
                    size = 48.dp
                )

                if (isBlocked) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(MotoRed)
                            .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Block,
                            contentDescription = "Blocked",
                            tint = Color.White,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Main Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = when {
                            isBlocked -> MotoRed
                            isMissed -> MotoRed
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (isBlocked) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MotoRed.copy(alpha = 0.15f))
                                .border(0.8.dp, MotoRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "BLOCKED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MotoRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val (icon, iconTint, typeLabel) = when {
                        isBlocked || entry.type == CallType.BLOCKED -> Triple(Icons.Filled.Block, MotoRed, "Blocked")
                        entry.type == CallType.INCOMING -> Triple(Icons.AutoMirrored.Filled.CallReceived, MotoGreen, "Incoming")
                        entry.type == CallType.OUTGOING -> Triple(Icons.AutoMirrored.Filled.CallMade, MotoBlue, "Outgoing")
                        else -> Triple(Icons.Filled.PhoneMissed, MotoRed, "Missed")
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = typeLabel,
                        tint = iconTint,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$typeLabel • ${formatTimestamp(entry.timestamp)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quick Call Back Liquid Button
            val quickCallInteraction = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .liquidPress(quickCallInteraction)
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isBlocked) MotoRed.copy(alpha = 0.12f) else MotoGreen.copy(alpha = 0.15f))
                    .combinedClickable(
                        onClick = onCall,
                        onLongClick = onLongClick
                    )
                    .testTag("quick_call_${entry.id}"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isBlocked) Icons.Filled.Shield else Icons.Filled.Phone,
                    contentDescription = if (isBlocked) "Blocked Options" else "Call ${entry.name}",
                    tint = if (isBlocked) MotoRed else MotoGreen,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val oneMinute = 60 * 1000L
    val oneHour = 60 * oneMinute
    val oneDay = 24 * oneHour

    return when {
        diff < 5 * oneMinute -> "Just now"
        diff < oneHour -> "${diff / oneMinute}m ago"
        diff < oneDay -> SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
        diff < 2 * oneDay -> "Yesterday"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
    }
}
