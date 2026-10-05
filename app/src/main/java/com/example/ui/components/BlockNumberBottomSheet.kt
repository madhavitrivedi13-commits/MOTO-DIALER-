package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.model.CallLogEntry
import com.example.ui.theme.MotoBlue
import com.example.ui.theme.MotoCyan
import com.example.ui.theme.MotoGreen
import com.example.ui.theme.MotoRed
import com.example.ui.theme.MotoRedGlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockNumberBottomSheet(
    entry: CallLogEntry,
    isBlocked: Boolean,
    onDismiss: () -> Unit,
    onBlockNumber: (String, String) -> Unit,
    onUnblockNumber: (String) -> Unit,
    onCall: (String, String) -> Unit,
    onDeleteLog: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val colorIndex = kotlin.math.abs(entry.name.hashCode()) % 8

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.testTag("block_number_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isBlocked) Icons.Filled.Shield else Icons.Filled.Block,
                        contentDescription = null,
                        tint = if (isBlocked) MotoGreen else MotoRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBlocked) "Number Blocked" else "Call Log Options",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Avatar & Name Card
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .border(
                        2.dp,
                        if (isBlocked) MotoRed.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                ContactAvatar(
                    name = entry.name,
                    initials = entry.name.take(2).uppercase(),
                    avatarColorIndex = colorIndex,
                    size = 76.dp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = entry.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = entry.phoneNumber,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Block / Unblock Primary Liquid Action Button
            if (isBlocked) {
                // Unblock Pill Button
                Surface(
                    onClick = {
                        onUnblockNumber(entry.phoneNumber)
                        Toast.makeText(context, "Unblocked ${entry.phoneNumber}", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(18.dp),
                    color = MotoGreen.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MotoGreen.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(18.dp), spotColor = MotoGreen)
                        .testTag("unblock_number_button")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 14.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = MotoGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Unblock This Number",
                                fontWeight = FontWeight.Bold,
                                color = MotoGreen,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Allow incoming calls from this contact again",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Block Number Warning Pill Button
                Surface(
                    onClick = {
                        onBlockNumber(entry.phoneNumber, entry.name)
                        Toast.makeText(context, "Blocked ${entry.name.ifBlank { entry.phoneNumber }}", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(18.dp),
                    color = MotoRed.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, MotoRed.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(18.dp), spotColor = MotoRedGlow)
                        .testTag("block_number_button")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 14.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Block,
                            contentDescription = null,
                            tint = MotoRed,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Block Number",
                                fontWeight = FontWeight.Bold,
                                color = MotoRed,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Prevent future incoming calls from this caller",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Secondary Quick Actions Grid (Call, SMS, Copy, Delete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuickSheetAction(
                    icon = Icons.Filled.Phone,
                    label = "Call",
                    tint = MotoGreen,
                    onClick = {
                        onDismiss()
                        onCall(entry.phoneNumber, entry.name)
                    }
                )

                QuickSheetAction(
                    icon = Icons.Filled.Message,
                    label = "Message",
                    tint = MotoBlue,
                    onClick = {
                        onDismiss()
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${entry.phoneNumber}"))
                        context.startActivity(intent)
                    }
                )

                QuickSheetAction(
                    icon = Icons.Filled.Message,
                    label = "WhatsApp",
                    tint = Color(0xFF25D366),
                    onClick = {
                        onDismiss()
                        com.example.util.WhatsAppHelper.openWhatsAppChat(context, entry.phoneNumber)
                    }
                )

                QuickSheetAction(
                    icon = Icons.Filled.ContentCopy,
                    label = "Copy",
                    tint = MotoCyan,
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Phone Number", entry.phoneNumber))
                        Toast.makeText(context, "Copied ${entry.phoneNumber}", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )

                QuickSheetAction(
                    icon = Icons.Filled.Delete,
                    label = "Delete",
                    tint = MotoRed,
                    onClick = {
                        onDismiss()
                        onDeleteLog(entry.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun QuickSheetAction(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f))
                .border(1.dp, tint.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
