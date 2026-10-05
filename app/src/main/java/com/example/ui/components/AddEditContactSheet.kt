package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.model.Contact
import com.example.ui.theme.MotoOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditContactSheet(
    contactToEdit: Contact? = null,
    prefilledPhone: String = "",
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, email: String, label: String, isFavorite: Boolean, id: String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf(contactToEdit?.name ?: "") }
    var phoneNumber by remember { mutableStateOf(contactToEdit?.phoneNumber ?: prefilledPhone) }
    var email by remember { mutableStateOf(contactToEdit?.email ?: "") }
    var label by remember { mutableStateOf(contactToEdit?.label ?: "Mobile") }
    var isFavorite by remember { mutableStateOf(contactToEdit?.isFavorite ?: false) }

    var isNameError by remember { mutableStateOf(false) }
    var isPhoneError by remember { mutableStateOf(false) }

    val labels = listOf("Mobile", "Work", "Home", "Other")

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
                .padding(bottom = 36.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (contactToEdit != null) "Edit Contact" else "New Contact",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name Field
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (it.isNotBlank()) isNameError = false
                },
                label = { Text("Full Name *") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Person, contentDescription = null)
                },
                isError = isNameError,
                supportingText = { if (isNameError) Text("Name cannot be empty") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("contact_input_name")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Phone Field
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    phoneNumber = it
                    if (it.isNotBlank()) isPhoneError = false
                },
                label = { Text("Phone Number *") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Phone, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = isPhoneError,
                supportingText = { if (isPhoneError) Text("Phone number cannot be empty") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("contact_input_phone")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email (Optional)") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Email, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("contact_input_email")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Label Selector Chips
            Text(
                text = "Label",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                labels.forEach { lbl ->
                    FilterChip(
                        selected = label == lbl,
                        onClick = { label = lbl },
                        label = { Text(lbl) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Favorite Toggle Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = null,
                        tint = if (isFavorite) MotoOrange else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Add to Favorites",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                Switch(
                    checked = isFavorite,
                    onCheckedChange = { isFavorite = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("contact_favorite_switch")
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Buttons (Cancel / Save)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        val validName = name.isNotBlank()
                        val validPhone = phoneNumber.isNotBlank()
                        isNameError = !validName
                        isPhoneError = !validPhone

                        if (validName && validPhone) {
                            onSave(name, phoneNumber, email, label, isFavorite, contactToEdit?.id)
                        }
                    },
                    modifier = Modifier.weight(1f).testTag("save_contact_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Save Contact", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
