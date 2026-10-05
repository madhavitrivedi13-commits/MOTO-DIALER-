package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.model.Contact
import com.example.ui.components.AlphabetIndexRail
import com.example.ui.components.ContactAvatar
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.liquidGlassBackground
import com.example.ui.components.liquidPress
import com.example.ui.theme.MotoGreen
import com.example.ui.theme.MotoOrange
import kotlinx.coroutines.launch

@Composable
fun ContactsScreen(
    contacts: List<Contact>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onContactClick: (Contact) -> Unit,
    onCallClick: (Contact) -> Unit,
    onToggleFavorite: (Contact) -> Unit,
    onAddContactClick: () -> Unit,
    onSyncDeviceContacts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val isDark = isSystemInDarkTheme()

    var hasContactsPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasContactsPermission = isGranted
        if (isGranted) {
            onSyncDeviceContacts()
        }
    }

    LaunchedEffect(hasContactsPermission) {
        if (hasContactsPermission) {
            onSyncDeviceContacts()
        }
    }

    val grouped = remember(contacts) {
        contacts.groupBy { contact ->
            val firstChar = contact.name.trim().take(1).uppercase().firstOrNull() ?: '#'
            if (firstChar in 'A'..'Z') firstChar else '#'
        }.toSortedMap { a, b ->
            if (a == '#') 1 else if (b == '#') -1 else a.compareTo(b)
        }
    }

    val availableLetters = remember(grouped) { grouped.keys.toSet() }

    val letterToIndexMap = remember(grouped) {
        val map = mutableMapOf<Char, Int>()
        var currentIndex = 0
        grouped.forEach { (letter, groupContacts) ->
            map[letter] = currentIndex
            currentIndex += 1 + groupContacts.size
        }
        map
    }

    val activeLetter by remember {
        derivedStateOf {
            val firstVisibleIndex = listState.firstVisibleItemIndex
            letterToIndexMap.entries
                .sortedBy { it.value }
                .lastOrNull { it.value <= firstVisibleIndex }
                ?.key ?: grouped.keys.firstOrNull()
        }
    }

    Scaffold(
        floatingActionButton = {
            val fabInteraction = remember { MutableInteractionSource() }
            FloatingActionButton(
                onClick = onAddContactClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier
                    .liquidPress(fabInteraction)
                    .shadow(12.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary)
                    .testTag("fab_add_contact")
            ) {
                Icon(
                    imageVector = Icons.Filled.PersonAdd,
                    contentDescription = "Add Contact"
                )
            }
        },
        containerColor = Color.Transparent,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header & Count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Contacts",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${contacts.size} contacts available",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Sync device contacts liquid button
                val syncInteraction = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .liquidPress(syncInteraction)
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                        .clickable(
                            interactionSource = syncInteraction,
                            indication = null,
                            onClick = {
                                if (hasContactsPermission) {
                                    onSyncDeviceContacts()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                                }
                            }
                        )
                        .testTag("sync_device_contacts_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Sync,
                        contentDescription = "Sync Device Contacts",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Liquid Search Bar with subtle glass refraction
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .liquidGlassBackground(RoundedCornerShape(24.dp), isDark = isDark, elevation = 2.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            "Search name or phone number…",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contacts_search_field")
                )
            }

            // Optional Device Contacts Permission Prompt Banner
            if (!hasContactsPermission) {
                LiquidGlassCard(
                    shape = RoundedCornerShape(18.dp),
                    onClick = { permissionLauncher.launch(Manifest.permission.READ_CONTACTS) },
                    elevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("sync_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContactPhone,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Load Device Contacts",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tap to grant permission and import your phone contacts.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Main Content: List View + Alphabetical Fast-Scroller Rail
            if (contacts.isEmpty()) {
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
                        Text(
                            text = if (searchQuery.isNotBlank()) "No Matching Contacts" else "No Contacts",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (searchQuery.isNotBlank())
                                "No contacts matching \"$searchQuery\""
                            else
                                "Tap + to add a contact or sync from your device.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 16.dp, end = 32.dp)
                            .testTag("contacts_lazy_list")
                    ) {
                        grouped.forEach { (initial, contactsInGroup) ->
                            item(key = "header_$initial") {
                                Surface(
                                    color = Color.Transparent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp, bottom = 4.dp)
                                ) {
                                    Text(
                                        text = initial.toString(),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            itemsIndexed(contactsInGroup, key = { _, c -> c.id }) { _, contact ->
                                LiquidContactRowItem(
                                    contact = contact,
                                    onContactClick = { onContactClick(contact) },
                                    onCallClick = { onCallClick(contact) },
                                    onToggleFavorite = { onToggleFavorite(contact) }
                                )
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(start = 58.dp)
                                )
                            }
                        }
                    }

                    // Alphabetical Index Rail (Right side)
                    if (searchQuery.isEmpty()) {
                        AlphabetIndexRail(
                            availableLetters = availableLetters,
                            activeLetter = activeLetter,
                            onLetterSelected = { char ->
                                val targetIndex = letterToIndexMap[char]
                                    ?: letterToIndexMap.entries
                                        .sortedBy { it.key }
                                        .firstOrNull { it.key >= char }
                                        ?.value

                                if (targetIndex != null) {
                                    coroutineScope.launch {
                                        listState.scrollToItem(targetIndex)
                                    }
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 4.dp)
                                .testTag("alphabet_index_rail")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LiquidContactRowItem(
    contact: Contact,
    onContactClick: () -> Unit,
    onCallClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val rowInteraction = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .liquidPress(rowInteraction, pressedScale = 0.98f)
            .fillMaxWidth()
            .clickable(
                interactionSource = rowInteraction,
                indication = null,
                onClick = onContactClick
            )
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .testTag("contact_item_${contact.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactAvatar(
            name = contact.name,
            initials = contact.initials,
            avatarColorIndex = contact.avatarColorIndex,
            size = 46.dp
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${contact.label} • ${contact.phoneNumber}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Favorite Toggle Icon
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier
                .size(36.dp)
                .testTag("fav_toggle_${contact.id}")
        ) {
            Icon(
                imageVector = if (contact.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = if (contact.isFavorite) "Remove from favorites" else "Add to favorites",
                tint = if (contact.isFavorite) MotoOrange else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier.size(20.dp)
            )
        }

        // Quick Call Liquid Button
        val callInteraction = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .liquidPress(callInteraction)
                .size(40.dp)
                .clip(CircleShape)
                .background(MotoGreen.copy(alpha = 0.15f))
                .clickable(
                    interactionSource = callInteraction,
                    indication = null,
                    onClick = onCallClick
                )
                .testTag("call_contact_${contact.id}"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Phone,
                contentDescription = "Call ${contact.name}",
                tint = MotoGreen,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}
