package com.example.data

import android.content.Context
import android.provider.ContactsContract
import com.example.model.Contact
import java.util.UUID

object DeviceContactsReader {
    fun fetchDeviceContacts(context: Context): List<Contact> {
        val contactList = mutableListOf<Contact>()
        try {
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
            )

            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            )

            cursor?.use {
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val typeIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)
                val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)

                while (it.moveToNext()) {
                    val rawId = if (idIndex != -1) it.getString(idIndex) else UUID.randomUUID().toString()
                    val name = if (nameIndex != -1) it.getString(nameIndex) ?: "Unknown" else "Unknown"
                    val number = if (numberIndex != -1) it.getString(numberIndex) ?: "" else ""
                    val type = if (typeIndex != -1) it.getInt(typeIndex) else ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE

                    val label = when (type) {
                        ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> "Home"
                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> "Work"
                        else -> "Mobile"
                    }

                    if (number.isNotBlank()) {
                        contactList.add(
                            Contact(
                                id = "device_${rawId}_${UUID.randomUUID().toString().take(4)}",
                                name = name,
                                phoneNumber = number,
                                label = label,
                                avatarColorIndex = kotlin.math.abs(name.hashCode()) % 8
                            )
                        )
                    }
                }
            }
        } catch (e: SecurityException) {
            // Permission not granted
        } catch (e: Exception) {
            // Provider query error
        }
        return contactList
    }
}
