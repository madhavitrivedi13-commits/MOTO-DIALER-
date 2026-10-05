package com.example.telecom

import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.telecom.TelecomManager
import androidx.core.content.ContextCompat

object RealPhoneManager {

    private const val PREFS_NAME = "moto_dialer_prefs"
    private const val KEY_FIRST_LAUNCH_DONE = "key_first_launch_done"

    val CORE_PERMISSIONS = arrayOf(
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALL_LOG
    )

    fun isFirstLaunch(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return !prefs.getBoolean(KEY_FIRST_LAUNCH_DONE, false)
    }

    fun setFirstLaunchCompleted(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH_DONE, true).apply()
    }

    /**
     * Checks if the app is currently granted CALL_PHONE runtime permission.
     */
    fun hasCallPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks if the app is currently granted READ_CONTACTS runtime permission.
     */
    fun hasContactsPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks if all primary core permissions are granted.
     */
    fun hasAllRequiredPermissions(context: Context): Boolean {
        return hasCallPermission(context) && hasContactsPermission(context)
    }

    /**
     * Checks if the app is currently set as the Android system's default phone dialer.
     */
    fun isDefaultDialer(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(Context.ROLE_SERVICE) as? RoleManager
            roleManager?.isRoleHeld(RoleManager.ROLE_DIALER) == true
        } else {
            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
            telecomManager?.defaultDialerPackage == context.packageName
        }
    }

    /**
     * Creates the Intent to prompt the user to set this app as their Default Phone App.
     */
    fun createDefaultDialerRequestIntent(context: Context): Intent? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(Context.ROLE_SERVICE) as? RoleManager
            roleManager?.createRequestRoleIntent(RoleManager.ROLE_DIALER)
        } else {
            Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, context.packageName)
            }
        }
    }

    /**
     * Makes a real cellular telephone call.
     * If CALL_PHONE permission is granted, initiates direct call via ACTION_CALL.
     * Otherwise falls back safely to ACTION_DIAL so the user can complete the call with one tap.
     */
    fun makePhoneCall(
        context: Context,
        phoneNumber: String,
        forceActionDial: Boolean = false
    ): Boolean {
        val sanitizedNumber = phoneNumber.replace(Regex("[^0-9+*#]"), "")
        val uri = Uri.parse("tel:${Uri.encode(sanitizedNumber)}")

        return if (!forceActionDial && hasCallPermission(context)) {
            try {
                val callIntent = Intent(Intent.ACTION_CALL, uri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(callIntent)
                true
            } catch (e: Exception) {
                // Fallback to ACTION_DIAL if security exception occurs
                openDialer(context, uri)
                false
            }
        } else {
            openDialer(context, uri)
            false
        }
    }

    private fun openDialer(context: Context, uri: Uri) {
        val dialIntent = Intent(Intent.ACTION_DIAL, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(dialIntent)
    }
}
