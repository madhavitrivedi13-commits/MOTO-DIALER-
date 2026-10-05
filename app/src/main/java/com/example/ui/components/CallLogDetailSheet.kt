package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneMissed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CallLogEntry
import com.example.model.CallType
import com.example.ui.theme.MotoBlue
import com.example.ui.theme.MotoGreen
import com.example.ui.theme.MotoRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallLogDetailSheet(
    entry: CallLogEntry,
    isBlocked: Boolean = false,
    onDismiss: () -> Unit,
    onCall: (String, String) -> Unit,
    onAddContact: (String) -> Unit,
    onDeleteLog: (String) -> Unit,
    onToggleBlock: (String, String, Boolean) -> Unit = { _, _, _ -> }
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val colorIndex = kotlin.math.abs(entry.name.hashCode()) % 8

    val fullDate = SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(Date(entry.timestamp))

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                }
            }

            ContactAvatar(
                name = entry.name,
                initials = entry.name.take(2).uppercase(),
                avatarColorIndex = colorIndex,
                size = 80.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = entry.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = entry.phoneNumber,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Call & Message row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Call
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MotoGreen)
                        .clickable {
                            onDismiss()
                            onCall(entry.phoneNumber, entry.name)
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .testTag("log_sheet_call")
                ) {
                    Icon(imageVector = Icons.Filled.Phone, contentDescription = "Call", tint = MaterialTheme.colorScheme.surface)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Call", color = MaterialTheme.colorScheme.surface, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Message
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MotoBlue.copy(alpha = 0.15f))
                        .clickable {
                            val smsIntent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("sms:${entry.phoneNumber}")
                            }
                            context.startActivity(smsIntent)
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Message, contentDescription = "Message", tint = MotoBlue)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "SMS", color = MotoBlue, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(10.dp))

                // WhatsApp
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF25D366).copy(alpha = 0.15f))
                        .clickable {
                            onDismiss()
                            com.example.util.WhatsAppHelper.openWhatsAppChat(context, entry.phoneNumber)
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("log_sheet_whatsapp")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Message,
                        contentDescription = "WhatsApp",
                        tint = Color(0xFF25D366)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "WhatsApp", color = Color(0xFF25D366), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Call Details Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val (icon, tint, label) = when {
                        entry.type == CallType.BLOCKED || isBlocked -> Triple(Icons.Filled.Close, MotoRed, "Blocked Call")
                        entry.type == CallType.INCOMING -> Triple(Icons.AutoMirrored.Filled.CallReceived, MotoGreen, "Incoming Call")
                        entry.type == CallType.OUTGOING -> Triple(Icons.AutoMirrored.Filled.CallMade, MotoBlue, "Outgoing Call")
                        else -> Triple(Icons.Filled.PhoneMissed, MotoRed, "Missed Call")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = fullDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Duration: ${entry.formattedDuration}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Block / Unblock Number Card Button
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isBlocked) MotoGreen.copy(alpha = 0.12f) else MotoRed.copy(alpha = 0.10f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isBlocked) MotoGreen.copy(alpha = 0.35f) else MotoRed.copy(alpha = 0.3f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onToggleBlock(entry.phoneNumber, entry.name, !isBlocked)
                    }
                    .testTag("detail_sheet_toggle_block")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isBlocked) Icons.Filled.Close else Icons.Filled.Close,
                        contentDescription = null,
                        tint = if (isBlocked) MotoGreen else MotoRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBlocked) "Unblock Number" else "Block Number",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isBlocked) MotoGreen else MotoRed
                        )
                        Text(
                            text = if (isBlocked) "Allow incoming calls from this caller" else "Prevent future incoming calls from this number",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action row: Add Contact (if not contact) & Delete Log
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (entry.contactId == null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onDismiss()
                                onAddContact(entry.phoneNumber)
                            }
                            .padding(8.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.PersonAdd, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Add to Contacts", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onDismiss()
                            onDeleteLog(entry.id)
                        }
                        .padding(8.dp)
                        .testTag("delete_log_button")
                ) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = null, tint = MotoRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Remove from Recents", color = MotoRed, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
