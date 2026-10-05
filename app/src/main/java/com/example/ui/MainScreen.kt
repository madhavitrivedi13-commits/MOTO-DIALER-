package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneCallback
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.example.ui.theme.MotoRed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.CallType
import com.example.telecom.RealPhoneManager
import com.example.ui.components.AddEditContactSheet
import com.example.ui.components.CallLogDetailSheet
import com.example.ui.components.ContactDetailSheet
import com.example.ui.components.DialerBottomBar
import com.example.ui.components.InCallScreen
import com.example.ui.components.IncomingCallScreen
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.liquidPress
import com.example.ui.screens.ContactsScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.KeypadScreen
import com.example.ui.screens.RecentsScreen
import com.example.ui.theme.MotoCyan
import com.example.ui.theme.MotoDialerTheme
import com.example.ui.theme.MotoGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: DialerViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val keypadInput by viewModel.keypadInput.collectAsStateWithLifecycle()
    val t9Suggestions by viewModel.t9Suggestions.collectAsStateWithLifecycle()
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val callLogs by viewModel.callLogs.collectAsStateWithLifecycle()
    val searchQuery by viewModel.contactsSearchQuery.collectAsStateWithLifecycle()
    val recentsFilter by viewModel.recentsFilter.collectAsStateWithLifecycle()
    val activeCall by viewModel.activeCall.collectAsStateWithLifecycle()
    val incomingCall by viewModel.incomingCall.collectAsStateWithLifecycle()
    val selectedContact by viewModel.selectedContactDetail.collectAsStateWithLifecycle()
    val selectedCallLog by viewModel.selectedCallLogDetail.collectAsStateWithLifecycle()
    val isAddContactOpen by viewModel.isAddContactOpen.collectAsStateWithLifecycle()
    val contactToEdit by viewModel.contactToEdit.collectAsStateWithLifecycle()
    val isDarkModeState by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val isDefaultDialer by viewModel.isDefaultDialer.collectAsStateWithLifecycle()
    val isRealCellularCallEnabled by viewModel.isRealCellularCallEnabled.collectAsStateWithLifecycle()
    val blockedAlert by viewModel.blockedAlert.collectAsStateWithLifecycle()

    val systemDark = isSystemInDarkTheme()
    val effectiveDark = isDarkModeState ?: systemDark

    var showFirstTimeSetupSheet by remember {
        mutableStateOf(RealPhoneManager.isFirstLaunch(context))
    }
    var hasContactsPerm by remember {
        mutableStateOf(RealPhoneManager.hasContactsPermission(context))
    }
    var hasCallPerm by remember {
        mutableStateOf(RealPhoneManager.hasCallPermission(context))
    }

    val defaultDialerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.updateDefaultDialerStatus(context)
    }

    val corePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        hasContactsPerm = permissionsMap[Manifest.permission.READ_CONTACTS] == true || RealPhoneManager.hasContactsPermission(context)
        hasCallPerm = permissionsMap[Manifest.permission.CALL_PHONE] == true || RealPhoneManager.hasCallPermission(context)
        if (hasContactsPerm) {
            viewModel.syncDeviceContacts(context)
        }
        // Next, prompt to set Default Phone App if not yet default
        if (!RealPhoneManager.isDefaultDialer(context)) {
            val intent = RealPhoneManager.createDefaultDialerRequestIntent(context)
            if (intent != null) {
                defaultDialerLauncher.launch(intent)
            }
        }
        RealPhoneManager.setFirstLaunchCompleted(context)
        viewModel.updateDefaultDialerStatus(context)
        showFirstTimeSetupSheet = false
    }

    LaunchedEffect(Unit) {
        viewModel.updateDefaultDialerStatus(context)
        hasContactsPerm = RealPhoneManager.hasContactsPermission(context)
        hasCallPerm = RealPhoneManager.hasCallPermission(context)
        if (hasContactsPerm) {
            viewModel.syncDeviceContacts(context)
        }
    }

    val handleCallAction: (String, String?) -> Unit = { number, name ->
        viewModel.initiateCall(context, number, name)
    }

    MotoDialerTheme(darkTheme = effectiveDark) {
        val missedCount = callLogs.count { it.type == CallType.MISSED }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "MOTO",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 19.sp,
                                            letterSpacing = 1.2.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "DIALER",
                                            fontWeight = FontWeight.Light,
                                            fontSize = 19.sp,
                                            letterSpacing = 1.2.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(MotoGreen)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isDefaultDialer) "Default Phone App" else "Real Cellular Dialer",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MotoGreen
                                        )
                                    }
                                }
                            }
                        },
                        actions = {
                            // Dark / Light Liquid Theme Toggle
                            IconButton(
                                onClick = { viewModel.toggleDarkMode(!effectiveDark) },
                                modifier = Modifier.testTag("theme_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (effectiveDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                    contentDescription = "Toggle Theme",
                                    tint = if (effectiveDark) MotoCyan else MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                        ),
                        modifier = Modifier.shadow(2.dp)
                    )
                },
                bottomBar = {
                    DialerBottomBar(
                        currentTab = currentTab,
                        missedCallsCount = missedCount,
                        onTabSelected = { viewModel.selectTab(it) }
                    )
                },
                containerColor = Color.Transparent
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Fluid Liquid Screen Navigation Transition
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = {
                            if (targetState.ordinal > initialState.ordinal) {
                                (slideInHorizontally { width -> width / 4 } + fadeIn(tween(250)))
                                    .togetherWith(slideOutHorizontally { width -> -width / 4 } + fadeOut(tween(200)))
                            } else {
                                (slideInHorizontally { width -> -width / 4 } + fadeIn(tween(250)))
                                    .togetherWith(slideOutHorizontally { width -> width / 4 } + fadeOut(tween(200)))
                            }
                        },
                        label = "tab_transition"
                    ) { tab ->
                        when (tab) {
                            DialerTab.KEYPAD -> {
                                KeypadScreen(
                                    input = keypadInput,
                                    t9Suggestions = t9Suggestions,
                                    onDigitClick = { viewModel.onDigitClick(it) },
                                    onZeroLongPress = { viewModel.onZeroLongPress() },
                                    onBackspaceClick = { viewModel.onBackspaceClick() },
                                    onBackspaceLongPress = { viewModel.onBackspaceLongPress() },
                                    onCallClick = { number ->
                                        handleCallAction(number, null)
                                    },
                                    onSelectContact = { contact ->
                                        handleCallAction(contact.phoneNumber, contact.name)
                                    },
                                    onAddNewContact = { number ->
                                        viewModel.openAddContact()
                                    }
                                )
                            }

                            DialerTab.FAVORITES -> {
                                FavoritesScreen(
                                    favorites = favorites,
                                    onCallContact = { contact ->
                                        handleCallAction(contact.phoneNumber, contact.name)
                                    },
                                    onContactDetails = { contact ->
                                        viewModel.openContactDetail(contact)
                                    },
                                    onAddFavoriteClick = {
                                        viewModel.selectTab(DialerTab.CONTACTS)
                                    }
                                )
                            }

                            DialerTab.RECENTS -> {
                                RecentsScreen(
                                    callLogs = callLogs,
                                    filter = recentsFilter,
                                    onFilterChange = { viewModel.setRecentsFilter(it) },
                                    onCallClick = { phone, name ->
                                        handleCallAction(phone, name)
                                    },
                                    onLogDetailClick = { log ->
                                        viewModel.openCallLogDetail(log)
                                    },
                                    onClearAllLogs = {
                                        viewModel.clearAllCallLogs()
                                    },
                                    onBlockNumber = { phone, name ->
                                        viewModel.blockNumber(phone, name)
                                    },
                                    onUnblockNumber = { phone ->
                                        viewModel.unblockNumber(phone)
                                    },
                                    isNumberBlocked = { phone ->
                                        viewModel.isNumberBlocked(phone)
                                    }
                                )
                            }

                            DialerTab.CONTACTS -> {
                                ContactsScreen(
                                    contacts = contacts,
                                    searchQuery = searchQuery,
                                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                    onContactClick = { contact ->
                                        viewModel.openContactDetail(contact)
                                    },
                                    onCallClick = { contact ->
                                        handleCallAction(contact.phoneNumber, contact.name)
                                    },
                                    onToggleFavorite = { contact ->
                                        viewModel.toggleFavorite(contact.id)
                                    },
                                    onAddContactClick = {
                                        viewModel.openAddContact()
                                    },
                                    onSyncDeviceContacts = {
                                        viewModel.syncDeviceContacts(context)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Contact Detail Bottom Sheet
            selectedContact?.let { contact ->
                ContactDetailSheet(
                    contact = contact,
                    onDismiss = { viewModel.closeContactDetail() },
                    onCall = { phone ->
                        handleCallAction(phone, contact.name)
                    },
                    onEdit = { contactToEditItem ->
                        viewModel.openAddContact(contactToEditItem)
                    },
                    onDelete = { id ->
                        viewModel.deleteContact(id)
                    },
                    onToggleFavorite = { id ->
                        viewModel.toggleFavorite(id)
                    }
                )
            }

            // Call Log Detail Bottom Sheet
            selectedCallLog?.let { log ->
                val isBlocked = viewModel.isNumberBlocked(log.phoneNumber) || log.isBlocked || log.type == CallType.BLOCKED
                CallLogDetailSheet(
                    entry = log,
                    isBlocked = isBlocked,
                    onDismiss = { viewModel.closeCallLogDetail() },
                    onCall = { phone, name ->
                        handleCallAction(phone, name)
                    },
                    onAddContact = { phone ->
                        viewModel.setKeypadInput(phone)
                        viewModel.openAddContact()
                    },
                    onDeleteLog = { id ->
                        viewModel.deleteCallLog(id)
                    },
                    onToggleBlock = { phone, name, shouldBlock ->
                        if (shouldBlock) {
                            viewModel.blockNumber(phone, name)
                        } else {
                            viewModel.unblockNumber(phone)
                        }
                    }
                )
            }

            // Add/Edit Contact Sheet
            if (isAddContactOpen) {
                AddEditContactSheet(
                    contactToEdit = contactToEdit,
                    prefilledPhone = if (contactToEdit == null) keypadInput else "",
                    onDismiss = { viewModel.closeAddContact() },
                    onSave = { name, phone, email, label, isFav, id ->
                        viewModel.saveContact(name, phone, email, label, isFav, id)
                    }
                )
            }

            // First-Time Setup Bottom Sheet (Contacts Permission, Calling Permission, Default App)
            if (showFirstTimeSetupSheet) {
                com.example.ui.components.FirstTimeSetupSheet(
                    hasContactsPermission = hasContactsPerm,
                    hasCallPermission = hasCallPerm,
                    isDefaultDialer = isDefaultDialer,
                    onRequestPermissionsAndDefault = {
                        corePermissionsLauncher.launch(RealPhoneManager.CORE_PERMISSIONS)
                    },
                    onDismiss = {
                        RealPhoneManager.setFirstLaunchCompleted(context)
                        showFirstTimeSetupSheet = false
                    }
                )
            }

            // Fullscreen Active In-Call Overlay
            AnimatedVisibility(
                visible = activeCall != null,
                enter = fadeIn(tween(250)),
                exit = fadeOut(tween(200))
            ) {
                activeCall?.let { session ->
                    InCallScreen(
                        session = session,
                        onEndCall = { viewModel.endCall() },
                        onToggleMute = { viewModel.toggleMute() },
                        onToggleSpeaker = { viewModel.toggleSpeaker() },
                        onToggleHold = { viewModel.toggleHold() },
                        onToggleKeypad = { viewModel.toggleInCallKeypad() },
                        onDtmfDigit = { /* DTMF audio */ }
                    )
                }
            }

            // Fullscreen Premium Liquid Incoming Call Overlay
            AnimatedVisibility(
                visible = incomingCall != null,
                enter = fadeIn(tween(300)),
                exit = fadeOut(tween(250))
            ) {
                incomingCall?.let { session ->
                    IncomingCallScreen(
                        session = session,
                        onAnswer = { viewModel.answerIncomingCall() },
                        onDecline = { viewModel.declineIncomingCall() },
                        onQuickMessage = { viewModel.declineIncomingCall() }
                    )
                }
            }

            // Blocked Call Floating Banner Notification
            blockedAlert?.let { alertMessage ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, MotoRed.copy(alpha = 0.5f)),
                        shadowElevation = 12.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("blocked_call_alert_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MotoRed.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Shield,
                                    contentDescription = null,
                                    tint = MotoRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Call Blocked",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MotoRed
                                )
                                Text(
                                    text = alertMessage,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { viewModel.clearBlockedAlert() }) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Filled.Close,
                                    contentDescription = "Dismiss",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
