package com.example.security

import android.content.Context
import android.content.SharedPreferences
import com.example.logic.convertBurmeseToArabicDigits

class LockManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "ledger_security_prefs"
        private const val KEY_PIN = "app_pin"
        private const val KEY_LOCK_ENABLED = "app_lock_enabled"
        const val DEFAULT_PIN = "7777"
    }

    /**
     * Checks if the app lock feature is enabled.
     * Enabled by default.
     */
    fun isLockEnabled(): Boolean {
        return prefs.getBoolean(KEY_LOCK_ENABLED, true)
    }

    fun setLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_LOCK_ENABLED, enabled).apply()
    }

    /**
     * Gets the saved PIN (default "7777").
     */
    fun getPin(): String {
        return prefs.getString(KEY_PIN, DEFAULT_PIN) ?: DEFAULT_PIN
    }

    /**
     * Saves a new 4-digit PIN.
     */
    fun setPin(pin: String) {
        val normalized = convertBurmeseToArabicDigits(pin).trim()
        prefs.edit().putString(KEY_PIN, normalized).apply()
    }

    /**
     * Verifies if the entered PIN matches the saved PIN (supports Arabic and Burmese numerals).
     */
    fun verifyPin(enteredPin: String): Boolean {
        val normalized = convertBurmeseToArabicDigits(enteredPin).trim()
        return normalized == getPin()
    }

    /**
     * Resets PIN back to default 7777.
     */
    fun resetToDefault() {
        prefs.edit().putString(KEY_PIN, DEFAULT_PIN).apply()
    }
}
