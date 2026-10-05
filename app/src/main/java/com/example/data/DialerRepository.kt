package com.example.data

import com.example.model.CallLogEntry
import com.example.model.CallType
import com.example.model.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

object DialerRepository {

    private val initialContacts = listOf(
        Contact(
            id = "c1",
            name = "Mom",
            phoneNumber = "+1 (555) 234-5678",
            email = "mom@family.com",
            label = "Mobile",
            isFavorite = true,
            avatarColorIndex = 0
        ),
        Contact(
            id = "c2",
            name = "Alex Rivera",
            phoneNumber = "+1 (555) 345-6789",
            email = "alex.rivera@techstudio.io",
            label = "Mobile",
            isFavorite = true,
            avatarColorIndex = 1
        ),
        Contact(
            id = "c3",
            name = "Dad",
            phoneNumber = "+1 (555) 876-5432",
            email = "dad@family.com",
            label = "Home",
            isFavorite = true,
            avatarColorIndex = 2
        ),
        Contact(
            id = "c4",
            name = "Sarah Jenkins",
            phoneNumber = "+1 (555) 456-7890",
            email = "sarah.j@designco.com",
            label = "Work",
            isFavorite = true,
            avatarColorIndex = 3
        ),
        Contact(
            id = "c5",
            name = "David Chen",
            phoneNumber = "+1 (555) 567-8901",
            email = "david.chen@clouddev.org",
            label = "Mobile",
            isFavorite = true,
            avatarColorIndex = 4
        ),
        Contact(
            id = "c6",
            name = "Emily Watson",
            phoneNumber = "+1 (555) 678-9012",
            email = "emily.w@mediaworks.net",
            label = "Mobile",
            isFavorite = false,
            avatarColorIndex = 5
        ),
        Contact(
            id = "c7",
            name = "Jessica Alba",
            phoneNumber = "+1 (555) 789-0123",
            email = "jessica@creativepulse.io",
            label = "Mobile",
            isFavorite = false,
            avatarColorIndex = 6
        ),
        Contact(
            id = "c8",
            name = "Lucas Brown",
            phoneNumber = "+1 (555) 890-1234",
            email = "lucas.b@fintechhub.com",
            label = "Work",
            isFavorite = false,
            avatarColorIndex = 7
        ),
        Contact(
            id = "c9",
            name = "Michael Scott",
            phoneNumber = "+1 (555) 901-2345",
            email = "m.scott@dunderpaper.com",
            label = "Work",
            isFavorite = false,
            avatarColorIndex = 0
        ),
        Contact(
            id = "c10",
            name = "Olivia Taylor",
            phoneNumber = "+1 (555) 012-3456",
            email = "olivia.taylor@artisan.co",
            label = "Mobile",
            isFavorite = true,
            avatarColorIndex = 1
        ),
        Contact(
            id = "c11",
            name = "Pizza Roma",
            phoneNumber = "+1 (555) 444-7662",
            email = "orders@pizzaroma.com",
            label = "Service",
            isFavorite = false,
            avatarColorIndex = 3
        ),
        Contact(
            id = "c12",
            name = "Tech Support Desk",
            phoneNumber = "+1 (800) 555-0199",
            email = "support@serviceline.org",
            label = "Toll Free",
            isFavorite = false,
            avatarColorIndex = 5
        )
    )

    private val now = System.currentTimeMillis()
    private val hour = 3600 * 1000L
    private val day = 24 * hour

    private val initialCallLogs = listOf(
        CallLogEntry(
            id = "l1",
            contactId = "c1",
            name = "Mom",
            phoneNumber = "+1 (555) 234-5678",
            type = CallType.INCOMING,
            timestamp = now - (25 * 60 * 1000L), // 25 mins ago
            durationSeconds = 142
        ),
        CallLogEntry(
            id = "l2",
            contactId = "c4",
            name = "Sarah Jenkins",
            phoneNumber = "+1 (555) 456-7890",
            type = CallType.MISSED,
            timestamp = now - (2 * hour), // 2 hours ago
            durationSeconds = 0
        ),
        CallLogEntry(
            id = "l3",
            contactId = "c2",
            name = "Alex Rivera",
            phoneNumber = "+1 (555) 345-6789",
            type = CallType.OUTGOING,
            timestamp = now - (5 * hour), // 5 hours ago
            durationSeconds = 310
        ),
        CallLogEntry(
            id = "l4",
            contactId = "c5",
            name = "David Chen",
            phoneNumber = "+1 (555) 567-8901",
            type = CallType.INCOMING,
            timestamp = now - day + (2 * hour), // Yesterday
            durationSeconds = 85
        ),
        CallLogEntry(
            id = "l5",
            contactId = null,
            name = "+1 (555) 987-6543",
            phoneNumber = "+1 (555) 987-6543",
            type = CallType.MISSED,
            timestamp = now - day - (3 * hour), // Yesterday
            durationSeconds = 0
        ),
        CallLogEntry(
            id = "l6",
            contactId = "c3",
            name = "Dad",
            phoneNumber = "+1 (555) 876-5432",
            type = CallType.OUTGOING,
            timestamp = now - (2 * day), // 2 days ago
            durationSeconds = 480
        ),
        CallLogEntry(
            id = "l7",
            contactId = "c11",
            name = "Pizza Roma",
            phoneNumber = "+1 (555) 444-7662",
            type = CallType.OUTGOING,
            timestamp = now - (3 * day),
            durationSeconds = 90
        )
    )

    private val _contacts = MutableStateFlow<List<Contact>>(initialContacts.sortedBy { it.name })
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private val _callLogs = MutableStateFlow<List<CallLogEntry>>(initialCallLogs.sortedByDescending { it.timestamp })
    val callLogs: StateFlow<List<CallLogEntry>> = _callLogs.asStateFlow()

    private val _blockedNumbers = MutableStateFlow<List<com.example.model.BlockedNumber>>(
        listOf(
            com.example.model.BlockedNumber(
                id = "b1",
                phoneNumber = "+1 (555) 987-6543",
                name = "+1 (555) 987-6543",
                blockedAt = System.currentTimeMillis() - 86400000L
            )
        )
    )
    val blockedNumbers: StateFlow<List<com.example.model.BlockedNumber>> = _blockedNumbers.asStateFlow()

    fun getFavorites(): StateFlow<List<Contact>> {
        val flow = MutableStateFlow(_contacts.value.filter { it.isFavorite })
        // sync on mutation
        return flow
    }

    fun addContact(contact: Contact) {
        val updated = (_contacts.value + contact).sortedBy { it.name }
        _contacts.value = updated
    }

    fun updateContact(contact: Contact) {
        val updated = _contacts.value.map { if (it.id == contact.id) contact else it }.sortedBy { it.name }
        _contacts.value = updated
    }

    fun deleteContact(contactId: String) {
        _contacts.value = _contacts.value.filter { it.id != contactId }
    }

    fun toggleFavorite(contactId: String) {
        _contacts.value = _contacts.value.map {
            if (it.id == contactId) it.copy(isFavorite = !it.isFavorite) else it
        }
    }

    fun importDeviceContacts(deviceContacts: List<Contact>) {
        if (deviceContacts.isEmpty()) return
        val existingNormalized = _contacts.value.map { it.phoneNumber.filter { ch -> ch.isDigit() } }.toSet()
        val newContacts = deviceContacts.filter { dc ->
            val digits = dc.phoneNumber.filter { it.isDigit() }
            digits.isNotEmpty() && !existingNormalized.contains(digits)
        }
        if (newContacts.isNotEmpty()) {
            _contacts.value = (_contacts.value + newContacts).sortedBy { it.name.lowercase() }
        }
    }

    fun addCallLog(
        phoneNumber: String,
        name: String? = null,
        type: CallType = CallType.OUTGOING,
        durationSeconds: Int = 0
    ) {
        val matchedContact = findContactByNumber(phoneNumber)
        val displayName = name ?: matchedContact?.name ?: phoneNumber
        val entry = CallLogEntry(
            id = UUID.randomUUID().toString(),
            contactId = matchedContact?.id,
            name = displayName,
            phoneNumber = phoneNumber,
            type = type,
            timestamp = System.currentTimeMillis(),
            durationSeconds = durationSeconds,
            isBlocked = type == CallType.BLOCKED || isNumberBlocked(phoneNumber)
        )
        _callLogs.value = listOf(entry) + _callLogs.value
    }

    fun normalizePhoneNumber(number: String): String {
        return number.filter { it.isDigit() }
    }

    fun isNumberBlocked(phoneNumber: String): Boolean {
        val clean = normalizePhoneNumber(phoneNumber)
        if (clean.isEmpty()) return false
        return _blockedNumbers.value.any { blocked ->
            val blockedClean = normalizePhoneNumber(blocked.phoneNumber)
            blockedClean.isNotEmpty() && (clean.endsWith(blockedClean) || blockedClean.endsWith(clean))
        }
    }

    fun blockNumber(phoneNumber: String, name: String? = null) {
        if (isNumberBlocked(phoneNumber)) return
        val matchedContact = findContactByNumber(phoneNumber)
        val displayName = name ?: matchedContact?.name ?: phoneNumber
        val newBlocked = com.example.model.BlockedNumber(
            id = UUID.randomUUID().toString(),
            phoneNumber = phoneNumber,
            name = displayName,
            blockedAt = System.currentTimeMillis()
        )
        _blockedNumbers.value = _blockedNumbers.value + newBlocked

        // Update all existing matching call logs to mark as blocked
        val clean = normalizePhoneNumber(phoneNumber)
        _callLogs.value = _callLogs.value.map { log ->
            val logClean = normalizePhoneNumber(log.phoneNumber)
            if (logClean.isNotEmpty() && (clean.endsWith(logClean) || logClean.endsWith(clean))) {
                log.copy(isBlocked = true)
            } else {
                log
            }
        }
    }

    fun unblockNumber(phoneNumber: String) {
        val clean = normalizePhoneNumber(phoneNumber)
        _blockedNumbers.value = _blockedNumbers.value.filter { blocked ->
            val blockedClean = normalizePhoneNumber(blocked.phoneNumber)
            !(clean.endsWith(blockedClean) || blockedClean.endsWith(clean))
        }

        // Update matching call logs to unblock
        _callLogs.value = _callLogs.value.map { log ->
            val logClean = normalizePhoneNumber(log.phoneNumber)
            if (logClean.isNotEmpty() && (clean.endsWith(logClean) || logClean.endsWith(clean))) {
                log.copy(isBlocked = false)
            } else {
                log
            }
        }
    }

    fun deleteCallLog(id: String) {
        _callLogs.value = _callLogs.value.filter { it.id != id }
    }

    fun clearAllCallLogs() {
        _callLogs.value = emptyList()
    }

    fun findContactByNumber(number: String): Contact? {
        val cleanNumber = number.filter { it.isDigit() }
        if (cleanNumber.isEmpty()) return null
        return _contacts.value.firstOrNull { contact ->
            val contactClean = contact.phoneNumber.filter { it.isDigit() }
            contactClean.endsWith(cleanNumber) || cleanNumber.endsWith(contactClean)
        }
    }

    /**
     * T9 Search: Converts contact names into keypad digits and matches with typed digits.
     * Also matches phone number digits.
     */
    fun searchContactsT9(query: String): List<Contact> {
        val cleanDigits = query.filter { it.isDigit() }
        if (cleanDigits.isEmpty()) return emptyList()

        return _contacts.value.filter { contact ->
            val phoneDigits = contact.phoneNumber.filter { it.isDigit() }
            val nameAsT9 = nameToT9(contact.name)

            phoneDigits.contains(cleanDigits) || nameAsT9.contains(cleanDigits)
        }
    }

    fun searchContactsText(query: String): List<Contact> {
        val clean = query.trim().lowercase()
        if (clean.isEmpty()) return _contacts.value

        return _contacts.value.filter {
            it.name.lowercase().contains(clean) ||
            it.phoneNumber.filter { ch -> ch.isDigit() }.contains(clean.filter { ch -> ch.isDigit() }) ||
            it.email.lowercase().contains(clean)
        }
    }

    private fun nameToT9(name: String): String {
        return buildString {
            for (ch in name.lowercase()) {
                val digit = when (ch) {
                    'a', 'b', 'c' -> '2'
                    'd', 'e', 'f' -> '3'
                    'g', 'h', 'i' -> '4'
                    'j', 'k', 'l' -> '5'
                    'm', 'n', 'o' -> '6'
                    'p', 'q', 'r', 's' -> '7'
                    't', 'u', 'v' -> '8'
                    'w', 'x', 'y', 'z' -> '9'
                    '0' -> '0'
                    '1' -> '1'
                    else -> ' '
                }
                append(digit)
            }
        }.replace(" ", "")
    }
}
