package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object WhatsAppHelper {

    /**
     * Sanitizes phone number and opens WhatsApp direct chat via deep link.
     * Uses https://wa.me/<number> which seamlessly routes to WhatsApp application if installed,
     * or WhatsApp web/play store if not.
     */
    fun openWhatsAppChat(context: Context, phoneNumber: String) {
        val sanitizedNumber = phoneNumber.filter { it.isDigit() }
        if (sanitizedNumber.isEmpty()) {
            Toast.makeText(context, "Invalid phone number for WhatsApp", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Prefer direct WhatsApp package intent if available
            val directIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("whatsapp://send?phone=$sanitizedNumber")
                setPackage("com.whatsapp")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            if (directIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(directIntent)
                return
            }
        } catch (_: Exception) {
            // Fall back to universal link
        }

        try {
            // Universal web and deep-link fallback
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://api.whatsapp.com/send?phone=$sanitizedNumber")
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(webIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open WhatsApp: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}
