package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MotoCyan
import com.example.ui.theme.MotoGreen
import com.example.ui.theme.MotoGreenGlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstTimeSetupSheet(
    hasContactsPermission: Boolean,
    hasCallPermission: Boolean,
    isDefaultDialer: Boolean,
    onRequestPermissionsAndDefault: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        modifier = Modifier.testTag("first_time_setup_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ambient Logo & Header Badge
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .shadow(16.dp, CircleShape, spotColor = MotoCyan)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                MotoCyan,
                                MaterialTheme.colorScheme.primary,
                                Color(0xFF0369A1)
                            )
                        )
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Welcome to MOTO DIALER",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Enable permissions to view contacts, make cellular calls, and set as your default phone app.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Contacts Permission Card
            SetupPermissionItem(
                icon = Icons.Filled.Contacts,
                title = "Contacts Access",
                description = "View, search, and manage your device contacts directly in MOTO DIALER.",
                isGranted = hasContactsPermission,
                grantedLabel = "Allowed",
                unassignedLabel = "Required"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Make Phone Calls Permission Card
            SetupPermissionItem(
                icon = Icons.Filled.Call,
                title = "Make Cellular Calls",
                description = "Place calls instantly with carrier connectivity from the keypad and contacts.",
                isGranted = hasCallPermission,
                grantedLabel = "Allowed",
                unassignedLabel = "Required"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Default Phone App Card
            SetupPermissionItem(
                icon = Icons.Filled.VerifiedUser,
                title = "Default Phone App",
                description = "Handle all incoming and outgoing calls with premium liquid call screens.",
                isGranted = isDefaultDialer,
                grantedLabel = "Default App",
                unassignedLabel = "Recommended"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Action Button
            Button(
                onClick = onRequestPermissionsAndDefault,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = MotoGreenGlow)
                    .testTag("setup_allow_and_default_button")
            ) {
                Text(
                    text = if (hasContactsPermission && hasCallPermission && isDefaultDialer) {
                        "All Set! Continue"
                    } else {
                        "Allow Permissions & Set Default"
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Secondary Skip Button
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("setup_dismiss_button")
            ) {
                Text(
                    text = "Continue to App",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun SetupPermissionItem(
    icon: ImageVector,
    title: String,
    description: String,
    isGranted: Boolean,
    grantedLabel: String,
    unassignedLabel: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted) {
                MotoGreen.copy(alpha = 0.08f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            }
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isGranted) MotoGreen.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.12f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isGranted) MotoGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isGranted) MotoGreen else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isGranted) MotoGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            )
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isGranted) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = MotoGreen,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                            }
                            Text(
                                text = if (isGranted) grantedLabel else unassignedLabel,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isGranted) MotoGreen else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
