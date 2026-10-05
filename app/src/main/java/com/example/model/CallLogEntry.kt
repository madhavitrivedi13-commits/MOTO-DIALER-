package com.example.model

data class CallLogEntry(
    val id: String,
    val contactId: String? = null,
    val name: String,
    val phoneNumber: String,
    val type: CallType,
    val timestamp: Long,
    val durationSeconds: Int = 0,
    val isBlocked: Boolean = false
) {
    val formattedDuration: String
        get() {
            if (type == CallType.BLOCKED || isBlocked) return "Blocked"
            if (durationSeconds <= 0) return "Not connected"
            val mins = durationSeconds / 60
            val secs = durationSeconds % 60
            return if (mins > 0) "${mins}m ${secs}s" else "${secs}s"
        }
}
