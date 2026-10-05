package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DialerRepository
import com.example.model.CallLogEntry
import com.example.model.CallType
import com.example.model.Contact
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

enum class DialerTab {
    FAVORITES,
    RECENTS,
    CONTACTS,
    KEYPAD
}

enum class RecentsFilter {
    ALL,
    MISSED,
    BLOCKED
}

enum class CallStatus {
    DIALING,
    CONNECTED,
    ENDED
}

data class ActiveCallSession(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phoneNumber: String,
    val avatarColorIndex: Int = 0,
    val status: CallStatus = CallStatus.DIALING,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = false,
    val isOnHold: Boolean = false,
    val showInCallKeypad: Boolean = false
)

class DialerViewModel : ViewModel() {

    private val repository = DialerRepository

    private val _currentTab = MutableStateFlow(DialerTab.KEYPAD)
    val currentTab: StateFlow<DialerTab> = _currentTab.asStateFlow()

    private val _keypadInput = MutableStateFlow("")
    val keypadInput: StateFlow<String> = _keypadInput.asStateFlow()

    private val _contactsSearchQuery = MutableStateFlow("")
    val contactsSearchQuery: StateFlow<String> = _contactsSearchQuery.asStateFlow()

    private val _recentsFilter = MutableStateFlow(RecentsFilter.ALL)
    val recentsFilter: StateFlow<RecentsFilter> = _recentsFilter.asStateFlow()

    val contacts: StateFlow<List<Contact>> = combine(
        repository.contacts,
        _contactsSearchQuery
    ) { allContacts, query ->
        if (query.isBlank()) allContacts else repository.searchContactsText(query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<Contact>> = repository.contacts
        .combine(MutableStateFlow(Unit)) { contacts, _ ->
            contacts.filter { it.isFavorite }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val callLogs: StateFlow<List<CallLogEntry>> = combine(
        repository.callLogs,
        _recentsFilter
    ) { logs, filter ->
        when (filter) {
            RecentsFilter.ALL -> logs
            RecentsFilter.MISSED -> logs.filter { it.type == CallType.MISSED }
            RecentsFilter.BLOCKED -> logs.filter { it.type == CallType.BLOCKED || it.isBlocked }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val t9Suggestions: StateFlow<List<Contact>> = _keypadInput
        .combine(repository.contacts) { input, _ ->
            if (input.isBlank()) emptyList() else repository.searchContactsT9(input)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeCall = MutableStateFlow<ActiveCallSession?>(null)
    val activeCall: StateFlow<ActiveCallSession?> = _activeCall.asStateFlow()

    private val _incomingCall = MutableStateFlow<com.example.ui.components.IncomingCallSession?>(null)
    val incomingCall: StateFlow<com.example.ui.components.IncomingCallSession?> = _incomingCall.asStateFlow()

    private val _selectedContactDetail = MutableStateFlow<Contact?>(null)
    val selectedContactDetail: StateFlow<Contact?> = _selectedContactDetail.asStateFlow()

    private val _selectedCallLogDetail = MutableStateFlow<CallLogEntry?>(null)
    val selectedCallLogDetail: StateFlow<CallLogEntry?> = _selectedCallLogDetail.asStateFlow()

    private val _isAddContactOpen = MutableStateFlow(false)
    val isAddContactOpen: StateFlow<Boolean> = _isAddContactOpen.asStateFlow()

    private val _contactToEdit = MutableStateFlow<Contact?>(null)
    val contactToEdit: StateFlow<Contact?> = _contactToEdit.asStateFlow()

    private val _isDarkMode = MutableStateFlow<Boolean?>(null) // null = follow system
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    private val _isDefaultDialer = MutableStateFlow(false)
    val isDefaultDialer: StateFlow<Boolean> = _isDefaultDialer.asStateFlow()

    private val _isRealCellularCallEnabled = MutableStateFlow(true)
    val isRealCellularCallEnabled: StateFlow<Boolean> = _isRealCellularCallEnabled.asStateFlow()

    private var callTimerJob: Job? = null

    fun selectTab(tab: DialerTab) {
        _currentTab.value = tab
    }

    fun toggleDarkMode(enableDark: Boolean?) {
        _isDarkMode.value = enableDark
    }

    // Keypad actions
    fun onDigitClick(digit: String) {
        if (_keypadInput.value.length < 24) {
            _keypadInput.value += digit
        }
    }

    fun onZeroLongPress() {
        if (_keypadInput.value.isEmpty()) {
            _keypadInput.value = "+"
        } else {
            _keypadInput.value += "+"
        }
    }

    fun onBackspaceClick() {
        if (_keypadInput.value.isNotEmpty()) {
            _keypadInput.value = _keypadInput.value.dropLast(1)
        }
    }

    fun onBackspaceLongPress() {
        _keypadInput.value = ""
    }

    fun setKeypadInput(text: String) {
        _keypadInput.value = text
    }

    fun clearKeypad() {
        _keypadInput.value = ""
    }

    fun updateDefaultDialerStatus(context: android.content.Context) {
        _isDefaultDialer.value = com.example.telecom.RealPhoneManager.isDefaultDialer(context)
    }

    fun toggleRealCellularCall(enabled: Boolean) {
        _isRealCellularCallEnabled.value = enabled
    }

    fun initiateCall(
        context: android.content.Context,
        phoneNumber: String,
        name: String? = null,
        avatarColorIndex: Int? = null,
        forceInAppOnly: Boolean = true
    ) {
        val trimmedNumber = phoneNumber.trim()
        if (trimmedNumber.isEmpty()) return

        val matchedContact = repository.findContactByNumber(trimmedNumber)
        val displayName = name ?: matchedContact?.name ?: trimmedNumber
        val colorIdx = avatarColorIndex ?: matchedContact?.avatarColorIndex ?: (kotlin.math.abs(displayName.hashCode()) % 8)

        // NEVER redirect to Google Phone! MOTO DIALER is the real phone dialer itself.
        // Directly display and handle the full-screen in-call screen natively.
        startCall(trimmedNumber, displayName, colorIdx)
    }

    // Calling logic
    fun startCall(phoneNumber: String, name: String? = null, avatarColorIndex: Int? = null) {
        val trimmedNumber = phoneNumber.trim()
        if (trimmedNumber.isEmpty()) return

        val matchedContact = repository.findContactByNumber(trimmedNumber)
        val displayName = name ?: matchedContact?.name ?: trimmedNumber
        val colorIdx = avatarColorIndex ?: matchedContact?.avatarColorIndex ?: (kotlin.math.abs(displayName.hashCode()) % 8)

        callTimerJob?.cancel()

        val session = ActiveCallSession(
            name = displayName,
            phoneNumber = trimmedNumber,
            avatarColorIndex = colorIdx,
            status = CallStatus.DIALING,
            durationSeconds = 0
        )
        _activeCall.value = session

        // Simulate network connection transition
        callTimerJob = viewModelScope.launch {
            delay(1800) // Dialing / ringing
            if (_activeCall.value != null && _activeCall.value?.status == CallStatus.DIALING) {
                _activeCall.value = _activeCall.value?.copy(status = CallStatus.CONNECTED)

                // Timer increments every second
                while (isActive && _activeCall.value?.status == CallStatus.CONNECTED) {
                    delay(1000)
                    _activeCall.value = _activeCall.value?.let { current ->
                        current.copy(durationSeconds = current.durationSeconds + 1)
                    }
                }
            }
        }
    }

    fun endCall() {
        val current = _activeCall.value ?: return
        callTimerJob?.cancel()
        _activeCall.value = current.copy(status = CallStatus.ENDED)

        // Save to call log
        repository.addCallLog(
            phoneNumber = current.phoneNumber,
            name = current.name,
            type = CallType.OUTGOING,
            durationSeconds = current.durationSeconds
        )

        viewModelScope.launch {
            delay(700)
            _activeCall.value = null
        }
    }

    fun toggleMute() {
        _activeCall.value = _activeCall.value?.let { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleSpeaker() {
        _activeCall.value = _activeCall.value?.let { it.copy(isSpeakerOn = !it.isSpeakerOn) }
    }

    fun toggleHold() {
        _activeCall.value = _activeCall.value?.let { it.copy(isOnHold = !it.isOnHold) }
    }

    fun toggleInCallKeypad() {
        _activeCall.value = _activeCall.value?.let { it.copy(showInCallKeypad = !it.showInCallKeypad) }
    }

    // Contacts
    fun setSearchQuery(query: String) {
        _contactsSearchQuery.value = query
    }

    fun openContactDetail(contact: Contact) {
        _selectedContactDetail.value = contact
    }

    fun closeContactDetail() {
        _selectedContactDetail.value = null
    }

    fun openAddContact(contactToEdit: Contact? = null) {
        _contactToEdit.value = contactToEdit
        _isAddContactOpen.value = true
    }

    fun closeAddContact() {
        _contactToEdit.value = null
        _isAddContactOpen.value = false
    }

    fun saveContact(
        name: String,
        phone: String,
        email: String,
        label: String,
        isFavorite: Boolean,
        id: String? = null
    ) {
        if (name.isBlank() || phone.isBlank()) return

        if (id != null) {
            val existing = repository.contacts.value.firstOrNull { it.id == id }
            val updated = Contact(
                id = id,
                name = name.trim(),
                phoneNumber = phone.trim(),
                email = email.trim(),
                label = label,
                isFavorite = isFavorite,
                avatarColorIndex = existing?.avatarColorIndex ?: (kotlin.math.abs(name.hashCode()) % 8)
            )
            repository.updateContact(updated)
            if (_selectedContactDetail.value?.id == id) {
                _selectedContactDetail.value = updated
            }
        } else {
            val newContact = Contact(
                id = UUID.randomUUID().toString(),
                name = name.trim(),
                phoneNumber = phone.trim(),
                email = email.trim(),
                label = label,
                isFavorite = isFavorite,
                avatarColorIndex = kotlin.math.abs(name.hashCode()) % 8
            )
            repository.addContact(newContact)
        }
        closeAddContact()
    }

    fun deleteContact(id: String) {
        repository.deleteContact(id)
        if (_selectedContactDetail.value?.id == id) {
            _selectedContactDetail.value = null
        }
    }

    fun toggleFavorite(id: String) {
        repository.toggleFavorite(id)
        if (_selectedContactDetail.value?.id == id) {
            _selectedContactDetail.value = _selectedContactDetail.value?.let {
                it.copy(isFavorite = !it.isFavorite)
            }
        }
    }

    // Recents
    fun setRecentsFilter(filter: RecentsFilter) {
        _recentsFilter.value = filter
    }

    fun openCallLogDetail(entry: CallLogEntry) {
        _selectedCallLogDetail.value = entry
    }

    fun closeCallLogDetail() {
        _selectedCallLogDetail.value = null
    }

    fun deleteCallLog(id: String) {
        repository.deleteCallLog(id)
        if (_selectedCallLogDetail.value?.id == id) {
            _selectedCallLogDetail.value = null
        }
    }

    fun clearAllCallLogs() {
        repository.clearAllCallLogs()
        _selectedCallLogDetail.value = null
    }

    fun syncDeviceContacts(context: android.content.Context) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val deviceContacts = com.example.data.DeviceContactsReader.fetchDeviceContacts(context)
            if (deviceContacts.isNotEmpty()) {
                repository.importDeviceContacts(deviceContacts)
            }
        }
    }

    val blockedNumbers: StateFlow<List<com.example.model.BlockedNumber>> = repository.blockedNumbers

    private val _blockedAlert = MutableStateFlow<String?>(null)
    val blockedAlert: StateFlow<String?> = _blockedAlert.asStateFlow()

    fun clearBlockedAlert() {
        _blockedAlert.value = null
    }

    fun isNumberBlocked(phoneNumber: String): Boolean {
        return repository.isNumberBlocked(phoneNumber)
    }

    fun blockNumber(phoneNumber: String, name: String? = null) {
        repository.blockNumber(phoneNumber, name)
        // Also update detail sheet if open
        if (_selectedCallLogDetail.value?.phoneNumber == phoneNumber) {
            _selectedCallLogDetail.value = _selectedCallLogDetail.value?.copy(isBlocked = true)
        }
    }

    fun unblockNumber(phoneNumber: String) {
        repository.unblockNumber(phoneNumber)
        // Also update detail sheet if open
        if (_selectedCallLogDetail.value?.phoneNumber == phoneNumber) {
            _selectedCallLogDetail.value = _selectedCallLogDetail.value?.copy(isBlocked = false)
        }
    }

    fun triggerSimulatedIncomingCall(name: String? = null, number: String? = null) {
        val randomContact = repository.contacts.value.shuffled().firstOrNull()
        val callerName = name ?: randomContact?.name ?: "Sarah Jenkins"
        val callerNumber = number ?: randomContact?.phoneNumber ?: "+1 (555) 456-7890"

        // Prevent incoming call if number is on the local blocklist!
        if (repository.isNumberBlocked(callerNumber)) {
            repository.addCallLog(
                phoneNumber = callerNumber,
                name = callerName,
                type = CallType.BLOCKED,
                durationSeconds = 0
            )
            _blockedAlert.value = "Incoming call from $callerName ($callerNumber) was automatically blocked by your blocklist."
            return
        }

        val colorIdx = randomContact?.avatarColorIndex ?: (kotlin.math.abs(callerName.hashCode()) % 8)

        _incomingCall.value = com.example.ui.components.IncomingCallSession(
            id = UUID.randomUUID().toString(),
            name = callerName,
            phoneNumber = callerNumber,
            avatarColorIndex = colorIdx
        )
    }

    fun answerIncomingCall() {
        val incoming = _incomingCall.value ?: return
        _incomingCall.value = null

        callTimerJob?.cancel()
        val session = ActiveCallSession(
            name = incoming.name,
            phoneNumber = incoming.phoneNumber,
            avatarColorIndex = incoming.avatarColorIndex,
            status = CallStatus.CONNECTED,
            durationSeconds = 0
        )
        _activeCall.value = session

        callTimerJob = viewModelScope.launch {
            while (isActive && _activeCall.value?.status == CallStatus.CONNECTED) {
                delay(1000)
                _activeCall.value = _activeCall.value?.let { current ->
                    current.copy(durationSeconds = current.durationSeconds + 1)
                }
            }
        }
    }

    fun declineIncomingCall() {
        val incoming = _incomingCall.value ?: return
        _incomingCall.value = null
        repository.addCallLog(
            phoneNumber = incoming.phoneNumber,
            name = incoming.name,
            type = CallType.MISSED,
            durationSeconds = 0
        )
    }
}
